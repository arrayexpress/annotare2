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

import com.google.common.annotations.GwtCompatible;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.magetab.table.TaggedRow.shift;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class RowSet {

    private ArrayList<RowTag> tags = new ArrayList<RowTag>();

    private HashMap<RowTag, TaggedRow> map;

    private Table table;

    public RowSet(RowTag... tags) {
        for (RowTag t : tags) {
            this.tags.add(t);
        }
    }

    public RowSet from(Table table) {
        this.table = table;
        this.map = new HashMap<RowTag, TaggedRow>();

        for (RowTag tag : tags) {
            map.put(tag, new TaggedRow(table, tag));
        }
        return this;
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
        ArrayList<Integer> shifted = new ArrayList<Integer>();
        for (Integer i : indices) {
            shifted.add(shift(i));
        }
        table.removeColumn(rows(), shifted);
    }

    public void moveColumn(int fromIndex, int toIndex) {
        ArrayList<Integer> indices = new ArrayList<Integer>();
        indices.add(fromIndex);
        indices.add(toIndex);
        checkColumnIndices(indices);
        table.moveColumn(rows(), shift(fromIndex), shift(toIndex));
    }

    public HashMap<RowTag, Cell<String>> addColumn() {
        return getColumn(getWidth());
    }

    public HashMap<RowTag, Cell<String>> getColumn(int i) {
        HashMap<RowTag, Cell<String>> column = new HashMap<RowTag, Cell<String>>();
        for (RowTag tag : tags) {
            column.put(tag, map.get(tag).cellAt(i));
        }
        return column;
    }

    private void checkColumnIndices(List<Integer> indices) {
        for (int i : indices) {
            if (i < 0) {
                throw new IndexOutOfBoundsException("Column index could not be negative: " + i);
            }
        }
    }

    private List<Row> rows() {
        List<Row> rows = new ArrayList<Row>();
        for (TaggedRow r : map.values()) {
            Row rr = r.getRow();
            if (rr != null) {
                rows.add(rr);
            }
        }
        return rows;
    }
}
