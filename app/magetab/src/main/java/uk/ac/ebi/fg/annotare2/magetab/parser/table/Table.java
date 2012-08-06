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

import com.google.common.base.Predicate;
import com.google.common.primitives.Ints;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Ordering.from;

/**
 * @author Olga Melnichuk
 */
class Table {

    static class Value {

        private String value;

        private String error;

        private Value(String value) {
            this.value = value;
        }

        private Value(String value, String error) {
            this.value = value;
            this.error = error;
        }

        public String getValue() {
            return value;
        }

        public String getError() {
            return error;
        }

        public boolean isEmpty() {
            return isNullOrEmpty(value) && isNullOrEmpty(error);
        }
    }

    private int rowCount;

    private Map<Index, Value> values = new HashMap<Index, Value>();

    int getRowCount() {
        return rowCount;
    }

    int lastColumnIndex(final int rIndex) {
        List<Index> ordered = from(Index.COMPARE_BY_COLUMN).reverse().sortedCopy(
                filter(values.keySet(), new Predicate<Index>() {
                    public boolean apply(@Nullable Index input) {
                        return input.getRow() == rIndex;
                    }
                })
        );
        return ordered.isEmpty() ? 0 : ordered.get(0).getCol();
    }

    int addRow(Collection<String> values) {
        int rIndex = rowCount++;
        int cIndex = 0;
        for (String v : values) {
            setValueAt(rIndex, cIndex, v);
            cIndex++;
        }
        return rIndex;
    }

    void setValueAt(int rIndex, int cIndex, String value) {
        Index index = indexFor(rIndex, cIndex);
        setValueAt(index, new Value(value));
    }

    void setErrorAt(int rIndex, int cIndex, String error) {
        Index index = indexFor(rIndex, cIndex);
        Value v = values.get(index);
        setValueAt(index,
                v == null ?
                        new Value("", error) :
                        new Value(v.getValue(), error));
    }

    Value getValueAt(int rIndex, int cIndex) {
        return values.get(new Index(rIndex, cIndex));
    }

    List<TableCell> getCells() {
        List<TableCell> cells = newArrayList();
        for(Index i : values.keySet()) {
            Value v = values.get(i);
            cells.add(new TableCell(i.getRow(), i.getCol(), v.getValue(), v.getError()));
        }
        return cells;
    }

    private void setValueAt(Index index, Value v) {
        if (v.isEmpty()) {
            values.remove(index);
        } else {
            values.put(index, v);
        }
    }

    private Index indexFor(int rIndex, int cIndex) {
        if (rIndex < 0 || rIndex > rowCount) {
            throw new IndexOutOfBoundsException("Row index " + rIndex + " is out of range [0," + rowCount + "]");
        }
        if (cIndex < 0) {
            throw new IndexOutOfBoundsException("Column index could not be less than 0: " + cIndex);
        }
        return new Index(rIndex, cIndex);
    }

    /* private TableCell addCell(int rIndex, int cIndex, String value) {
        columnCount = max(cIndex + 1, columnCount);

        TableCell cell = new TableCell(rIndex, cIndex, value);
        cells.put(new Index(rIndex, cIndex), cell);
        return cell;
    }

    public TableCell getCell(int rIndex, int cIndex) {
        TableCell cell = cells.get(new Index(rIndex, cIndex));
        if (cell == null) {
            cell = addCell(rIndex, cIndex, "");
        }
        return cell;
    }

    @VisibleForTesting
    Map<Index, TableCell> getCells() {
        return cells;
    }*/

    private static class Index {

        private static Comparator<Index> COMPARE_BY_ROW = new Comparator<Index>() {
            public int compare(Index o1, Index o2) {
                return Ints.compare(o1.getRow(), o2.getRow());
            }
        };

        private static Comparator<Index> COMPARE_BY_COLUMN = new Comparator<Index>() {
            public int compare(Index o1, Index o2) {
                return Ints.compare(o1.getCol(), o2.getCol());
            }
        };

        private int row;

        private int col;

        Index(int row, int col) {
            checkArgument(row >= 0, "Row Location can't be negative");
            checkArgument(col >= 0, "Column Location can't be negative");
            this.row = row;
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Index)) return false;

            Index index = (Index) o;

            if (col != index.col) return false;
            if (row != index.row) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = row;
            result = 31 * result + col;
            return result;
        }
    }
}
