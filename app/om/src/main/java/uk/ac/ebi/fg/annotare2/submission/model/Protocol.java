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

package uk.ac.ebi.fg.annotare2.submission.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Protocol implements Serializable {

    @JsonProperty("id")
    private int id;

    @JsonProperty("type")
    private OntologyTerm type;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("hardware")
    private String hardware;

    @JsonProperty("software")
    private String software;

    @JsonProperty("contact")
    private String contact;

    @JsonProperty("parameters")
    private List<String> parameters;

    @JsonProperty("targetType")
    private ProtocolTargetType targetType;

    @JsonProperty("assigned2All")
    private boolean assigned2All;

    Protocol() {
    /* used by GWT serialization only */
    }

    @JsonCreator
    public Protocol(@JsonProperty("id") int id) {
        this.id = id;
        this.parameters = new ArrayList<String>();
        assigned2All = true;
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

    public OntologyTerm getType() {
        return type;
    }

    public void setType(OntologyTerm type) {
        this.type = type;
    }

    public ProtocolTargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(ProtocolTargetType targetType) {
        this.targetType = targetType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHardware() {
        return hardware;
    }

    public void setHardware(String hardware) {
        this.hardware = hardware;
    }

    public String getSoftware() {
        return software;
    }

    public void setSoftware(String software) {
        this.software = software;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = new ArrayList<String>(parameters);
    }

    public List<String> getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Protocol protocol = (Protocol) o;

        if (id != protocol.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public void setAssigned2All(boolean assigned2All) {
        this.assigned2All = assigned2All;
    }

    public boolean isAssigned2All() {
        return assigned2All;
    }
}
