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

package uk.ac.ebi.fg.annotare2.magetab.base.operation;

import com.google.common.annotations.GwtCompatible;
import uk.ac.ebi.fg.annotare2.magetab.base.Table;

import java.util.ArrayList;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class MoveColumnOperation implements Operation {

    private ArrayList<Integer> rowIndices = new ArrayList<Integer>();

    private int fromIndex;

    private int toIndex;

    public MoveColumnOperation() {
        //Required by GWT serialization policy
    }

    public MoveColumnOperation(ArrayList<Integer> rowIndices, int fromColIndex, int toColIndex) {
        this.rowIndices.addAll(rowIndices);
        this.fromIndex = fromColIndex;
        this.toIndex = toColIndex;
    }

    @Override
    public void apply(Table table) {
        table.apply(this);
    }

    public ArrayList<Integer> getRowIndices() {
        return rowIndices;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public int getToIndex() {
        return toIndex;
    }
}
