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

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.magetab.base.Table;
import uk.ac.ebi.fg.annotare2.magetab.base.TsvParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Olga Melnichuk
 */
public class PersonListTest {

    @Test
    public void test() throws IOException {
        Table table = new TsvParser().parse(InvestigationTest.class.getResourceAsStream("/E-TABM-1009.idf.txt"));
        PersonList personList = new PersonList(table);

        List<Person> list = personList.getAll();
        System.out.println(list.size());

        assertFalse(list.isEmpty());
        Person p = list.get(0);
        assertEquals("Lars", p.getFirstName().getValue());
        assertEquals("Hennig", p.getLastName().getValue());
        assertTrue(isNullOrEmpty(p.getMidInitials().getValue()));
        assertEquals("lhennig@ethz.ch", p.getEmail().getValue());
    }
}
