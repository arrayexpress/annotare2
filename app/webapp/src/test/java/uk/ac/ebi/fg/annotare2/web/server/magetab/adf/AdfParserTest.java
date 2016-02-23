/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.magetab.adf;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Row;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Olga Melnichuk
 */
public class AdfParserTest {

    @Test
    public void parseHeaderTest() throws IOException {
        Table header = new AdfParser().parseHeader(AdfParserTest.class.getResourceAsStream("/A-MEXP-2196_part.adf.txt"));
        assertEquals(16, header.getHeight());

        Row row = header.getRow(0);
        assertEquals(2, row.getSize());
        assertEquals("Array Design Name", row.getValue(0));

        row = header.getRow(15);
        assertEquals(0, row.getSize());
    }

    @Test
    public void parseBodyTest() throws IOException {
        Table body = new AdfParser().parseBody(AdfParserTest.class.getResourceAsStream("/A-MEXP-2196_part.adf.txt"));
        assertEquals(20, body.getHeight());

        Row row = body.getRow(0);
        assertEquals(11, row.getSize());
        assertEquals("Block Column", row.getValue(0));
    }
}
