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

package uk.ac.ebi.fg.annotare2.magetab.base.operation;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.magetab.base.ChangeListener;
import uk.ac.ebi.fg.annotare2.magetab.base.RowSet;
import uk.ac.ebi.fg.annotare2.magetab.base.RowTag;
import uk.ac.ebi.fg.annotare2.magetab.base.Table;

import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.ac.ebi.fg.annotare2.magetab.TestUtils.asList;

/**
 * @author Olga Melnichuk
 */
public class MoveColumnOperationTest {

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
    public void testHandleOperation() {
        final Queue<Operation> operations = new ArrayDeque<Operation>();
        Table table = new Table();
        table.addRow(asList(ROW_TAG_1.getName(), "", "1"));
        table.addRow(asList(ROW_TAG_2.getName(), "2", ""));

        RowSet rowSet = new RowSet(ROW_TAG_1, ROW_TAG_2).from(table);

        table.addChangeListener(new ChangeListener() {
            @Override
            public void onChange(Operation operation) {
                operations.offer(operation);
            }
        });

        rowSet.moveColumn(0, 1);
        assertFalse(operations.isEmpty());

        Operation op = operations.poll();
        assertTrue(op instanceof MoveColumnOperation);

        MoveColumnOperation moveOp = (MoveColumnOperation) op;
        assertEquals(1, moveOp.getFromIndex());
        assertEquals(2, moveOp.getToIndex());
        assertEquals(2, moveOp.getRowIndices().size());
        assertTrue(moveOp.getRowIndices().contains(0));
        assertTrue(moveOp.getRowIndices().contains(1));
    }

    @Test
    public void testApplyOperation() {
        Table table = new Table();
        table.addRow();
        table.addRow(asList("2", "3"));

        assertEquals(2, table.getHeight());
        assertEquals(2, table.getWidth());

        table.apply(new MoveColumnOperation(asList(0, 1), 0, 1));

        assertEquals("3", table.getValueAt(1, 0));
        assertEquals("2", table.getValueAt(1, 1));
        assertEquals(2, table.getHeight());
        assertEquals(2, table.getWidth());

        table.apply(new MoveColumnOperation(asList(0, 1), 1, 0));

        assertEquals("2", table.getValueAt(1, 0));
        assertEquals("3", table.getValueAt(1, 1));
        assertEquals(2, table.getHeight());
        assertEquals(2, table.getWidth());
    }

}
