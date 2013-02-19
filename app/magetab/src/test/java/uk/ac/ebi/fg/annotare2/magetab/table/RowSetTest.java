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

import java.util.Map;

import static org.junit.Assert.*;
import static uk.ac.ebi.fg.annotare2.magetab.TestUtils.asList;

/**
 * @author Olga Melnichuk
 */
public class RowSetTest {

    private static final RowTag ROW_TAG_1 = new RowTag() {
        @Override
        public String getName() {
            return "ROW_TAG_1";
        }
    };

    private static final RowTag ROW_TAG_2 = new RowTag() {
        @Override
        public String getName() {
            return "ROW_TAG_2";
        }
    };

    @Test
    public void testAddColumn() {
        Table table = new Table();
        table.addRow(asList(ROW_TAG_1.getName()));
        table.addRow(asList(ROW_TAG_2.getName(), "2"));

        RowSet rowSet = new RowSet(ROW_TAG_1, ROW_TAG_2).from(table);

        assertEquals(1, rowSet.getWidth());
        Map<RowTag, Row.Cell<String>> column = rowSet.getColumn(0);
        assertNull(column.get(ROW_TAG_1).getValue());
        assertEquals("2", column.get(ROW_TAG_2).getValue());

        rowSet.addColumn();
        assertEquals(1, rowSet.getWidth());

        column = rowSet.getColumn(1);
        assertNull(column.get(ROW_TAG_1).getValue());
        assertNull(column.get(ROW_TAG_2).getValue());

        column.get(ROW_TAG_1).setValue("1");
        assertEquals(2, rowSet.getWidth());
    }

    @Test
    public void testRemoveColumn() {
        Table table = new Table();
        table.addRow(asList(ROW_TAG_1.getName()));
        table.addRow(asList(ROW_TAG_2.getName(), "2"));

        RowSet rowSet = new RowSet(ROW_TAG_1, ROW_TAG_2).from(table);
        assertEquals(1, rowSet.getWidth());

        rowSet.removeColumn(asList(0));
        assertEquals(0, rowSet.getWidth());

        try {
            rowSet.removeColumn(asList(0));
            //OK
        } catch (IndexOutOfBoundsException e) {
            fail("It is okay to remove any column with index >= 0, even if it doesn't exist ");
        }

        try {
            rowSet.removeColumn(asList(-1));
            fail("It should not be possible to remove row with an invalid index");
        } catch (IndexOutOfBoundsException e) {
            //OK
        }
    }

    @Test
    public void testMoveColumn() {
        Table table = new Table();
        table.addRow(asList(ROW_TAG_1.getName(), "", "1"));
        table.addRow(asList(ROW_TAG_2.getName(), "2"));

        RowSet rowSet = new RowSet(ROW_TAG_1, ROW_TAG_2).from(table);
        assertEquals(2, rowSet.getWidth());

        rowSet.moveColumn(1, 0);
        assertEquals(2, rowSet.getWidth());

        Map<RowTag, Row.Cell<String>> column = rowSet.getColumn(0);
        assertEquals("1", column.get(ROW_TAG_1).getValue());
        assertNull(column.get(ROW_TAG_2).getValue());

        column = rowSet.getColumn(1);
        assertNull(column.get(ROW_TAG_1).getValue());
        assertEquals("2", column.get(ROW_TAG_2).getValue());
    }
}
