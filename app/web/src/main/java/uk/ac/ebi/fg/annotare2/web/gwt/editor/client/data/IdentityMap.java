/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data;

import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.HasIdentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public abstract class IdentityMap<T extends HasIdentity> {

    private int nextId = 0;

    // real_or_temporary_id -> object
    private Map<Integer, T> map;

    // real_id -> temporary_id
    private Map<Integer, Integer> idMap;

    private List<Integer> order;

    public boolean isInitialized() {
        return map != null;
    }

    public void init(List<T> objects) {
        idMap = new HashMap<Integer, Integer>();
        map = new HashMap<Integer, T>();
        order = new ArrayList<Integer>();
        for (T obj : objects) {
            map.put(obj.getId(), obj);
            order.add(obj.getId());
        }
    }

    public List<T> values() {
        return new ArrayList<T>(map.values());
    }

    public T create() {
        T created = create(nextId());
        map.put(created.getId(), created);
        order.add(created.getId());
        return created;
    }

    public void update(T updated) {
        if (updated.getId() != updated.getTmpId()) {
            idMap.put(updated.getId(), updated.getTmpId());
        }
        map.put(getId(updated), updated);
    }

    public void remove(T removed) {
        int id = getId(removed);
        map.remove(id);
        order.remove(Integer.valueOf(id));
        idMap.remove(id);
    }

    public T find(T obj) {
        return map.get(getId(obj));
    }

    private int getId(T obj) {
        Integer id = idMap.get(obj.getId());
        return id == null ? obj.getId() : id;
    }

    private int nextId() {
        return --nextId;
    }

    abstract protected T create(int tmpId);
}
