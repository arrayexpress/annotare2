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

import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Math.max;
import static uk.ac.ebi.fg.annotare2.magetab.parser.table.TableCell.createCell;
import static uk.ac.ebi.fg.annotare2.magetab.parser.table.TableCell.createEmptyCell;

/**
 * @author Olga Melnichuk
 */
class Table {

    private int ncols;

    private final List<Row> rows = newArrayList();

    void addRow(int line, Collection<String> values) {
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

    /**
     * Returns a list of cells in the row; the number of cells is always the same.
     *
     * @param i row index
     * @return a list of cells in the row
     */
    public List<TableCell> getRow(int i) {
        return rows.get(i).getCells(ncols);
    }

    public static class Row {
        private final int line;
        private final Map<Integer, TableCell> cells = new LinkedHashMap<Integer, TableCell>();
        private int maxColIndex = 0;

        public Row(int line, Collection<String> values) {
            this.line = line;

            int vc = -1;
            for (String v : values) {
                vc++;
                v = v == null ? "" : v.trim();
                if (!isNullOrEmpty(v)) {
                    TableCell cell = createCell(line, vc, v);
                    cells.put(vc, cell);
                    maxColIndex = vc;
                }
            }
        }

        public List<TableCell> getCells(int ncols) {
            List<TableCell> out = new ArrayList<TableCell>();
            for(int i=0; i<ncols; i++) {
                TableCell cell = cells.get(i);
                if (cell == null) {
                    cell = createEmptyCell(line, i);
                }
                out.add(cell);
            }
            return out;
        }

        int ncols() {
           return maxColIndex + 1;
        }
    }
}
