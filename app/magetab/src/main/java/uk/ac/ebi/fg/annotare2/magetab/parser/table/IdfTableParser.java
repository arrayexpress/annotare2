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

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import uk.ac.ebi.fg.annotare2.magetab.idf.Investigation;
import uk.ac.ebi.fg.annotare2.magetab.idf.Person;
import uk.ac.ebi.fg.annotare2.magetab.idf.TermList;
import uk.ac.ebi.fg.annotare2.magetab.idf.TermSource;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static uk.ac.ebi.fg.annotare2.magetab.parser.table.IdfTableParser.IdfTag.*;

/**
 * @author Olga Melnichuk
 */
public class IdfTableParser {

    private static final String EMPTY = "";

    protected enum IdfTag {
        PERSON_LAST_NAME("Person Last Name"),
        PERSON_FIRST_NAME("Person First Name"),
        PERSON_MID_INITIALS("Person Mid Initials"),
        PERSON_EMAIL("Person Email") {
            @Override
            public CheckedValue checkValue(TableCell cell) {
                //TODO a proper email checks
                return CheckedValue.checkTrue(cell.isEmpty() || (cell.getValue().contains("@")), cell, "Invalid email address");
            }
        },
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

        public boolean identifies(List<TableCell> row) {
            return title.equals(row.get(0).getValue());
        }

        public List<CheckedValue> checkValues(List<TableCell> row) {
            List<CheckedValue> checked = new ArrayList<CheckedValue>();
            for (TableCell cell : row) {
                if (cell.getColumn() == 0) {
                    continue;
                }
                checked.add(checkValue(cell));
            }
            return checked;
        }

        public CheckedValue checkValue(TableCell cell) {
            return CheckedValue.OK;
        }
    }

    private List<Error> errors;

    private Map<String, TermSource> termSources;

    public Investigation parse(InputStream in) throws IOException {
        errors = new ArrayList<Error>();

        Table table = new TsvParser().parse(in);

        Multimap<IdfTag, TableCell> parsedRows = ArrayListMultimap.create();

        for (int i = 0; i < table.getRowCount(); i++) {
            List<TableCell> row = table.getRow(i);

            boolean recognized = false;
            for (IdfTag tag : IdfTag.values()) {
                if (recognized = tag.identifies(row)) {
                    if (parsedRows.containsKey(tag)) {
                        addError(row.get(0), "Duplicated row");
                    } else {
                        addErrors(tag.checkValues(row));
                        parsedRows.putAll(tag, row);
                    }
                    break;
                }
            }

            if (!recognized) {
                addError(row.get(0), "unknown row specification");
            }
        }

        termSources = new HashMap<String, TermSource>();
        for (TermSource source : parseTermSources(parsedRows)) {
            if (termSources.containsKey(source.getName())) {
                addError("Duplicated term sources: " + source.getName());
                continue;
            }
            termSources.put(source.getName(), source);
        }

        Investigation.Builder builder = new Investigation.Builder();
        builder.setContacts(parseContacts(parsedRows));
        return builder.build();
    }


    private List<TermSource> parseTermSources(Multimap<IdfTag, TableCell> parsedRows) {
        return (new TermSourceTags(parsedRows)).parse();
    }

    private List<Person> parseContacts(Multimap<IdfTag, TableCell> parsedRows) {
        return (new PersonTags(parsedRows)).parse();
    }

    private TermList createTermList(TableCell names, TableCell accessions, TableCell ref) {
        return new TermList.Builder(lookup(ref))
                .addTerm(
                        required(names, "Term Name"),
                        optional(accessions))
                .build();
    }

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

    private void addError(TableCell cell, String message) {
        errors.add(new Error(cell, message));
    }

    private void addError(String message) {
        errors.add(new Error(null, message));
    }

    private void addErrors(List<CheckedValue> checkedValues) {
        for (CheckedValue v : checkedValues) {
            v.addErrorTo(errors);
        }
    }

    public List<Error> getErrors() {
        return errors;
    }

    private class PersonTags {

        private final GroupedTags<Person> tags = new GroupedTags<Person>(
                new Function<TableCell[], Person>() {
                    public Person apply(@Nullable TableCell[] cells) {
                        checkNotNull(cells);
                        return new Person.Builder()
                                .setFirstName(required(cells[0], "Person First Name"))
                                .setLastName(required(cells[1], "Person Last Name"))
                                .setMidInitials(optional(cells[2]))
                                .setEmail(optional(cells[3]))
                                .setRoles(createTermList(cells[4], cells[5], cells[6]))
                                .build();
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

        private PersonTags(Multimap<IdfTag, TableCell> parsedRows) {
            tags.setAll(parsedRows);
        }

        public List<Person> parse() {
            return tags.getAll();
        }
    }

    private class TermSourceTags {

        private final GroupedTags<TermSource> tags = new GroupedTags<TermSource>(
                new Function<TableCell[], TermSource>() {
                    public TermSource apply(@Nullable TableCell[] cells) {
                        checkNotNull(cells);
                        return new TermSource(
                                required(cells[0], "Term Source Name"),
                                optional(cells[1]),
                                required(cells[2], "Term Source File")
                        );
                    }
                },
                TERM_SOURCE_NAME,
                TERM_SOURCE_VERSION,
                TERM_SOURCE_FILE
        );

        public TermSourceTags(Multimap<IdfTag, TableCell> parsedRows) {
            tags.setAll(parsedRows);
        }

        public List<TermSource> parse() {
            return tags.getAll();
        }
    }

    private String optional(TableCell cell) {
        return cell == null || cell.isEmpty() ? EMPTY : cell.getValue();
    }

    private String required(TableCell cell, String name) {
        if (cell == null) {
            String m = "Tag [" + name + "] is required; no any value found specified";
            addError(m);
            return null;
        }
        if (cell.isEmpty()) {
            String m = "Tag [" + name + "] is required and should not be empty";
            addError(cell, m);
            return EMPTY;
        }
        return cell.getValue();
    }

    private static class GroupedTags<T> {

        private final Map<IdfTag, List<TableCell>> map = new LinkedHashMap<IdfTag, List<TableCell>>();

        private final Function<TableCell[], T> func;

        private int size;

        public GroupedTags(Function<TableCell[], T> func, IdfTag... tags) {
            this.func = func;
            for (IdfTag tag : tags) {
                map.put(tag, new ArrayList<TableCell>());
            }
        }

        public void setAll(Multimap<IdfTag, TableCell> parsedRows) {
            Integer sz = null;
            for (IdfTag tag : map.keySet()) {
                Collection<TableCell> cells = parsedRows.get(tag);
                List<TableCell> list = newArrayList(cells);
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
                TableCell[] values = new TableCell[map.size()];
                int t = -1, empties = 0;

                for (IdfTag tag : map.keySet()) {
                    t++;
                    List<TableCell> cells = map.get(tag);
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
    }

    public static class Error {
        private final int line;
        private final int column;
        private final String message;

        private Error(@Nullable TableCell cell, String message) {
            this.line = cell == null ? -1 : cell.getLine();
            this.column = cell == null ? -1 : cell.getColumn();
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
            return "(" + line + "," + column + "): " + message;
        }
    }

    private static class CheckedValue {
        private static final CheckedValue OK = new CheckedValue(null);
        private final Error error;

        private CheckedValue(Error error) {
            this.error = error;
        }

        public void addErrorTo(Collection<Error> errorList) {
            if (error != null) {
                errorList.add(error);
            }
        }

        public static CheckedValue checkTrue(boolean expr, TableCell cell, String msg) {
            if (expr) {
                return CheckedValue.OK;
            }
            return new CheckedValue(new Error(cell, msg));
        }
    }
}
