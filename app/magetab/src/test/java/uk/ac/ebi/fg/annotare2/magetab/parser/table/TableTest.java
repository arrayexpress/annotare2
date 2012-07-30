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

package uk.ac.ebi.fg.annotare2.magetab.parser.table;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.primitives.Ints;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Olga Melnichuk
 */
public class TableTest {

    @Test
    public void test() {
        testTable(0, 0);
        testTable(0, 1, 2, 3);
        testTable(0, 1, 2, 3, 0, 2);
    }

    private void testTable(int... rows) {
        Table table = new Table();
        int i = 0;
        for (int row : rows) {
            table.addRow(i++, list(row));
        }
        assertTableEquals(table, ignoreZeros(Ints.asList(rows)));
    }

    private List<Integer> ignoreZeros(List<Integer> ints) {
        return new ArrayList<Integer>(Collections2.filter(ints, new Predicate<Integer>() {
            public boolean apply(@Nullable Integer input) {
                return input != 0;
            }
        }));
    }

    private void assertTableEquals(Table table, List<Integer> rows) {
        if (rows.isEmpty()) {
            assertEquals(0, table.getRowCount());
            return;
        }

        assertEquals(rows.size(), table.getRowCount());

        int colNumbers = Collections.max(rows);

        for (int i = 0; i < rows.size(); i++) {
            List<TableCell> cells = table.getRow(i);
            assertEquals(colNumbers, cells.size());

            for (int j = 0; j < colNumbers; j++) {
                TableCell cell = cells.get(j);
                if (j < rows.get(i)) {
                    assertEquals(j + "", cell.getValue());
                } else {
                    assertTrue(cell.isEmpty());
                }
            }
        }
    }

    private List<String> list(int size) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            list.add("" + i);
        }
        return list;
    }
}
