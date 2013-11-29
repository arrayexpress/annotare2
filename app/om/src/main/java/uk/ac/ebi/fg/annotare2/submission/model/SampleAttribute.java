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

import java.io.Serializable;

/**
 * @author Olga Melnichuk
 */
public class SampleAttribute implements Serializable {

    private int id;

    private String name;

    private SampleAttributeType type;

    private OntologyTerm term;

    private OntologyTerm units;

    private String template;

    SampleAttribute() {
    /* used by GWT serialization */
    }

    public SampleAttribute(int id, String template) {
        this.id = id;
        this.template = template;
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

    public SampleAttributeType getType() {
        return type;
    }

    public void setType(SampleAttributeType type) {
        this.type = type;
    }

    public OntologyTerm getUnits() {
        return units;
    }

    public void setUnits(OntologyTerm units) {
        this.units = units;
    }

    public String getTemplate() {
        return template;
    }

    public SampleAttribute copy() {
        SampleAttribute attr = new SampleAttribute(id, template);
        attr.name  = name;
        attr.type = type;
        attr.term = term;
        attr.units = units;
        return attr;
    }
}
