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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class Row {

    private final Table table;

    private int rIndex;

    private final List<Cell> cells = new ArrayList<Cell>();

    public Row(Table table, int rIndex) {
        this.table = table;
        this.rIndex = rIndex;
    }

    public String getValueFor(Cell cell) {
        if (!exists()) {
            return null;
        }
        int cIndex = cells.indexOf(cell);
        Table.Value v = table.getValueAt(rIndex, cIndex);
        return v == null ? null : v.getValue();
    }

    public void setValueFor(Cell cell, String s) {
        if (!exists()) {
            rIndex = table.addRow();
        }
        int cIndex = cells.indexOf(cell);
        table.setValueAt(rIndex, cIndex, s);
    }

    public Cell cellAt(int index) {
        while(index >= cells.size()) {
            addCell();
        }
        return cells.get(index);
    }

    public Cell addCell() {
        Cell cell = new Cell();
        cells.add(cell);
        return cell;
    }

    public void removeCell(Cell cell) {
        int index = cells.indexOf(cell);
        for (int i = index; i < cells.size() -1; i++) {
            String v = getValueFor(cells.get(i));
            table.setValueAt(rIndex, i + 1, v);
        }
        cells.remove(index);
    }

    public boolean exists() {
        return rIndex >=0 && rIndex < table.getRowCount();
    }

    int getColumnCount() {
        return exists() ? table.lastColumnIndex(rIndex) + 1 : 0;
    }

    public class Cell {

        public void setValue(String s) {
            setValueFor(this, s);
        }

        public String getValue() {
            return getValueFor(this);
        }
    }
}
