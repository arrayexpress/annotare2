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

package uk.ac.ebi.fg.annotare2.magetab.table;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static uk.ac.ebi.fg.annotare2.magetab.TestUtils.asList;

/**
 * @author Olga Melnichuk
 */
public class TaggedRowTest {

    private static final RowTag ROW_TAG = new RowTag() {
        @Override
        public String getName() {
            return "ROW_TAG";
        }
    };

    @Test
    public void testNewTag() {
        Table table = new Table();

        TaggedRow taggedRow = new TaggedRow(table, ROW_TAG);
        assertNull(taggedRow.getRow());
        assertEquals(0, taggedRow.getSize());
        assertNull(taggedRow.cellAt(0).getValue());

        taggedRow.cellAt(0).setValue("A");
        assertEquals(1, taggedRow.getSize());
        assertEquals(2, taggedRow.getRow().getSize());
        assertEquals(ROW_TAG.getName(), taggedRow.getRow().getValue(0));
        assertEquals("A", taggedRow.getRow().getValue(1));
        assertEquals("A", taggedRow.cellAt(0).getValue());
    }

    @Test
    public void testExistedTag() {
        Table table = new Table();
        Row row = table.addRow(asList(ROW_TAG.getName()));

        TaggedRow taggedRow = new TaggedRow(table, ROW_TAG);
        assertEquals(row, taggedRow.getRow());
        assertEquals(0, taggedRow.getSize());
        assertEquals(1, taggedRow.getRow().getSize());

        taggedRow.cellAt(0).setValue("A");
        assertEquals(1, taggedRow.getSize());
        assertEquals(ROW_TAG.getName(), row.getValue(0));
        assertEquals("A", row.getValue(1));
        assertEquals("A", taggedRow.cellAt(0).getValue());
    }
}
