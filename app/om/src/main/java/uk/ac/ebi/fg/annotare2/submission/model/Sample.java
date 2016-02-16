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
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.unmodifiableMap;

/**
 * @author Olga Melnichuk
 */
public class Sample implements Serializable {

    private int id;

    private String name;

    private Map<Integer, String> values;

    Sample() {
    /* used by GWT serialization */
        this(0);
    }

    public Sample(int id) {
        this.id = id;
        values = newHashMap();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Integer, String> getValues() {
        return unmodifiableMap(values);
    }

    public void setValues(Map<Integer, String> values) {
        this.values = newHashMap(values);
    }

    public String getValue(SampleAttribute attribute) {
        String value = values.get(attribute.getId());
        return value == null ? "" : value;
    }

    void removeAttributeValue(int id) {
        values.remove(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sample sample = (Sample) o;

        if (id != sample.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
