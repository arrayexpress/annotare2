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

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Olga Melnichuk
 */
public class RowTest {

    @Test
    public void testRowSize() {
        Row row = new Row();

        assertEquals(0, row.getSize());
        assertEquals(0, row.getTrimmedSize());

        row.setValue(0, "A", false);
        assertEquals(1, row.getSize());
        assertEquals(1, row.getTrimmedSize());

        row.setValue(1, "B", false);
        assertEquals(2, row.getSize());
        assertEquals(2, row.getTrimmedSize());

        row.setValue(3, "D", false);
        assertEquals(4, row.getSize());
        assertEquals(4, row.getTrimmedSize());

        row.setValue(4, "", false);
        assertEquals(5, row.getSize());
        assertEquals(4, row.getTrimmedSize());

        row.setValue(4, null, false);
        assertEquals(5, row.getSize());
        assertEquals(4, row.getTrimmedSize());
    }

    @Test
    public void testRowChangeNotification() {
        List<Integer> columnsToChange = Arrays.asList(2, 3, 4);
        List<String> valuesToChange = Arrays.asList("A", "B", "C");
        assertEquals(columnsToChange.size(), valuesToChange.size());

        Table table1 = createMock(Table.class);
        replay(table1);

        Row row1 = new Row(table1);
        for (int i = 0; i < columnsToChange.size(); i++) {
            row1.setValue(columnsToChange.get(i), valuesToChange.get(i), false);
        }
        verify(table1);

        Table table2 = createMock(Table.class);
        for (int i = 0; i < columnsToChange.size(); i++) {
            table2.notifyRowValueUpdated(isA(Row.class), eq(columnsToChange.get(i).intValue()), eq(valuesToChange.get(i)));
            expectLastCall().once();
        }
        replay(table2);

        Row row2 = new Row(table2);
        for (int i = 0; i < columnsToChange.size(); i++) {
            row2.setValue(columnsToChange.get(i), valuesToChange.get(i), true);
        }
        verify(table2);
    }

    @Test
    public void testRowValues() {
        Row row = new Row();

        row.setValue(0, "A", false);
        assertEquals("A", row.getValue(0));

        row.setValue(0, "", false);
        assertEquals("", row.getValue(0));

        row.setValue(0, null, false);
        assertEquals(null, row.getValue(0));
    }
}
