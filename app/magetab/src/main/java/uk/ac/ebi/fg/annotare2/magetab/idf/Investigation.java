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
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import uk.ac.ebi.fg.annotare2.magetab.base.*;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static uk.ac.ebi.fg.annotare2.magetab.idf.Investigation.Tag.*;


/**
 * @author Olga Melnichuk
 */
public class Investigation {

    static enum Tag implements RowTag {
        INVESTIGATION_TITLE("Investigation Title"),
        EXPERIMENT_DESCRIPTION("Experiment Description"),
        DATE_OF_EXPERIMENT("Date of Experiment"),
        DATE_OF_PUBLIC_RELEASE("Public Release Date"),

        PERSON_FIRST_NAME("Person First Name"),
        PERSON_LAST_NAME("Person Last Name"),
        PERSON_MID_INITIALS("Person Mid Initials"),
        PERSON_EMAIL("Person Email"),
        PERSON_ROLES("Person Roles"),
        PERSON_ROLES_TERM_ACCESSION_NUMBER("Person Roles Term Accession Number"),
        PERSON_ROLES_TERM_SOURCE_REF("Person Roles Term Source REF"),

        TERM_SOURCE_NAME("Term Source Name"),
        TERM_SOURCE_FILE("Term Source File"),
        TERM_SOURCE_VERSION("Term Source Version");

        private String tagName;

        private Tag(String title) {
            this.tagName = title;
        }

        @Override
        public String getName() {
            return tagName;
        }
    }

    private final GeneralInfoList generalInfoList;

    private final ContactList contactList;

    private TermSourceList termSourceList;


    private Table table;

    public Investigation() {
        this(new Table());
    }

    @VisibleForTesting
    Investigation(Table table) {
        this.table = table;

        generalInfoList = new GeneralInfoList(table);
        contactList = new ContactList(table);
        termSourceList = new TermSourceList(table);

        if (generalInfoList.isEmpty()) {
            generalInfoList.add();
        }
    }

    public static Investigation parse(InputStream in) throws IOException {
        Investigation table = new Investigation(new TsvParser().parse(in));
        table.check();
        return table;
    }

    public void check() {
        /*
        TODO
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
        }*/
    }

    public Row.Cell getTitle() {
        return generalInfoList.get(0).getTitle();
    }

    public Row.Cell getDescription() {
        return generalInfoList.get(0).getDescription();
    }

    public Row.Cell getDateOfExperiment() {
        return generalInfoList.get(0).getDateOfExperiment();
    }

    public Row.Cell getDateOfPublicRelease() {
        return generalInfoList.get(0).getDateOfPublicRelease();
    }

    public List<Person> getContacts() {
        return contactList.getAll();
    }

    public List<TermSource> getTermSources() {
        return termSourceList.getAll();
    }

    private static class GeneralInfoList extends ObjectList<Info> {

        private GeneralInfoList(Table table) {
            super(table,
                    INVESTIGATION_TITLE,
                    EXPERIMENT_DESCRIPTION,
                    DATE_OF_EXPERIMENT,
                    DATE_OF_PUBLIC_RELEASE);
        }

        @Override
        protected Info create(Map<RowTag, Row.Cell> map) {
            Info generalInfo = new Info();
            generalInfo.setTitle(map.get(INVESTIGATION_TITLE));
            generalInfo.setDescription(map.get(EXPERIMENT_DESCRIPTION));
            generalInfo.setDateOfExperiment(map.get(DATE_OF_EXPERIMENT));
            generalInfo.setDateOfPublicRelease(map.get(DATE_OF_PUBLIC_RELEASE));
            return generalInfo;
        }
    }

    private static class TermSourceList extends ObjectList<TermSource> {

        protected TermSourceList(Table table) {
            super(table,
                    TERM_SOURCE_NAME,
                    TERM_SOURCE_VERSION,
                    TERM_SOURCE_FILE);
        }

        @Override
        protected TermSource create(Map<RowTag, Row.Cell> map) {
            TermSource termSource = new TermSource();
            termSource.setName(map.get(TERM_SOURCE_NAME));
            termSource.setVersion(map.get(TERM_SOURCE_VERSION));
            termSource.setFile(map.get(TERM_SOURCE_FILE));
            return termSource;
        }
    }

    private static class ContactList extends ObjectList<Person> {

        private ContactList(Table table) {
            super(table,
                    PERSON_FIRST_NAME,
                    PERSON_LAST_NAME,
                    PERSON_MID_INITIALS,
                    PERSON_EMAIL,
                    PERSON_ROLES,
                    PERSON_ROLES_TERM_ACCESSION_NUMBER,
                    PERSON_ROLES_TERM_SOURCE_REF);
        }

        @Override
        protected Person create(Map<RowTag, Row.Cell> map) {
            Person p = new Person();
            p.setFirstName(map.get(PERSON_FIRST_NAME));
            p.setLastName(map.get(PERSON_LAST_NAME));
            p.setMidInitials(map.get(PERSON_MID_INITIALS));
            p.setEmail(map.get(PERSON_EMAIL));
            return p;
        }
    }

    public List<TableCell> getErrors() {
        return newArrayList(Collections2.filter(table.getCells(), new Predicate<TableCell>() {
            public boolean apply(@Nullable TableCell input) {
                return input.hasError();
            }
        }));
    }
}
