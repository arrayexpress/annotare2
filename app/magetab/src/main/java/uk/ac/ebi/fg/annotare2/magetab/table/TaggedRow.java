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

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class TaggedRow {

    private final RowTag tag;

    private Row row;

    private final Table table;

    public TaggedRow(Table table, RowTag tag) {
        this.row = findRow(table, tag);
        this.tag = tag;
        this.table = table;
    }

    private static Row findRow(Table table, RowTag tag) {
        for (int i = 0; i < table.getHeight(); i++) {
            Row r = table.getRow(i);
            if (tag.getName().equals(r.getValue(0))) {
                return r;
            }
        }
        return null;
    }

    private Cell<String> getCell(int index, boolean create) {
        if (row == null) {
            if (create) {
                row = table.addRow();
                row.cellAt(0).setValue(tag.getName());
            } else {
                return null;
            }
        }
        return row.cellAt(shift(index));
    }

    public Cell<String> cellAt(final int index) {
        return new Cell<String>() {

            @Override
            public void setValue(String s) {
                getCell(index, true).setValue(s);
            }

            @Override
            public String getValue() {
                Cell<String> cell = getCell(index, false);
                return cell == null ? null : cell.getValue();
            }

            @Override
            public boolean isEmpty() {
                Cell<String> cell = getCell(index, false);
                return cell == null || cell.isEmpty();
            }
        };
    }

    static int shift(int idx) {
        if (idx < 0) {
            throw new IndexOutOfBoundsException("Column index could not be negative:" + idx);
        }
        // zero index is reserved for the tag
        return idx + 1;
    }

    int getSize() {
        int size = row == null ? 0 : row.getTrimmedSize();
        return size > 0 ? size - 1 : 0;
    }

    Row getRow() {
        return row;
    }
}
