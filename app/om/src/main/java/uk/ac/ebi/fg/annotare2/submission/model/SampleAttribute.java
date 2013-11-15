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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @author Olga Melnichuk
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SampleAttribute implements Serializable {

    private int id;

    private String name;

    private OntologyTerm term;

    private AttributeType type;

    private AttributeValueSubType valueSubType;

    private OntologyTerm units;

    private OntologyTerm ontologyBranch;

    private boolean isEditable;

    SampleAttribute() {
    /* used by GWT serialization */
    }

    public SampleAttribute(int id) {
        this.id = id;
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

    public OntologyTerm getTerm() {
        return term;
    }

    public void setTerm(OntologyTerm term) {
        this.term = term;
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

    public AttributeValueType getValueType() {
        return valueSubType == null ? null : valueSubType.get(this);
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
