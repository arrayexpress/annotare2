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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Olga Melnichuk
 */
public class RowTest {

    @Test
    public void testRowSize() {
        Row row = new Row();

        assertEquals(0, row.getSize());
        assertEquals(0, row.getTrimmedSize());

        row.setValue(0, "A");
        assertEquals(1, row.getSize());
        assertEquals(1, row.getTrimmedSize());

        row.setValue(1, "B");
        assertEquals(2, row.getSize());
        assertEquals(2, row.getTrimmedSize());

        row.setValue(3, "D");
        assertEquals(4, row.getSize());
        assertEquals(4, row.getTrimmedSize());

        row.setValue(4, "");
        assertEquals(5, row.getSize());
        assertEquals(4, row.getTrimmedSize());

        row.setValue(4, null);
        assertEquals(5, row.getSize());
        assertEquals(4, row.getTrimmedSize());
    }

    @Test
    public void testRowValues() {
        Row row = new Row();

        row.setValue(0, "A");
        assertEquals("A", row.getValue(0));

        row.setValue(0, "");
        assertEquals("", row.getValue(0));

        row.setValue(0, null);
        assertEquals(null, row.getValue(0));
    }
}
