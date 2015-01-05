/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.server.magetab.adf;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Row;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
        assertNull(taggedRow.getValue(0));

        taggedRow.setValue(0, "A");
        assertEquals(1, taggedRow.getSize());
        assertEquals(2, taggedRow.getRow().getSize());
        assertEquals(ROW_TAG.getName(), taggedRow.getRow().getValue(0));
        assertEquals("A", taggedRow.getRow().getValue(1));
        assertEquals("A", taggedRow.getValue(0));
    }

    @Test
    public void testExistedTag() {
        Table table = new Table();
        Row row = table.addRow(asList(ROW_TAG.getName()));

        TaggedRow taggedRow = new TaggedRow(table, ROW_TAG);
        assertEquals(row, taggedRow.getRow());
        assertEquals(0, taggedRow.getSize());
        assertEquals(1, taggedRow.getRow().getSize());

        taggedRow.setValue(0, "A");
        assertEquals(1, taggedRow.getSize());
        assertEquals(ROW_TAG.getName(), row.getValue(0));
        assertEquals("A", row.getValue(1));
        assertEquals("A", taggedRow.getValue(0));
    }
}
