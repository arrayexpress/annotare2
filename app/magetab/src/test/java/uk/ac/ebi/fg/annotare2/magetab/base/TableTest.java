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

package uk.ac.ebi.fg.annotare2.magetab.base;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.junit.Test;
import uk.ac.ebi.fg.annotare2.magetab.base.Table;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.primitives.Ints.asList;
import static com.google.common.primitives.Ints.lastIndexOf;
import static org.junit.Assert.*;

/**
 * @author Olga Melnichuk
 */
public class TableTest {

    @Test
    public void testTableCreation() {
        testTable(
                asList(),
                asList());

        testTable(
                asList(),
                asList(0),
                asList(0, 1),
                asList(0, 1, 2));

        testTable(
                asList(),
                asList(0),
                asList(0, 1),
                asList(0, 1, 2),
                asList(),
                asList(0, 1));
    }

    @Test
    public void testTableChanges() {
        final Table table1 = new Table();
        final Table table2 = new Table();

        table1.addChangeListener(new ChangeListener() {
            @Override
            public void onChange(List<Operation> operations) {
                table2.applyChanges(operations);
            }
        });

        table1.addRow(Arrays.asList("1", "2", "3"));
        table1.addRow(Arrays.asList("1", "2", "3"));

        assertEquals(table1.getRowCount(), table2.getRowCount());
        assertEquals(table1.lastColumnIndex(), table2.lastColumnIndex());

        for (int i = 0; i < table1.getRowCount(); i++) {
            int maxIndex = table1.lastColumnIndex(i);
            assertEquals(maxIndex, table2.lastColumnIndex(i));
            for (int j = 0; j <= maxIndex; j++) {
                Table.Value v1 = table1.getValueAt(i, j);
                Table.Value v2 = table2.getValueAt(i, j);
                assertEquals(v1, v2);
            }
        }
    }

    private void testTable(List<Integer>... rows) {
        Table table = new Table();
        List<List<String>> stringRows = newArrayList();
        for (List<Integer> row : rows) {
            List<String> strings = newArrayList(transform(row, new Function<Integer, String>() {
                public String apply(@Nullable Integer input) {
                    return input.toString();
                }
            }));
            table.addRow(strings);
            stringRows.add(strings);
        }
        assertTableEquals(table, stringRows);
    }

    private void assertTableEquals(Table table, List<List<String>> rows) {
        if (rows.isEmpty()) {
            assertEquals(0, table.getRowCount());
            return;
        }

        int columnCount = Collections.max(
                transform(rows, new Function<List<String>, Integer>() {
                    public Integer apply(@Nullable List<String> input) {
                        return input.size();
                    }
                }));

        assertEquals(rows.size(), table.getRowCount());

        for (int i = 0; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            for (int j = 0; j < columnCount; j++) {
                Table.Value cell = table.getValueAt(i, j);
                if (j < row.size()) {
                    assertNotNull(cell);
                    assertEquals(row.get(j), cell.getValue());
                } else {
                    assertNull(cell);
                }
            }
        }
    }
}
