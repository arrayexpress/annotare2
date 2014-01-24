/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.submission.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class Extract implements Serializable {

    private int id;

    private String name;

    private Map<ExtractAttribute, String> attributeValues;

    Extract() {
        /* used by GWT serialization */
        this(0);
    }

    @JsonCreator
    public Extract(int id) {
        this.id = id;
        this.attributeValues = new HashMap<ExtractAttribute, String>();
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

    public String getAttributeValue(ExtractAttribute attribute) {
        return attributeValues.get(attribute);
    }

    @JsonIgnore
    public Map<ExtractAttribute, String> getAttributeValues() {
        return new HashMap<ExtractAttribute, String>(attributeValues);
    }

    @JsonIgnore
    public void setAttributeValues(Map<ExtractAttribute, String> values) {
        this.attributeValues = new HashMap<ExtractAttribute, String>(values);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Extract extract = (Extract) o;

        if (id != extract.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
