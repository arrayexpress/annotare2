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
import uk.ac.ebi.fg.annotare2.magetab.base.operation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class Table implements Serializable, RowChangeListener {

    private List<Row> rows = new ArrayList<Row>();

    private transient List<ChangeListener> listeners = new ArrayList<ChangeListener>();

    public Table() {
        // required by GWT serialization policy
    }

    public int getHeight() {
        return rows.size();
    }

    public int getWidth() {
        int w = 0;
        for (Row r : rows) {
            int rowWidth = r.getSize();
            w = w < rowWidth ? rowWidth : w;
        }
        return w;
    }

    public int getTrimmedWidth() {
        int w = 0;
        for (Row r : rows) {
            int rowWidth = r.getTrimmedSize();
            w = w < rowWidth ? rowWidth : w;
        }
        return w;
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    public Row addRow(Collection<String> strings) {
        Row row = new Row(this);
        rows.add(row);
        int colIndex = 0;
        for (String s : strings) {
            if (!isNullOrEmpty(s)) {
                row.setValue(colIndex, s, true);
            }
            colIndex++;
        }
        return row;
    }

    public Row addRow() {
        Row row = new Row(this);
        rows.add(row);
        return row;
    }

    public void removeColumn(Collection<Row> rowSet, int colIndex) {
        doRemoveColumn(rowSet, colIndex);
        notifyListeners(Operations.removeColumn(toRowIndices(rowSet), colIndex));
    }

    public void moveColumn(Collection<Row> rowSet, int fromColIndex, int toColIndex) {
        if (fromColIndex == toColIndex) {
            return;
        }
        doMoveColumn(rowSet, fromColIndex, toColIndex);
        notifyListeners(Operations.moveColumn(toRowIndices(rowSet), fromColIndex, toColIndex));
    }

    public void setValueAt(int rIndex, int cIndex, String value) {
        doUpdateCell(rIndex, cIndex, value, true);
    }


    public String getValueAt(int rIndex, int cIndex) {
        checkRowIndex(rIndex);
        return rows.get(rIndex).getValue(cIndex);
    }

    public Row getRow(int rIndex) {
        checkRowIndex(rIndex);
        return rows.get(rIndex);
    }

    @Override
    public void onRowValueChange(Row row, int columnIndex, String newValue) {
        notifyListeners(Operations.updateCell(rows.indexOf(row), columnIndex, newValue));
    }

    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    public void apply(UpdateCellOperation op) {
        doUpdateCell(op.getRowIndex(), op.getColumnIndex(), op.getValue(), false);
    }

    public void apply(RemoveColumnOperation op) {
        doRemoveColumn(toRows(op.getRowIndices()), op.getColumnIndex());
    }

    public void apply(MoveColumnOperation op) {
        doMoveColumn(toRows(op.getRowIndices()), op.getFromIndex(), op.getToIndex());
    }

    private void notifyListeners(Operation change) {
        for (ChangeListener listener : listeners) {
            listener.onChange(change);
        }
    }

    private List<Integer> toRowIndices(Collection<Row> rowSet) {
        List<Integer> rowIndices = new ArrayList<Integer>();
        for (Row r : rowSet) {
            rowIndices.add(rows.indexOf(r));
        }
        return rowIndices;
    }

    private List<Row> toRows(List<Integer> indices) {
        List<Row> rowSet = new ArrayList<Row>();
        for (Integer rowIndex : indices) {
            rowSet.add(rows.get(rowIndex));
        }
        return rowSet;
    }

    private void checkRowIndex(int rIndex) {
        if (rIndex < 0) {
            throw new IndexOutOfBoundsException("Row index is out of bounds [0," + rows.size() + "] : " + rIndex);
        }
        while (rIndex >= rows.size()) {
            addRow();
        }
    }

    private void doUpdateCell(int rIndex, int cIndex, String value, boolean notify) {
        checkRowIndex(rIndex);
        rows.get(rIndex).setValue(cIndex, value, notify);
    }

    private void doRemoveColumn(Collection<Row> rowSet, int colIndex) {
        for (Row r : rowSet) {
            r.removeColumn(colIndex);
        }
    }

    private void doMoveColumn(Collection<Row> rowSet, int fromIndex, int toIndex) {
        for (Row r : rowSet) {
            r.moveColumn(fromIndex, toIndex);
        }
    }
}
