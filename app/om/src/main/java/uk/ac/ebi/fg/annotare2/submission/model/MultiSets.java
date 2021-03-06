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

package uk.ac.ebi.fg.annotare2.submission.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newLinkedHashSet;

/**
 * @author Olga Melnichuk
 */
public class MultiSets<K, V> implements Serializable {

    private Map<K, Set<V>> map;

    public MultiSets() {
        this.map = newLinkedHashMap();
    }

    public void putAll(K key, Collection<V> values) {
        for (V value : values) {
            put(key, value);
        }
    }

    public void put(K key, V value) {
        Set<V> values = map.get(key);
        if (values == null) {
            values = newLinkedHashSet();
            map.put(key, values);
        }
        values.add(value);
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Set<V> get(K key) {
        Set<V> values = map.get(key);
        return values == null ? new HashSet<V>() : values;
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public Set<V> remove(K key) {
        return map.remove(key);
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }
}
