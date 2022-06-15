/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
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

    public void cleanUp(){
        int rowWidth = getWidth();
        for(int i = 1; i<rows.size(); i++){
            int noOfFactorValueColums = 0;
            String factorValueColHeader;
            String factorValue;
            for(int j = 0; j<rowWidth; j++){
                if(rows.get(0).getValue(j).contains("Factor Value")){
                    factorValueColHeader = rows.get(0).getValue(j);
                    factorValue = rows.get(i).getValue(j);
                    if(!isUnassignedOrEmpty(factorValue)){
                        noOfFactorValueColums ++;
                        rows.get(0).setValue(rowWidth+noOfFactorValueColums-1, factorValueColHeader);
                        rows.get(i).setValue(rowWidth+noOfFactorValueColums-1, factorValue);
                        rows.get(i).setValue(j, null);
                    }
                }
            }
        }
        for(int i = 0; i < rowWidth; i++){
            if(isUnassignedOrEmpty(rows.get(1).getValue(i))){
                boolean emptyColumn = true;
                for(int j = 2; j<rows.size(); j++){
                    if(!(isUnassignedOrEmpty(rows.get(j).getValue(i)))){
                        emptyColumn = false;
                        break;
                    }
                }
                if(emptyColumn){
                    int reIndex = i; //created new variable to make it effectively final to use in Lambda
                    rows.forEach(r-> r.removeValue(reIndex));
                    i--; rowWidth--;
                }

            }
        }
    }

    private boolean isUnassignedOrEmpty(String value) {
        return value == null || value.isEmpty() || value.startsWith("____UNASSIGNED____");
    }
}
