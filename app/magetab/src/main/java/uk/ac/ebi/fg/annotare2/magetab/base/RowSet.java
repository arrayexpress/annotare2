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

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class RowSet {

    private final Map<RowTag, Integer> map = new HashMap<RowTag, Integer>();

    private final List<Row> rows = new ArrayList<Row>();

    private final List<RowTag> tags = new ArrayList<RowTag>();

    private int columnCount = 0;

    public RowSet(RowTag... tags) {
        this.tags.addAll(Arrays.asList(tags));
    }

    public void addAll(Table table) {
        int count = 0;
        for (RowTag t : tags) {
            TaggedRow row = new TaggedRow(table, t);
            map.put(t, rows.size());
            rows.add(row);
            count = Math.max(count, row.getColumnCount());
        }
        columnCount = count;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void removeColumn(int i) {
       for(Row row : rows) {
          row.removeCell(row.cellAt(i));
       }
    }

    public void moveColumn(int i) {
       // TODO
    }

    public int addColumn() {
        return columnCount++;
    }

    public Row rowAt(RowTag tag) {
        return rows.get(map.get(tag));
    }
}
