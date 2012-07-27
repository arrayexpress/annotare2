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

package uk.ac.ebi.fg.annotare2.magetab.parser.table;

import javax.annotation.Nonnull;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.Math.max;

/**
 * @author Olga Melnichuk
 */
class Table {

    private int ncols;

    private final List<Row> rows = new ArrayList<Row>();

    public void addRow(int line, Collection<String> values) {
        if (values.isEmpty()) {
            return;
        }
        Row row = new Row(line, values);
        rows.add(row);
        ncols = max(ncols, row.ncols());
    }

    public int getRowCount() {
       return rows.size();
    }

    public List<Cell> getRow(int i) {
        return rows.get(i).getCells(ncols);
    }

    public static class Row {
        private final int line;
        private final Map<Integer, Cell> cells = new LinkedHashMap<Integer, Cell>();
        private int maxColIndex = 0;

        public Row(int line, Collection<String> values) {
            this.line = line;

            int vc = -1;
            for (String v : values) {
                vc++;
                v = v == null ? "" : v.trim();
                if (!isNullOrEmpty(v)) {
                    Cell cell = Cell.notEmpty(line, vc, v);
                    cells.put(vc, cell);
                    maxColIndex = vc;
                }
            }
        }

        public List<Cell> getCells(int ncols) {
            List<Cell> out = new ArrayList<Cell>();
            for(int i=0; i<ncols; i++) {
                Cell cell = cells.get(i);
                if (cell == null) {
                    cell = Cell.empty(line, i);
                }
                out.add(cell);
            }
            return out;
        }

        int ncols() {
           return maxColIndex + 1;
        }
    }

    public static class Cell {
        private final int line;
        private final int col;
        private final String value;

        private Cell(int line, int col, String value) {
            this.line = line;
            this.col = col;
            this.value = value;
        }

        public int getLine() {
            return line;
        }

        public int getCol() {
            return col;
        }

        public String getValue() {
            return value;
        }

        public static Cell empty(int line, int col) {
            return new Cell(line, col, "");
        }

        public static Cell notEmpty(int line, int col, @Nonnull String value) {
            checkNotNull(value);
            return new Cell(line, col, value);
        }

        public boolean isEmpty() {
            return isNullOrEmpty(value);
        }
    }
}
