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

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author Olga Melnichuk
 */
public class RemoveColumnOperationTest {


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
        table.addRow(asList(ROW_TAG_1.getName()));
        table.addRow(asList(ROW_TAG_2.getName(), "2"));

        RowSet rowSet = new RowSet(ROW_TAG_1, ROW_TAG_2);
        rowSet.addAll(table);

        table.addChangeListener(new ChangeListener() {
            @Override
            public void onChange(Operation operation) {
                operations.offer(operation);
            }
        });

        rowSet.removeColumn(0);
        assertFalse(operations.isEmpty());

        Operation op = operations.poll();
        assertTrue(op instanceof RemoveColumnOperation);

        RemoveColumnOperation removeOp = (RemoveColumnOperation) op;
        assertEquals(1, removeOp.getColumnIndex());
        assertEquals(2, removeOp.getRowIndices().size());
        assertTrue(removeOp.getRowIndices().contains(0));
        assertTrue(removeOp.getRowIndices().contains(1));
    }

    @Test
    public void testApplyOperation() {
        Table table = new Table();
        table.addRow();
        table.addRow(asList("2"));

        assertEquals(2, table.getHeight());
        assertEquals(1, table.getWidth());

        table.apply(new RemoveColumnOperation(asList(0, 1), 0));

        assertEquals(2, table.getHeight());
        assertEquals(0, table.getWidth());
    }

}
