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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    @JsonProperty("protocols")
    private Set<Integer> protocolIds;

    Extract() {
    /* used by GWT serialization */
    }

    public Extract(@JsonProperty("id") int id) {
        this.id = id;
        this.values = new HashMap<ExtractAttribute, String>();
        this.protocolIds = new HashSet<Integer>();
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
        return values.get(attribute);
    }

    public Map<ExtractAttribute, String> getAttributeValues() {
        return new HashMap<ExtractAttribute, String>(values);
    }

    public void setAttributeValues(Map<ExtractAttribute, String> values) {
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

    public boolean hasProtocol(Protocol protocol) {
        return protocolIds.contains(protocol.getId());
    }

    public void assign(Protocol protocol, boolean assigned) {
        if (!assigned && hasProtocol(protocol)) {
            protocolIds.remove(protocol.getId());
        } else {
            protocolIds.add(protocol.getId());
        }
    }
}
