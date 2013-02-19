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

package uk.ac.ebi.fg.annotare2.magetab.rowbased;

import com.google.common.annotations.GwtCompatible;
import uk.ac.ebi.fg.annotare2.magetab.rowbased.format.TextFormatter;
import uk.ac.ebi.fg.annotare2.magetab.table.*;

import java.util.*;

import static uk.ac.ebi.fg.annotare2.magetab.rowbased.Investigation.Tag.*;


/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class Investigation {

    static enum Tag implements RowTag {
        INVESTIGATION_TITLE("Investigation Title"),
        EXPERIMENT_DESCRIPTION("Experiment Description"),
        DATE_OF_EXPERIMENT("Date of Experiment"),
        DATE_OF_PUBLIC_RELEASE("Public Release Date"),
        SDRF_FILE("SDRF File"),

        PERSON_FIRST_NAME("Person First Name"),
        PERSON_LAST_NAME("Person Last Name"),
        PERSON_MID_INITIALS("Person Mid Initials"),
        PERSON_EMAIL("Person Email"),
        PERSON_PHONE("Person Phone"),
        PERSON_FAX("Person Fax"),
        PERSON_ADDRESS("Person Address"),
        PERSON_AFFILIATION("Person Affiliation"),
        PERSON_ROLES("Person Roles"),
        PERSON_ROLES_TERM_ACCESSION_NUMBER("Person Roles Term Accession Number"),
        PERSON_ROLES_TERM_SOURCE_REF("Person Roles Term Source REF"),

        EXPERIMENTAL_DESIGN_NAME("Experimental Design"),
        EXPERIMENTAL_DESIGN_TERM_SOURCE_REF("Experimental Design Term Source REF"),
        EXPERIMENTAL_DESIGN_TERM_ACCESSION_NUMBER("Experimental Design Term Accession Number"),

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

    private final TermBasedObjectList experimentalDesignList;

    private final TermSourceList termSourceList;

    public Investigation() {
        this(new Table());
    }

    public Investigation(Table table) {
        termSourceList = new TermSourceList(table,
                TERM_SOURCE_NAME,
                TERM_SOURCE_VERSION,
                TERM_SOURCE_FILE);

        generalInfoList = new GeneralInfoList(table);
        contactList = new ContactList(table);
        experimentalDesignList = new TermBasedObjectList(table, termSourceList,
                EXPERIMENTAL_DESIGN_NAME,
                EXPERIMENTAL_DESIGN_TERM_ACCESSION_NUMBER,
                EXPERIMENTAL_DESIGN_TERM_SOURCE_REF);

        if (generalInfoList.isEmpty()) {
            generalInfoList.add();
        }

        //check();
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

    public Row.Cell<String> getTitle() {
        return generalInfoList.get(0).getTitle();
    }

    public Row.Cell<String> getDescription() {
        return generalInfoList.get(0).getDescription();
    }

    public Row.Cell<Date> getDateOfExperiment() {
        return generalInfoList.get(0).getDateOfExperiment();
    }

    public Row.Cell<Date> getDateOfPublicRelease() {
        return generalInfoList.get(0).getDateOfPublicRelease();
    }

    public Row.Cell<String> getSdrfFile() {
        return generalInfoList.get(0).getSdrfFile();
    }

    public void removeContact(List<Integer> indices) {
        contactList.remove(indices);
    }

    public Person createContact() {
        return contactList.add();
    }

    public List<Person> getContacts() {
        return contactList.getAll();
    }

    public List<Term> getExperimentalDesigns() {
        return experimentalDesignList.getAll();
    }

    public Term createExperimentalDesign() {
        return experimentalDesignList.add();
    }

    public void removeExperimentalDesigns(List<Integer> indices) {
        experimentalDesignList.remove(indices);
    }

    public List<TermSource> getTermSources() {
        return termSourceList.getAll();
    }

    public TermSource getTermSource(String name) {
        return termSourceList.getTermSource(name);
    }

    public TermSource createTermSource() {
        return termSourceList.add();
    }

    public void removeTermSources(List<Integer> indices) {
        termSourceList.remove(indices);
    }

    private static class GeneralInfoList extends ObjectList<Info> {

        private GeneralInfoList(Table table) {
            super(new RowSet(
                    INVESTIGATION_TITLE,
                    EXPERIMENT_DESCRIPTION,
                    DATE_OF_EXPERIMENT,
                    DATE_OF_PUBLIC_RELEASE,
                    SDRF_FILE).from(table),
                    new ObjectCreator<Info>() {
                        @Override
                        public Info create(Map<RowTag, Row.Cell<String>> map) {
                            Info generalInfo = new Info();
                            generalInfo.setTitle(map.get(INVESTIGATION_TITLE));
                            generalInfo.setDescription(map.get(EXPERIMENT_DESCRIPTION));
                            generalInfo.setDateOfExperiment(asDateCell(map.get(DATE_OF_EXPERIMENT)));
                            generalInfo.setDateOfPublicRelease(asDateCell(map.get(DATE_OF_PUBLIC_RELEASE)));
                            generalInfo.setSdrfFile(map.get(SDRF_FILE));
                            return generalInfo;
                        }

                        private Row.Cell<Date> asDateCell(final Row.Cell<String> cell) {
                            return new Row.Cell<Date>() {

                                @Override
                                public void setValue(Date date) {
                                    cell.setValue(format(date));
                                }

                                @Override
                                public Date getValue() {
                                    return parse(cell.getValue());
                                }

                                @Override
                                public boolean isEmpty() {
                                    return cell.isEmpty();
                                }

                                private String format(Date date) {
                                    return TextFormatter.getInstance().formatDate(date);
                                }

                                private Date parse(String s) {
                                    return TextFormatter.getInstance().parseDate(s);
                                }
                            };
                        }
                    });
        }
    }

    private static class ContactList extends ObjectList<Person> {

        private ContactList(Table table) {
            super(new RowSet(
                    PERSON_FIRST_NAME,
                    PERSON_LAST_NAME,
                    PERSON_MID_INITIALS,
                    PERSON_EMAIL,
                    PERSON_PHONE,
                    PERSON_FAX,
                    PERSON_AFFILIATION,
                    PERSON_ADDRESS,
                    PERSON_ROLES,
                    PERSON_ROLES_TERM_ACCESSION_NUMBER,
                    PERSON_ROLES_TERM_SOURCE_REF).from(table),
                    new ObjectCreator<Person>() {
                        @Override
                        public Person create(Map<RowTag, Row.Cell<String>> map) {
                            Person p = new Person();
                            p.setFirstName(map.get(PERSON_FIRST_NAME));
                            p.setLastName(map.get(PERSON_LAST_NAME));
                            p.setMidInitials(map.get(PERSON_MID_INITIALS));
                            p.setEmail(map.get(PERSON_EMAIL));
                            p.setPhone(map.get(PERSON_PHONE));
                            p.setFax(map.get(PERSON_FAX));
                            p.setAffiliation(map.get(PERSON_AFFILIATION));
                            p.setAddress(map.get(PERSON_ADDRESS));
                            return p;
                        }
                    });
        }
    }

    public List<TableCell> getErrors() {
        //TODO
        return new ArrayList<TableCell>();
    }
}
