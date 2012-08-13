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

package uk.ac.ebi.fg.annotare2.magetab.idf;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import uk.ac.ebi.fg.annotare2.magetab.base.Table;
import uk.ac.ebi.fg.annotare2.magetab.base.TableCell;
import uk.ac.ebi.fg.annotare2.magetab.base.TsvParser;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

/**
 * @author Olga Melnichuk
 */
public class Investigation {

    private static final IdfRow INVESTIGATION_TITLE = new IdfRow("Investigation Title");
    private static final IdfRow EXPERIMENT_DESCRIPTION = new IdfRow("Experiment Description");
    private static final IdfRow DATE_OF_EXPERIMENT = new IdfRow("Date of Experiment");
    private static final IdfRow DATE_OF_PUBLIC_RELEASE = new IdfRow("Public Release Date");
    private static final IdfRow PERSON_LAST_NAME = new IdfRow("Person Last Name");
    private static final IdfRow PERSON_FIRST_NAME = new IdfRow("Person First Name");
    private static final IdfRow PERSON_MID_INITIALS = new IdfRow("Person Mid Initials");
    private static final IdfRow PERSON_EMAIL = new IdfRow("Person Email");
    private static final IdfRow PERSON_ROLES = new IdfRow("Person Roles");
    private static final IdfRow PERSON_ROLES_TERM_ACCESSION_NUMBER = new IdfRow("Person Roles Term Accession Number");
    private static final IdfRow PERSON_ROLES_TERM_SOURCE_REF = new IdfRow("Person Roles Term Source REF");
    private static final IdfRow TERM_SOURCE_NAME = new IdfRow("Term Source Name");
    private static final IdfRow TERM_SOURCE_FILE = new IdfRow("Term Source File");
    private static final IdfRow TERM_SOURCE_VERSION = new IdfRow("Term Source Version");

    private ContactList contactList = new ContactList();

    private TermSourceList termSourceList = new TermSourceList();

    private GeneralInfoList generalInfoList = new GeneralInfoList();

    private Table table;

    public Investigation() {
        this(new Table());
    }

    @VisibleForTesting
    Investigation(Table table) {
        this.table = table;

        contactList.init(table);
        termSourceList.init(table);
        generalInfoList.init(table);
    }

    public static Investigation parse(InputStream in) throws IOException {
        Investigation table = new Investigation(new TsvParser().parse(in));
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

        Map<String, TermSource> termSources = newHashMap();
        for (TermSource source : getTermSources()) {
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

        List<Term> terms = newArrayList();
        terms.addAll(Collections2.transform(contactList.getAll(), new Function<Person, Term>() {
            public Term apply(@Nullable Person input) {
                return input.getRoles();
            }
        }));

        for (Term term : terms) {
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

    public IdfCell getDateOfExperiment() {
        return generalInfoList.getFirst(true).getDateOfExperiment();
    }

    public IdfCell getDateOfPublicRelease() {
        return generalInfoList.getFirst(true).getDateOfPublicRelease();
    }

    public List<Person> getContacts() {
        return contactList.getAll();
    }

    public List<TermSource> getTermSources() {
        return termSourceList.getAll();
    }

    private static class GeneralInfoList extends AbstractIdfList<Info> {
        private GeneralInfoList() {
            super(INVESTIGATION_TITLE,
                    EXPERIMENT_DESCRIPTION,
                    DATE_OF_EXPERIMENT,
                    DATE_OF_PUBLIC_RELEASE);
        }

        @Override
        protected Info create(IdfCell[] cells) {
            Info generalInfo = new Info();
            generalInfo.setTitle(cells[0]);
            generalInfo.setDescription(cells[1]);
            generalInfo.setDateOfExperiment(cells[2]);
            generalInfo.setDateOfPublicRelease(cells[3]);
            return generalInfo;
        }
    }

    private static class TermSourceList extends AbstractIdfList<TermSource> {
        protected TermSourceList() {
            super(TERM_SOURCE_NAME,
                    TERM_SOURCE_VERSION,
                    TERM_SOURCE_FILE);
        }

        @Override
        public TermSource create(IdfCell[] cells) {
            TermSource termSource = new TermSource();
            termSource.setName(cells[0]);
            termSource.setVersion(cells[1]);
            termSource.setFile(cells[2]);
            return termSource;
        }
    }

    private static class ContactList extends AbstractIdfList<Person> {
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
        public Person create(IdfCell[] cells) {
            /*Person p = new Person();
            p.setFirstName(cells[0]);
            p.setLastName(cells[1]);
            p.setMidInitials(cells[2]);
            p.setEmail(cells[3]);
            p.setRoles(createTerm(cells[4], cells[5], cells[6]));
            return p;*/
            return null;
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
            for (int j = 1; j <= maxColumn; j++) {
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

        protected Term createTerm(IdfCell name, IdfCell accession, IdfCell ref) {
            Term term = new Term();
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
}
