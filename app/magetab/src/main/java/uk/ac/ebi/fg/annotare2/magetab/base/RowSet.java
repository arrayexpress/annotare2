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

import com.google.common.annotations.GwtCompatible;

import java.util.*;

import static java.util.Arrays.asList;
import static uk.ac.ebi.fg.annotare2.magetab.base.TaggedRow.shift;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class RowSet {

    private List<RowTag> tags = new ArrayList<RowTag>();

    private Map<RowTag, TaggedRow> map;

    private Table table;

    public RowSet(RowTag... tags) {
        this.tags.addAll(Arrays.asList(tags));
    }

    public void addAll(Table table) {
        this.table = table;
        this.map = new HashMap<RowTag, TaggedRow>();

        for (RowTag tag : tags) {
            map.put(tag, new TaggedRow(table, tag));
        }
    }

    public int getWidth() {
        int res = 0;
        for (TaggedRow r : map.values()) {
            int size = r.getSize();
            res = res < size ? size : res;
        }
        return res;
    }

    public void removeColumn(List<Integer> indices) {
        checkColumnIndices(indices);
        List<Integer> shifted = new ArrayList<Integer>();
        for (Integer i : indices) {
            shifted.add(shift(i));
        }
        table.removeColumn(rows(), shifted);
    }

    public void moveColumn(int fromIndex, int toIndex) {
        checkColumnIndices(asList(fromIndex, toIndex));
        table.moveColumn(rows(), shift(fromIndex), shift(toIndex));
    }

    public Map<RowTag, Row.Cell<String>> addColumn() {
        return getColumn(getWidth());
    }

    public Map<RowTag, Row.Cell<String>> getColumn(int i) {
        Map<RowTag, Row.Cell<String>> column = new HashMap<RowTag, Row.Cell<String>>();
        for (RowTag tag : tags) {
            column.put(tag, map.get(tag).cellAt(i));
        }
        return column;
    }

    private void checkColumnIndices(List<Integer> indices) {
        int width = getWidth();
        for (int i : indices) {
            if (i < 0 || i >= width) {
                throw new IndexOutOfBoundsException("Column index is out of bounds [0, " + width + "): " + i);
            }
        }
    }

    private List<Row> rows() {
        List<Row> rows = new ArrayList<Row>();
        for (TaggedRow r : map.values()) {
            rows.add(r.getRow());
        }
        return rows;
    }
}
