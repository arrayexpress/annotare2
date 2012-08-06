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
import static com.google.common.collect.Sets.newHashSet;
import static uk.ac.ebi.fg.annotare2.magetab.parser.table.IdfTable.IdfTag.*;

/**
 * @author Olga Melnichuk
 */
public class IdfTable {

    protected enum IdfTag {
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
    }

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
            TableCell firstCell = table.getCell(i, 0);
            if (!firstCell.isEmpty() && !mappedRows.contains(i)) {
                table.getCell(i, 0).setError("unrecognized row specification");
            }
        }

        Map<String, IdfTermSource> termSources = newHashMap();
        for (IdfTermSource source : getTermSources()) {
            TableCell cell = source.getName();
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
            TableCell cell = term.getRef();
            if (!cell.isEmpty() && !termSources.containsKey(cell.getValue())) {
                cell.setError("Term source is not defined");
            }
        }
    }

    public TableCell getTitle() {
        return generalInfoList.getFirst(true).getTitle();
    }

    public TableCell getDescription() {
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
        protected IdfGeneralInfo create(TableCell[] cells) {
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
        public IdfTermSource create(TableCell[] cells) {
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
        public IdfPerson create(TableCell[] cells) {
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

        private final Map<IdfTag, Integer> tagToRow = newHashMap();

        private final List<IdfTag> tags;

        private Table table;

        private int maxColumn = 0;

        protected AbstractIdfList(IdfTag... tags) {
            this.tags = newArrayList(tags);
        }

        public void init(Table table) {
            for (int i = 0; i < table.getRowCount(); i++) {
                TableCell firstCell = table.getCell(i, 0);
                for (IdfTag tag : tags) {
                    if (tag.identifies(firstCell)) {
                        if (tagToRow.containsKey(tag)) {
                            firstCell.setError("Duplicated row");
                            return;
                        }
                        int rIndex = firstCell.getRow();
                        tagToRow.put(tag, rIndex);
                        maxColumn = Math.max(maxColumn, table.maxColumnIndex(rIndex));
                    }
                }
            }
            this.table = table;
        }

        public Collection<Integer> getMappedRows() {
            return Collections.unmodifiableCollection(tagToRow.values());
        }

        public List<T> getAll() {
            List<T> list = newArrayList();
            for (int j = 1; j < maxColumn; j++) {
                TableCell[] cells = new TableCell[tags.size()];
                int i = 0, zc = 0;
                for (IdfTag tag : tags) {
                    Integer rIndex = tagToRow.get(tag);
                    if (rIndex == null) {
                        rIndex = table.getRowCount();
                        table.getCell(rIndex, 0).setValue(tag.getTitle());
                    }
                    cells[i] = table.getCell(rIndex, j);
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

            TableCell[] cells = new TableCell[tags.size()];
            int i = 0;
            for (IdfTag tag : tags) {
                Integer rIndex = tagToRow.get(tag);
                if (rIndex == null) {
                    rIndex = table.getRowCount();
                    table.getCell(rIndex, 0).setValue(tag.getTitle());
                }
                cells[i] = table.getCell(rIndex, maxColumn);
                i++;
            }
            return create(cells);
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

        protected IdfTerm createTerm(TableCell name, TableCell accession, TableCell ref) {
            IdfTerm term = new IdfTerm();
            term.setName(name);
            term.setAccession(accession);
            term.setRef(ref);
            return term;
        }

        protected abstract T create(TableCell[] cells);
    }


    public List<TableCell> getErrors() {
        List<TableCell> errors = newArrayList();
        for (int i = 0; i < table.getRowCount(); i++) {
            for (int j = 0; j < table.getColumnCount(); j++) {
                TableCell cell = table.getCell(i, j);
                if (cell.getError() != null) {
                    errors.add(cell);
                }
            }
        }
        return errors;
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
