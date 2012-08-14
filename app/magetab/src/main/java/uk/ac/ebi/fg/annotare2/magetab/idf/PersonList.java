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
import uk.ac.ebi.fg.annotare2.magetab.base.RowTag;
import uk.ac.ebi.fg.annotare2.magetab.base.Table;

import java.util.Map;

import static uk.ac.ebi.fg.annotare2.magetab.idf.PersonList.Tag.*;

/**
 * @author Olga Melnichuk
 */
public class PersonList extends ObjectList<Person> {

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

    public PersonList(Table table) {
        super(table, Tag.values());
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
