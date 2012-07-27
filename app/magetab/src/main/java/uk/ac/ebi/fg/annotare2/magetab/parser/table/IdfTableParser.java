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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import uk.ac.ebi.fg.annotare2.magetab.idf.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static uk.ac.ebi.fg.annotare2.magetab.parser.table.IdfTableParser.IdfField.*;

/**
 * @author Olga Melnichuk
 */
public class IdfTableParser {

    protected enum IdfField {
        PERSON_LAST_NAME("Person Last Name"),
        PERSON_FIRST_NAME("Person First Name"),
        PERSON_MID_INITIALS("Person Mid Initials"),
        PERSON_EMAIL("Person Email") {
            @Override
            public Error checkValue(Table.Cell cell) {
                //TODO
                return (!cell.getValue().contains("@")) ?
                        new Error(cell, "Invalid email address") : null;
            }
        },
        PERSON_ROLES("Person Roles"),
        PERSON_ROLES_TERM_ACCESSION_NUMBER("Person Roles Term Accession Number"),
        PERSON_ROLES_TERM_SOURCE_REF("Person Roles Term Source REF"),

        TERM_SOURCE_NAME("Term Source Name"),
        TERM_SOURCE_FILE("Term Source File"),
        TERM_SOURCE_VERSION("Term Source Version");

        private final String title;

        IdfField(String title) {
            this.title = title;
        }

        public boolean knows(List<Table.Cell> row) {
            return title.equals(row.get(0).getValue());
        }

        public List<Error> checkValues(List<Table.Cell> cells) {
            List<Error> errors = new ArrayList<Error>();
            for (Table.Cell cell : cells) {
                Error error = checkValue(cell);
                if (error != null) {
                    errors.add(error);
                }
            }
            return errors;
        }

        public Error checkValue(Table.Cell cell) {
            return null;
        }
    }

    private List<Error> errors;

    private Map<String, TermSource> termSources;

    public Investigation parse(InputStream in) throws IOException {
        errors = new ArrayList<Error>();

        Table table = new TsvParser().parse(in);

        Multimap<IdfField, Table.Cell> parsedRows = ArrayListMultimap.create();

        for (int i = 0; i < table.getRowCount(); i++) {
            List<Table.Cell> row = table.getRow(i);

            boolean known = false;
            for (IdfField field : IdfField.values()) {
                if (known = field.knows(row)) {
                    if (parsedRows.containsKey(field)) {
                        addError(new Error(row.get(0), "Duplicated row"));
                    } else {
                        addErrors(field.checkValues(row));
                        parsedRows.putAll(field, row);
                    }
                    break;
                }
            }

            if (!known) {
                addError(new Error(row.get(0), "unknown row specification"));
            }
        }

        termSources = new HashMap<String, TermSource>();
        for(TermSource source : parseTermSources(parsedRows)) {
           termSources.put(source.getName(), source);
        }

        Investigation.Builder builder = new Investigation.Builder();
        builder.setContacts(parseContacts(parsedRows));
        return builder.build();
    }

    private List<TermSource> parseTermSources(Multimap<IdfField, Table.Cell> parsedRows) {
        return (new TermSourceFieldGroup(parsedRows)).parse();
    }

    private List<Person> parseContacts(Multimap<IdfField, Table.Cell> parsedRows) {
        return (new PersonFieldGroup(parsedRows)).parse();
    }

    private TermList createTermList(Table.Cell names, Table.Cell accessions, Table.Cell ref) {
        return new TermList.Builder(lookup(ref))
                .addTerm(names.getValue(), accessions.getValue())
                .build();
    }


    public TermSource lookup(Table.Cell ref) {
        if (ref.isEmpty()) {
            return TermSource.DEFAULT;
        }
        TermSource source = termSources.get(ref.getValue());
        if (source == null) {
            addError(new Error(ref, "Term Source Reference not found"));
            return TermSource.DEFAULT;
        }
        return source;
    }

    private void addError(Error error) {
       errors.add(error);
    }

    private void addErrors(List<Error> errors) {
        errors.addAll(errors);
    }

    public List<Error> getErrors() {
        return errors;
    }

    private class PersonFieldGroup {
        private final List<Table.Cell> firstNames = newArrayList();
        private final List<Table.Cell> lastNames = newArrayList();
        private final List<Table.Cell> midInitials = newArrayList();
        private final List<Table.Cell> emails = newArrayList();
        private final List<Table.Cell> roleNames = newArrayList();
        private final List<Table.Cell> roleAccessions = newArrayList();
        private final List<Table.Cell> roleRefs = newArrayList();

        private PersonFieldGroup(Multimap<IdfField, Table.Cell> parsedRows) {
            firstNames.addAll(parsedRows.get(PERSON_FIRST_NAME));
            lastNames.addAll(parsedRows.get(PERSON_LAST_NAME));
            midInitials.addAll(parsedRows.get(PERSON_MID_INITIALS));
            emails.addAll(parsedRows.get(PERSON_EMAIL));
        }

        public List<Person> parse() {
            List<Person> out = new ArrayList<Person>();
            for (int i = 0; i < firstNames.size(); i++) {
                out.add(new Person.Builder()
                        .setFirstName(firstNames.get(0).getValue())
                        .setLastName(lastNames.get(0).getValue())
                        .setMidInitials(midInitials.get(0).getValue())
                        .setEmail(emails.get(0).getValue())
                        .setRoles(createTermList(roleNames.get(0), roleAccessions.get(0), roleRefs.get(0)))
                        .build());

            }
            return out;
        }
    }

    private class TermSourceFieldGroup {
        private final List<Table.Cell> termSourceNames = newArrayList();
        private final List<Table.Cell> termSourceFiles = newArrayList();
        private final List<Table.Cell> termSourceVersions = newArrayList();

        private TermSourceFieldGroup(Multimap<IdfField, Table.Cell> parsedRows) {
            termSourceNames.addAll(parsedRows.get(TERM_SOURCE_NAME));
            termSourceFiles.addAll(parsedRows.get(TERM_SOURCE_FILE));
            termSourceVersions.addAll(parsedRows.get(TERM_SOURCE_VERSION));
        }

        public List<TermSource> parse() {
            List<TermSource> out = new ArrayList<TermSource>();
            for (int i = 0; i < termSourceNames.size(); i++) {
                //TODO check if all values are empty
                out.add(new TermSource(
                        termSourceNames.get(i).getValue(),
                        termSourceFiles.get(i).getValue(),
                        termSourceVersions.get(i).getValue()
                ));
            }
            return out;
        }
    }

    private static class Error {
        private final int line;
        private final int column;
        private final String message;

        private Error(Table.Cell cell, String message) {
            this.line = cell.getLine();
            this.column = cell.getCol();
            this.message = message;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "(" + line +
                    "," + column +
                    ": " + message + '\'' +
                    ')';
        }
    }
}
