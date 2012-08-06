/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.magetab.parser.table;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import uk.ac.ebi.fg.annotare2.magetab.parser.table.om.IdfGeneralInfo;
import uk.ac.ebi.fg.annotare2.magetab.parser.table.om.IdfPerson;
import uk.ac.ebi.fg.annotare2.magetab.parser.table.om.IdfTerm;
import uk.ac.ebi.fg.annotare2.magetab.parser.table.om.IdfTermSource;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newHashSet;

/**
 * @author Olga Melnichuk
 */
public class IdfTable {

    /* protected enum IdfTag {
        INVESTIGATION_TITLE("Investigation Title"),
        EXPERIMENT_DESCRIPTION("Experiment Description"),
        PERSON_LAST_NAME("Person Last Name"),
        PERSON_FIRST_NAME("Person First Name"),
        PERSON_MID_INITIALS("Person Mid Initials"),
        PERSON_EMAIL("Person Email"),
        PERSON_ROLES("Person Roles"),
        PERSON_ROLES_TERM_ACCESSION_NUMBER("Person Roles Term Accession Number"),
        PERSON_ROLES_TERM_SOURCE_REF("Person Roles Term Source REF"),
        TERM_SOURCE_NAME("Term Source Name"),
        TERM_SOURCE_FILE("Term Source File"),
        TERM_SOURCE_VERSION("Term Source Version");

        private final String title;

        IdfTag(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public boolean identifies(TableCell cell) {
            return title.equals(cell.getValue());
        }
    }*/

    public static final IdfRow INVESTIGATION_TITLE = new IdfRow("Investigation Title");
    public static final IdfRow EXPERIMENT_DESCRIPTION = new IdfRow("Experiment Description");
    public static final IdfRow PERSON_LAST_NAME = new IdfRow("Person Last Name");
    public static final IdfRow PERSON_FIRST_NAME = new IdfRow("Person First Name");
    public static final IdfRow PERSON_MID_INITIALS = new IdfRow("Person Mid Initials");
    public static final IdfRow PERSON_EMAIL = new IdfRow("Person Email");
    public static final IdfRow PERSON_ROLES = new IdfRow("Person Roles");
    public static final IdfRow PERSON_ROLES_TERM_ACCESSION_NUMBER = new IdfRow("Person Roles Term Accession Number");
    public static final IdfRow PERSON_ROLES_TERM_SOURCE_REF = new IdfRow("Person Roles Term Source REF");
    public static final IdfRow TERM_SOURCE_NAME = new IdfRow("Term Source Name");
    public static final IdfRow TERM_SOURCE_FILE = new IdfRow("Term Source File");
    public static final IdfRow TERM_SOURCE_VERSION = new IdfRow("Term Source Version");


    private ContactList contactList = new ContactList();

    private TermSourceList termSourceList = new TermSourceList();

    private GeneralInfoList generalInfoList = new GeneralInfoList();

    private Table table;

    public IdfTable() {
        this(new Table());
    }

    @VisibleForTesting
    IdfTable(Table table) {
        this.table = table;

        contactList.init(table);
        termSourceList.init(table);
        generalInfoList.init(table);
    }

    public static IdfTable parse(InputStream in) throws IOException {
        IdfTable table = new IdfTable(new TsvParser().parse(in));
        table.check();
        return table;
    }

    public void check() {
        Set<Integer> mappedRows = newHashSet();
        mappedRows.addAll(contactList.getMappedRows());
        mappedRows.addAll(termSourceList.getMappedRows());
        mappedRows.addAll(generalInfoList.getMappedRows());

        for (int i = 0; i < table.getRowCount(); i++) {
            Table.Value value = table.getValueAt(i, 0);
            if (!value.isEmpty() && !mappedRows.contains(i)) {
                table.setErrorAt(i, 0, "unrecognized row specification");
            }
        }

        Map<String, IdfTermSource> termSources = newHashMap();
        for (IdfTermSource source : getTermSources()) {
            IdfCell cell = source.getName();
            if (cell.isEmpty()) {
                continue;
            }
            if (termSources.containsKey(cell.getValue())) {
                source.getName().setError("duplicated term sources: " + cell.getValue());
                continue;
            }
            termSources.put(cell.getValue(), source);
        }

        List<IdfTerm> terms = newArrayList();
        terms.addAll(Collections2.transform(contactList.getAll(), new Function<IdfPerson, IdfTerm>() {
            public IdfTerm apply(@Nullable IdfPerson input) {
                return input.getRoles();
            }
        }));

        for (IdfTerm term : terms) {
            IdfCell cell = term.getRef();
            if (!cell.isEmpty() && !termSources.containsKey(cell.getValue())) {
                cell.setError("Term source is not defined");
            }
        }
    }

    public IdfCell getTitle() {
        return generalInfoList.getFirst(true).getTitle();
    }

    public IdfCell getDescription() {
        return generalInfoList.getFirst(true).getDescription();
    }

    public List<IdfPerson> getContacts() {
        return contactList.getAll();
    }

    public List<IdfTermSource> getTermSources() {
        return termSourceList.getAll();
    }

    private static class GeneralInfoList extends AbstractIdfList<IdfGeneralInfo> {
        private GeneralInfoList() {
            super(INVESTIGATION_TITLE,
                    EXPERIMENT_DESCRIPTION);
        }

        @Override
        protected IdfGeneralInfo create(IdfCell[] cells) {
            IdfGeneralInfo generalInfo = new IdfGeneralInfo();
            generalInfo.setTitle(cells[0]);
            generalInfo.setDescription(cells[1]);
            return generalInfo;
        }
    }

    private static class TermSourceList extends AbstractIdfList<IdfTermSource> {
        protected TermSourceList() {
            super(TERM_SOURCE_NAME,
                    TERM_SOURCE_VERSION,
                    TERM_SOURCE_FILE);
        }

        @Override
        public IdfTermSource create(IdfCell[] cells) {
            IdfTermSource termSource = new IdfTermSource();
            termSource.setName(cells[0]);
            termSource.setVersion(cells[1]);
            termSource.setFile(cells[2]);
            return termSource;
        }
    }

    private static class ContactList extends AbstractIdfList<IdfPerson> {
        protected ContactList() {
            super(PERSON_FIRST_NAME,
                    PERSON_LAST_NAME,
                    PERSON_MID_INITIALS,
                    PERSON_EMAIL,
                    PERSON_ROLES,
                    PERSON_ROLES_TERM_ACCESSION_NUMBER,
                    PERSON_ROLES_TERM_SOURCE_REF);
        }

        @Override
        public IdfPerson create(IdfCell[] cells) {
            IdfPerson p = new IdfPerson();
            p.setFirstName(cells[0]);
            p.setLastName(cells[1]);
            p.setMidInitials(cells[2]);
            p.setEmail(cells[3]);
            p.setRoles(createTerm(cells[4], cells[5], cells[6]));
            return p;
        }
    }

    private abstract static class AbstractIdfList<T> {

        private final Map<IdfRow, Integer> rowToIndex = newHashMap();

        private final List<IdfRow> rows;

        private Table table;

        private int maxColumn = 0;

        protected AbstractIdfList(IdfRow... rows) {
            this.rows = newArrayList(rows);
        }

        public void init(Table table) {
            for (int i = 0; i < table.getRowCount(); i++) {
                Table.Value firstCell = table.getValueAt(i, 0);
                if (firstCell == null || firstCell.isEmpty()) {
                    continue;
                }
                for (IdfRow row : rows) {
                    if (row.identifies(firstCell.getValue())) {
                        if (rowToIndex.containsKey(row)) {
                            table.setErrorAt(i, 0, "Duplicated row");
                            return;
                        }
                        rowToIndex.put(row, i);
                        maxColumn = Math.max(maxColumn, table.lastColumnIndex(i));
                    }
                }
            }
            this.table = table;
        }

        public Collection<Integer> getMappedRows() {
            return Collections.unmodifiableCollection(rowToIndex.values());
        }

        public List<T> getAll() {
            List<T> list = newArrayList();
            for (int j = 1; j < maxColumn; j++) {
                IdfCell[] cells = new IdfCell[rows.size()];
                int i = 0, zc = 0;
                for (IdfRow row : rows) {
                    cells[i] = idfCell(row, j);
                    if (cells[i].isEmpty()) {
                        zc++;
                    }
                    i++;
                }
                if (zc < cells.length) {
                    list.add(create(cells));
                }
            }
            return list;
        }

        protected T createNew() {
            maxColumn++;

            IdfCell[] cells = new IdfCell[rows.size()];
            int i = 0;
            for (IdfRow row : rows) {
                cells[i] = idfCell(row, maxColumn);
                i++;
            }
            return create(cells);
        }

        private IdfCell idfCell(IdfRow row, int columnIndex) {
            return new IdfCell(row, columnIndex) {
                @Override
                protected Integer getRowIndex(IdfRow row) {
                    return rowToIndex.get(row);
                }

                @Override
                protected String getValue(IdfRow row, int columnIndex) {
                    Table.Value value = this.getValueAndError(row, columnIndex);
                    return value == null ? null : value.getValue();
                }

                @Override
                protected void setValue(IdfRow row, int columnIndex, String value) {
                    table.setValueAt(getOrCreateRow(row), columnIndex, value);
                }

                @Override
                protected String getError(IdfRow row, int columnIndex) {
                    Table.Value value = this.getValueAndError(row, columnIndex);
                    return value == null ? null : value.getError();
                }

                @Override
                protected void setError(IdfRow row, int columnIndex, String error) {
                   table.setErrorAt(getOrCreateRow(row), columnIndex, error);
                }

                private Table.Value getValueAndError(IdfRow row, Integer columnIndex) {
                    Integer rowIndex = rowToIndex.get(row);
                    return rowIndex == null ? null : table.getValueAt(rowIndex, columnIndex);
                }

                private Integer getOrCreateRow(IdfRow row) {
                    Integer rowIndex = rowToIndex.get(row);
                    if (rowIndex == null) {
                        rowIndex = table.addRow(Arrays.asList(row.getTag()));
                    }
                    return rowIndex;
                }
            };
        }

        public T getFirst(boolean forceCreate) {
            List<T> all = getAll();
            T t = null;
            if (all.isEmpty()) {
                t = forceCreate ? createNew() : t;
            } else {
                t = all.get(0);
            }
            return t;
        }

        protected IdfTerm createTerm(IdfCell name, IdfCell accession, IdfCell ref) {
            IdfTerm term = new IdfTerm();
            term.setName(name);
            term.setAccession(accession);
            term.setRef(ref);
            return term;
        }

        protected abstract T create(IdfCell[] cells);
    }

    public List<TableCell> getErrors() {
        return newArrayList(Collections2.filter(table.getCells(), new Predicate<TableCell>() {
            public boolean apply(@Nullable TableCell input) {
                return input.hasError();
            }
        }));
    }


    /*

    public TermSource lookup(TableCell ref) {
        if (ref == null || ref.isEmpty()) {
            return TermSource.DEFAULT;
        }
        TermSource source = termSources.get(ref.getValue());
        if (source == null) {
            addError(ref, "Term Source Reference not found");
            return TermSource.DEFAULT;
        }
        return source;
    }


    private class PersonTags {
        private final GroupedTags<IdfPerson> tags = new GroupedTags<IdfPerson>(
                new Function<Token[], IdfPerson>() {
                    public IdfPerson apply(@Nullable Token[] cells) {
                        checkNotNull(cells);
                        IdfPerson p = new IdfPerson();
                        p.setFirstName(cells[0]);
                        p.setLastName(cells[1]);
                        p.setMidInitials(cells[2]);
                        p.setEmail(cells[3]);
                        p.setRoles(createTerm(cells[4], cells[5], cells[6]));
                        return p;
                    }
                },
                PERSON_FIRST_NAME,
                PERSON_LAST_NAME,
                PERSON_MID_INITIALS,
                PERSON_EMAIL,
                PERSON_ROLES,
                PERSON_ROLES_TERM_ACCESSION_NUMBER,
                PERSON_ROLES_TERM_SOURCE_REF
        );

        private PersonTags(Multimap<IdfTag, Token> parsedRows) {
            tags.setAll(parsedRows);
        }

        public List<IdfPerson> parse() {
            return tags.getAll();
        }
    }

    private class TermSourceTags {
        private final GroupedTags<IdfTermSource> tags = new GroupedTags<IdfTermSource>(
                new Function<Token[], IdfTermSource>() {
                    public IdfTermSource apply(@Nullable Token[] cells) {
                        checkNotNull(cells);
                        IdfTermSource source = new IdfTermSource();
                        source.setName(cells[0]);
                        source.setVersion(cells[1]);
                        source.setFile(cells[2]);
                        return source;
                    }
                },
                TERM_SOURCE_NAME,
                TERM_SOURCE_VERSION,
                TERM_SOURCE_FILE
        );

        public TermSourceTags(Multimap<IdfTag, Token> parsedRows) {
            tags.setAll(parsedRows);
        }

        public List<IdfTermSource> parse() {
            return tags.getAll();
        }
    }

    private static class GroupedTags<T> {

        private final Map<IdfTag, List<Token>> map = new LinkedHashMap<IdfTag, List<Token>>();

        private final Function<Token[], T> func;

        private int size;

        public GroupedTags(Function<Token[], T> func, IdfTag... tags) {
            this.func = func;
            for (IdfTag tag : tags) {
                map.put(tag, new ArrayList<Token>());
            }
        }

        public void setAll(Multimap<IdfTag, Token> parsedRows) {
            Integer sz = null;
            for (IdfTag tag : map.keySet()) {
                Collection<Token> cells = parsedRows.get(tag);
                List<Token> list = newArrayList(cells);
                if (!list.isEmpty()) {
                    if (sz == null) {
                        sz = list.size();
                    } else if (sz != list.size()) {
                        throw new IllegalStateException("Inconsistent list size: expected " + sz + ", but got " + list.size());
                    }
                }
                map.put(tag, list);
            }
            if (sz != null) {
                size = sz;
            }
        }

        public List<T> getAll() {
            List<T> list = new ArrayList<T>();
            for (int i = 0; i < size; i++) {
                Token[] values = new Token[map.size()];
                int t = -1, empties = 0;

                for (IdfTag tag : map.keySet()) {
                    t++;
                    List<Token> cells = map.get(tag);
                    if (cells.isEmpty()) {
                        values[t] = null;
                        empties++;
                        continue;
                    }
                    values[t] = cells.get(i);
                    if (values[t].isEmpty()) {
                        empties++;
                    }
                }

                if (empties < values.length) {
                    list.add(func.apply(values));
                }
            }
            return list;
        }
    }*/
}
