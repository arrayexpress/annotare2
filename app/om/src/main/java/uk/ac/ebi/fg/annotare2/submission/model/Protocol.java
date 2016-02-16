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
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class Protocol implements Serializable {

    private int id;

    private OntologyTerm type;

    private String name;

    private String description;

    private String hardware;

    private String software;

    private String performer;

    private List<String> parameters;

    private ProtocolSubjectType subjectType;

    private boolean isAssigned;

    Protocol() {
        /* used by GWT serialization only */
        this(0);
    }

    public Protocol(int id) {
        this.id = id;
        this.parameters = new ArrayList<String>();
        this.isAssigned = true;
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

    public ProtocolSubjectType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(ProtocolSubjectType subjectType) {
        this.subjectType = subjectType;
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

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = new ArrayList<String>(parameters);
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setAssigned(boolean isAssigned) {
        this.isAssigned = isAssigned;
    }

    public boolean isAssigned() {
        return isAssigned;
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

    public boolean hasPerformer() {
        return !isNullOrEmpty(this.performer);
    }
}
