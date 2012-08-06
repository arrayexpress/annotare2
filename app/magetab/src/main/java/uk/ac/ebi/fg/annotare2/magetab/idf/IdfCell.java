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

package uk.ac.ebi.fg.annotare2.magetab.idf;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public abstract class IdfCell {

    private final IdfRow row;

    private final int columnIndex;

    public IdfCell(IdfRow row, int columnIndex) {
        this.row = row;
        this.columnIndex = columnIndex;
    }

    public String getValue() {
        return getValue(row, columnIndex);
    }

    public void setValue(String value) {
        setValue(row, columnIndex, value);
    }

    public String getError() {
        return getError(row, columnIndex);
    }

    public void setError(String error) {
        setError(row, columnIndex, error);
    }

    public Integer getColumnIndex() {
        return columnIndex;
    }

    public Integer getRowIndex() {
        return getRowIndex(row);
    }

    protected abstract Integer getRowIndex(IdfRow row);

    protected abstract String getValue(IdfRow row, int columnIndex);

    protected abstract void setValue(IdfRow row, int columnIndex, String value);

    protected abstract String getError(IdfRow row, int columnIndex);

    protected abstract void setError(IdfRow row, int columnIndex, String error);

    public boolean isEmpty() {
        return isNullOrEmpty(getValue());
    }
}
