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

import com.google.common.annotations.GwtCompatible;
import uk.ac.ebi.fg.annotare2.magetab.base.Row;
import uk.ac.ebi.fg.annotare2.magetab.base.RowSet;
import uk.ac.ebi.fg.annotare2.magetab.base.RowTag;
import uk.ac.ebi.fg.annotare2.magetab.base.Table;

import java.util.*;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public abstract class ObjectList<T> {

    private final List<RowTag> tags = new ArrayList<RowTag>();

    private final RowSet rowSet;

    private final List<T> list = new ArrayList<T>();

    public ObjectList(Table table, RowTag... rowTags) {
        tags.addAll(asList(rowTags));
        rowSet = new RowSet(rowTags);
        rowSet.addAll(table);
        for (int i=0; i<rowSet.getColumnCount(); i++) {
            list.add(create(i));
        }
    }

    public List<T> getAll() {
        return Collections.unmodifiableList(list);
    }

    public T get(int index) {
        return list.get(index);
    }

    public T add() {
        int column = rowSet.addColumn();
        T t = create(column);
        list.add(t);
        return t;
    }

    public void remove(T t) {
        list.indexOf(t);
        remove(list.indexOf(t));
    }

    public void remove(int index) {
        rowSet.removeColumn(index);
        list.remove(index);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    private T create(int i) {
        Map<RowTag, Row.Cell> map = new HashMap<RowTag, Row.Cell>();
        for (RowTag tag : tags) {
            map.put(tag, rowSet.rowAt(tag).cellAt(i));
        }
        return create(map);
    }

    protected abstract T create(Map<RowTag, Row.Cell> map);
}
