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
public class TaggedRow extends Row {

    private final RowTag tag;

    private Cell tagCell;

    public TaggedRow(Table table, RowTag tag) {
        super(table, findRowIn(table, tag));
        this.tag = tag;
        if (!exists()) {
            this.tagCell = addCell();
        }
    }

    private static <T extends RowTag> int findRowIn(Table table, T t) {
        for (int i = 0; i < table.getRowCount(); i++) {
            String s = table.getValueAt(i, 0).getValue();
            if (t.getName().equals(s)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Cell cellAt(int index) {
        return super.cellAt(index + 1);
    }

    @Override
    int getColumnCount() {
        int count = super.getColumnCount();
        return count == 0 ? 0 : count - 1;
    }

    @Override
    public void setValueFor(Cell cell, String s) {
        if (!exists()) {
            super.setValueFor(tagCell, tag.getName());
        }
        super.setValueFor(cell, s);
    }

}
