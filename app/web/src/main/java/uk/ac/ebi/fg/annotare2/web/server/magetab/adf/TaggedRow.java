/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.server.magetab.adf;

import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Row;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;

/**
 * @author Olga Melnichuk
 */
class TaggedRow {

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

    public String getValue(int index) {
        return row == null ? null : row.getValue(corrected(index));
    }

    public void setValue(int index, String value) {
        if (row == null) {
            row = table.addRow();
            row.setValue(0, tag.getName());
        }
        row.setValue(corrected(index), value);
    }

    static int corrected(int idx) {
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
