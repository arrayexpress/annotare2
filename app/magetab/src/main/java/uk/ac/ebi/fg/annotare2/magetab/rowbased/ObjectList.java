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

package uk.ac.ebi.fg.annotare2.magetab.rowbased;

import com.google.common.annotations.GwtCompatible;
import uk.ac.ebi.fg.annotare2.magetab.table.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public abstract class ObjectList<T> {

    private final RowSet rowSet;

    private final ObjectCreator<T> creator;

    private List<T> list = new ArrayList<T>();

    public ObjectList(RowSet rowSet, ObjectCreator<T> creator) {
        this.rowSet = rowSet;
        this.creator = creator;
        createObjects();
    }

    public ArrayList<T> getAll() {
        ArrayList<T> copy = new ArrayList<T>();
        copy.addAll(list);
        return copy;
    }

    public T get(int index) {
        return list.get(index);
    }

    public T add() {
        T t = creator.create(rowSet.getColumn(list.size()));
        list.add(t);
        return t;
    }

    public void remove(List<Integer> indices) {
        if (indices.isEmpty()) {
            return;
        }
        list = GwtQuirks.remove(list, indices);
        rowSet.removeColumn(indices);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    private void createObjects() {
        for (int i = 0; i < rowSet.getWidth(); i++) {
            list.add(creator.create(rowSet.getColumn(i)));
        }
    }
}
