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

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class TaggedRow {

    private final RowTag tag;

    private final Row row;

    private Row.Cell<String> tagCell;

    public TaggedRow(Table table, RowTag tag) {
        this.row = findRow(table, tag);
        this.tag = tag;
        this.tagCell = this.row.cellAt(0);
    }

    private static Row findRow(Table table, RowTag tag) {
        for (int i = 0; i < table.getHeight(); i++) {
            Row r = table.getRow(i);
            if (tag.getName().equals(r.getValue(0))) {
                return r;
            }
        }
        return table.addRow();
    }

    public Row.Cell<String> cellAt(int index) {
        final Row.Cell<String> cell = row.cellAt(shift(index));
        return new Row.Cell<String>() {

            @Override
            public void setValue(String s) {
                if (tagCell.isEmpty()) {
                    tagCell.setValue(tag.getName());
                }
                cell.setValue(s);
            }

            @Override
            public String getValue() {
                return cell.getValue();
            }

            @Override
            public boolean isEmpty() {
                return cell.isEmpty();
            }
        };
    }

    static int shift(int idx){
        if (idx < 0) {
            throw new IndexOutOfBoundsException("Column index could not be negative:" + idx);
        }
        // zero index is reserved for the tag
        return idx + 1;
    }

    int getSize() {
        int size = row.getSize();
        return  size > 0 ? size - 1 : 0;
    }

    Row getRow() {
        return row;
    }
}
