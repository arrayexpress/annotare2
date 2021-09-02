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

/**
 * @author Olga Melnichuk
 */
public class Row implements IsSerializable {

    private List<String> values = new ArrayList<String>();

    public int getSize() {
        return values.size();
    }

    public int getTrimmedSize() {
        int idx = -1;
        for (int i = 0; i < values.size(); i++) {
            String v = values.get(i);
            if (!isEmpty(v)) {
                idx = i;
            }
        }
        return idx + 1;
    }

    public String getValue(int colIndex) {
        checkColumnIndex(colIndex);
        return values.get(colIndex);
    }

    public void setValue(int colIndex, String value) {
        checkColumnIndex(colIndex);
        values.set(colIndex, value);
    }

    public void removeValue(int colIndex){
        checkColumnIndex(colIndex);
        values.remove(colIndex);
    }

    private void checkColumnIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Column index can not be less than zero: " + index);
        }
        while (index >= values.size()) {
            values.add(null);
        }
    }

    private static boolean isEmpty(String v) {
        return v == null || v.isEmpty();
    }
}


