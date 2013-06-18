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

package uk.ac.ebi.fg.annotare2.configmodel;

import com.google.common.annotations.GwtCompatible;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class Extract implements Serializable {

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("attributes")
    private Map<ExtractAttribute, String> values;

    Extract() {
    /* used by GWT serialization */
    }

    public Extract(@JsonProperty("id") int id) {
        this.id = id;
        this.values = new HashMap<ExtractAttribute, String>();
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

    public Map<ExtractAttribute, String> getValues() {
        return new HashMap<ExtractAttribute, String>(values);
    }

    public void setValues(Map<ExtractAttribute, String> values) {
        this.values = new HashMap<ExtractAttribute, String>(values);
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
