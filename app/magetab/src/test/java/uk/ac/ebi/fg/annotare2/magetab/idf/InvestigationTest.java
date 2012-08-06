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
import uk.ac.ebi.fg.annotare2.magetab.base.TableCell;

import java.io.IOException;
import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Olga Melnichuk
 */
public class InvestigationTest {

    @Test
    public void parseTest() throws IOException {
        Investigation idf = Investigation.parse(InvestigationTest.class.getResourceAsStream("/E-TABM-1009.idf.txt"));

        List<TableCell> errors = idf.getErrors();
        System.out.println("Errors: " + errors.size());
        for(TableCell er : errors) {
            System.out.println(format("%d, %d : %s", er.getRow(), er.getColumn(), er.getError()));
        }
    }

    @Test
    public void newTableTest() {
        Table table = new Table();
        Investigation idf = new Investigation(table);

        assertTrue(table.getCells().isEmpty());

        assertTrue(idf.getContacts().isEmpty());
        assertTrue(idf.getTermSources().isEmpty());

        assertNotNull(idf.getTitle());
        assertTrue(idf.getTitle().isEmpty());
        assertTrue(table.getCells().isEmpty());
    }
}
