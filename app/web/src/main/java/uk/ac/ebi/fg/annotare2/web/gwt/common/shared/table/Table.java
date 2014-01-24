/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class Table implements IsSerializable {

    private List<Row> rows = new ArrayList<Row>();

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

    public Row addRow(List<String> strings) {
        Row row = new Row();
        rows.add(row);
        int colIndex = 0;
        for (String s : strings) {
            if (!isNullOrEmpty(s)) {
                row.setValue(colIndex, s);
            }
            colIndex++;
        }
        return row;
    }

    public Row addRow() {
        Row row = new Row();
        rows.add(row);
        return row;
    }

    public String getValueAt(int rIndex, int cIndex) {
        checkRowIndex(rIndex);
        return rows.get(rIndex).getValue(cIndex);
    }

    public Row getRow(int rIndex) {
        checkRowIndex(rIndex);
        return rows.get(rIndex);
    }

    private void checkRowIndex(int rIndex) {
        if (rIndex < 0) {
            throw new IndexOutOfBoundsException("Row index is out of bounds [0," + rows.size() + "] : " + rIndex);
        }
        while (rIndex >= rows.size()) {
            addRow();
        }
    }
}
