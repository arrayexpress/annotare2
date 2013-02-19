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

package uk.ac.ebi.fg.annotare2.magetab.table.operation;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.magetab.table.ChangeListener;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;

import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.ac.ebi.fg.annotare2.magetab.TestUtils.asList;

/**
 * @author Olga Melnichuk
 */
public class UpdateCellOperationTest {

    @Test
    public void testHandleOperation() {
        final Queue<Operation> operations = new ArrayDeque<Operation>();
        Table table = new Table();
        table.addRow(asList(""));
        table.addRow(asList("2"));

        table.addChangeListener(new ChangeListener() {
            @Override
            public void onChange(Operation operation) {
                operations.offer(operation);
            }
        });

        table.setValueAt(0, 0, "1");
        assertFalse(operations.isEmpty());

        Operation op = operations.poll();
        assertTrue(op instanceof UpdateCellOperation);

        UpdateCellOperation updateOp = (UpdateCellOperation) op;
        assertEquals(0, updateOp.getColumnIndex());
        assertEquals(0, updateOp.getRowIndex());
    }

    @Test
    public void testApplyOperation() {
        Table table = new Table();
        table.addRow();
        table.addRow(asList("2"));

        assertEquals(2, table.getHeight());
        assertEquals(1, table.getWidth());

        table.apply(new UpdateCellOperation(1, 0, "3"));

        assertEquals("3", table.getValueAt(1, 0));
        assertEquals(2, table.getHeight());
        assertEquals(1, table.getWidth());
    }

}
