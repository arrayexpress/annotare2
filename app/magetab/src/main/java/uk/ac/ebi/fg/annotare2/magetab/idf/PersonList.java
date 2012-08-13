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

import uk.ac.ebi.fg.annotare2.magetab.base.Row;
import uk.ac.ebi.fg.annotare2.magetab.base.RowSet;
import uk.ac.ebi.fg.annotare2.magetab.base.RowTag;
import uk.ac.ebi.fg.annotare2.magetab.base.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.magetab.idf.PersonList.Tag.*;

/**
 * @author Olga Melnichuk
 */
public class PersonList {

    static enum Tag implements RowTag {
        PERSON_FIRST_NAME("Person First Name"),
        PERSON_LAST_NAME("Person Last Name"),
        PERSON_MID_INITIALS("Person Mid Initials"),
        PERSON_EMAIL("Person Email"),
        PERSON_ROLES("Person Roles"),
        PERSON_ROLES_TERM_ACCESSION_NUMBER("Person Roles Term Accession Number"),
        PERSON_ROLES_TERM_SOURCE_REF("Person Roles Term Source REF");

        private String tagName;

        private Tag(String title) {
            this.tagName = title;
        }

        @Override
        public String getName() {
            return tagName;
        }
    }

    private final RowSet<Tag> rowSet;

    private final List<Person> list = new ArrayList<Person>();

    public PersonList(Table table) {
        rowSet = new RowSet<Tag>(Tag.values());
        rowSet.addAll(table);
        for (int i=0; i<rowSet.getColumnCount(); i++) {
            list.add(get(i));
        }
    }

    public Person add() {
        int column = rowSet.addColumn();
        return get(column);
    }

    private Person get(int i) {
        // TODO check index
        Person p = new Person();
        p.setFirstName(cellAt(PERSON_FIRST_NAME, i));
        p.setLastName(cellAt(PERSON_LAST_NAME, i));
        p.setMidInitials(cellAt(PERSON_MID_INITIALS, i));
        p.setEmail(cellAt(PERSON_EMAIL, i));
        return p;
    }

    private Row.Cell cellAt(Tag tag, int i) {
        return rowSet.rowAt(tag).cellAt(i);
    }

    public void move(Person p, int index) {
        //TODO
    }

    public void remove(Person person) {
        list.indexOf(person);
        remove(list.indexOf(person));
    }

    public void remove(int index) {
        rowSet.removeColumn(index);
        list.remove(index);
    }

    public List<Person> getAll() {
        return Collections.unmodifiableList(list);
    }
}
