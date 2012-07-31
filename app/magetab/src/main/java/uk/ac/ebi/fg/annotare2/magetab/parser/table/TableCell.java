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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class TableCell {

    private final int line;

    private final int column;

    private final String value;

    private String error;

    private TableCell(int line, int column, String value) {
        this.line = line;
        this.column = column;
        this.value = value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getValue() {
        return value;
    }

    public String getError() {
        return error;
    }

    public void setError(String msg) {
        error = msg;
    }

    public void clearError() {
        error = null;
    }

    public boolean isEmpty() {
        return isNullOrEmpty(value);
    }

    public static TableCell createEmptyCell(int line, int col) {
        return new TableCell(line, col, null);
    }

    public static TableCell createCell(int line, int col, @Nonnull String value) {
        checkNotNull(value);
        return new TableCell(line, col, value);
    }
}
