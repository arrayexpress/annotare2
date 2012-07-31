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
import uk.ac.ebi.fg.annotare2.magetab.parser.table.om.IdfPerson;
import uk.ac.ebi.fg.annotare2.magetab.parser.table.om.IdfTerm;
import uk.ac.ebi.fg.annotare2.magetab.parser.table.om.IdfTermSource;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static uk.ac.ebi.fg.annotare2.magetab.parser.table.IdfTable.IdfTag.*;

/**
 * @author Olga Melnichuk
 */
public class IdfTable {

    protected enum IdfTag {
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

        public boolean identifies(List<TableCell> row) {
            return title.equals(row.get(0).getValue());
        }
    }

    private Table table;

    public void parse(InputStream in) throws IOException {

        table = new TsvParser().parse(in);

        Multimap<IdfTag, TableCell> parsedRows = ArrayListMultimap.create();

        for (int i = 0; i < table.getRowCount(); i++) {
            List<TableCell> row = table.getRow(i);

            boolean recognized = false;
            for (IdfTag tag : IdfTag.values()) {
                if (recognized = tag.identifies(row)) {
                    if (parsedRows.containsKey(tag)) {
                        row.get(0).setError("Duplicated row");
                    } else {
                        parsedRows.putAll(tag, row);
                    }
                    break;
                }
            }

            if (!recognized) {
                row.get(0).setError("unknown row specification");
            }
        }

        /*termSources = new HashMap<String, TermSource>();
        for (TermSource source : parseTermSources(parsedRows)) {
            if (termSources.containsKey(source.getName())) {
                addError("Duplicated term sources: " + source.getName());
                continue;
            }
            termSources.put(source.getName(), source);
        }*/
    }

    public List<IdfPerson> getContacts() {
        // TODO
        return null;
    }

    public void addContact(String firstName, String lastName) {
        // TODO
    }

/*    private List<IdfTermSource> parseTermSources(Multimap<IdfTag, TableCell> parsedRows) {
        return (new TermSourceTags(parsedRows)).parse();
    }

    private List<IdfPerson> parseContacts(Multimap<IdfTag, TableCell> parsedRows) {
        return (new PersonTags(parsedRows)).parse();
    }*/

    private IdfTerm createTerm(TableCell name, TableCell accession, TableCell ref) {
        IdfTerm term = new IdfTerm();
        term.setName(name);
        term.setAccession(accession);
        term.setRef(ref);
        return term;
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
*/

    private class PersonTags {
        private final GroupedTags<IdfPerson> tags = new GroupedTags<IdfPerson>(
                new Function<TableCell[], IdfPerson>() {
                    public IdfPerson apply(@Nullable TableCell[] cells) {
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

        private PersonTags(Multimap<IdfTag, TableCell> parsedRows) {
            tags.setAll(parsedRows);
        }

        public List<IdfPerson> parse() {
            return tags.getAll();
        }
    }

    private class TermSourceTags {
        private final GroupedTags<IdfTermSource> tags = new GroupedTags<IdfTermSource>(
                new Function<TableCell[], IdfTermSource>() {
                    public IdfTermSource apply(@Nullable TableCell[] cells) {
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

        public TermSourceTags(Multimap<IdfTag, TableCell> parsedRows) {
            tags.setAll(parsedRows);
        }

        public List<IdfTermSource> parse() {
            return tags.getAll();
        }
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
}
