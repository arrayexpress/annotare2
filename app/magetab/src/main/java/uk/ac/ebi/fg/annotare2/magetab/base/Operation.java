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

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public abstract class Operation implements Serializable {

    public static Operation addRow() {
        return new AddRowOperation();
    }

    public static Operation updateCell(int row, int col, String value) {
        return new UpdateCellOperation(row, col, value);
    }

    abstract void apply(Table table);

    public static class AddRowOperation extends Operation implements Serializable {

        public AddRowOperation() {
            // required by GWT serialization policy
        }

        @Override
        void apply(Table table) {
            table.addRow();
        }
    }

    public static class UpdateCellOperation extends Operation implements Serializable {

        private int row;

        private int col;

        private String value;

        public UpdateCellOperation() {
            // required by GWT serialization policy
        }

        public UpdateCellOperation(int row, int col, String value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }

        @Override
        void apply(Table table) {
            table.setValueAt(row, col, value);
        }
    }
}
