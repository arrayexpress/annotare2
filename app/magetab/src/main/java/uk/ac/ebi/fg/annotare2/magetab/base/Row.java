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

import java.io.Serializable;
import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class Row implements Serializable {

    private ArrayList<Value> values = new ArrayList<Value>();

   private Table table;

    public Row() {
        //required by GWT serialization policy
    }

    public Row(Table table) {
        this.table = table;
    }

    public int getSize() {
        return values.size();
    }

    public int getTrimmedSize() {
        for (int i = values.size() - 1; i >= 0; i--) {
            Value v = values.get(i);
            if (!v.isEmpty()) {
                return i + 1;
            }
        }
        return 0;
    }

    public void removeColumn(ArrayList<Integer> colIndices) {
        checkColumnIndices(colIndices);
        values = GwtQuirks.remove(values, colIndices);
    }

    public void moveColumn(int fromIndex, int toIndex) {
        if (fromIndex == toIndex) {
            return;
        }
        checkColumnIndex(fromIndex);
        checkColumnIndex(toIndex);

        Value cell = values.remove(fromIndex);
        if (toIndex < fromIndex) {
            values.add(toIndex, cell);
        } else {
            values.add(toIndex, cell);
        }
    }

    public Cell<String> cellAt(int colIndex) {
        checkColumnIndex(colIndex);
        final Value v = values.get(colIndex);

        return new Cell<String>() {
            @Override
            public void setValue(String s) {
                setValueFor(v, s, true);
            }

            @Override
            public String getValue() {
                return v.get();
            }

            @Override
            public boolean isEmpty() {
                return v.isEmpty();
            }
        };
    }

    public String getValue(int colIndex) {
        checkColumnIndex(colIndex);
        return values.get(colIndex).get();
    }

    public void setValue(int colIndex, String value, boolean notify) {
        checkColumnIndex(colIndex);
        setValueFor(values.get(colIndex), value, notify);
    }

    private void setValueFor(Value v, String newValue, boolean notify) {
        v.set(newValue);
        if (notify) {
            notifyValueUpdated(values.indexOf(v), newValue);
        }
    }

    private void notifyValueUpdated(int colIndex, String newValue) {
        if (table != null) {
            table.notifyRowValueUpdated(this, colIndex, newValue);
        }
    }

    private void checkColumnIndices(ArrayList<Integer> indices) {
        for (Integer i : indices) {
            checkColumnIndex(i);
        }
    }

    private void checkColumnIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Column index can not be less than zero: " + index);
        }
        while (index >= values.size()) {
            values.add(new Value());
        }
    }

    public static class Value implements Serializable {

        private String value;

        private String error;

        public Value() {
            // required by GWT serialization policy
        }

        public Value(String value) {
            this.value = value;
        }

        public Value(String value, String error) {
            this.value = value;
            this.error = error;
        }

        public String get() {
            return value;
        }

        public void set(String value) {
            this.value = value;
        }

        public String getError() {
            return error;
        }

        public boolean isEmpty() {
            return isNullOrEmpty(value) && isNullOrEmpty(error);
        }
    }

    public static interface Cell<T> {

        public void setValue(T t);

        public T getValue();

        public boolean isEmpty();
    }
}


