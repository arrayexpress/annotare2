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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import uk.ac.ebi.fg.annotare2.configmodel.enums.AttributeType;

/**
 * @author Olga Melnichuk
 */
public class SampleAttribute {

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private AttributeType type;

    @JsonProperty("valueSubType")
    private AttributeValueSubType valueSubType;

    @JsonProperty("units")
    private OntologyTerm units;

    @JsonProperty("ontologyBranch")
    private OntologyTerm ontologyBranch;

    @JsonProperty("editable")
    private boolean isEditable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AttributeType getType() {
        return type;
    }

    public void setType(AttributeType type) {
        this.type = type;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    @JsonIgnore
    public String getSdrfColumnName() {
        return type.getName(name);
    }

    @JsonIgnore
    public AttributeValueType getValueType() {
        return valueSubType == null ? null : valueSubType.get(this);
    }

    @JsonIgnore
    public void setValueType(AttributeValueType valueType) {
        if (valueType == null) {
            valueSubType = null;
            return;
        }
        valueType.set(this);
    }

    void setValueSubType(AttributeValueSubType valueSubType) {
        this.valueSubType = valueSubType;
    }

    OntologyTerm getOntologyBranch() {
        return ontologyBranch;
    }

    void setOntologyBranch(OntologyTerm ontologyBranch) {
        this.ontologyBranch = ontologyBranch;
    }

    OntologyTerm getUnits() {
        return units;
    }

    void setUnits(OntologyTerm units) {
        this.units = units;
    }
}
