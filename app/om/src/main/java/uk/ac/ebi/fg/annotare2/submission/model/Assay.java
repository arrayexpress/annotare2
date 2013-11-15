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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @author Olga Melnichuk
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Assay implements Serializable, HasProtocolAssignment {

    @JsonProperty("extract")
    private Integer extractId;
    private Extract extract;

    @JsonProperty("label")
    private String label;

    @JsonProperty("assayProtocols")
    private ProtocolAssignment assayProtocols;

    @JsonProperty("labeledExtractProtocols")
    private ProtocolAssignment labeledExtractProtocols;

    Assay() {
        /* used by GWT serialization only */
        this(0, null, "");
    }

    @JsonCreator
    Assay(@JsonProperty("extract") int extractId, @JsonProperty("label") String label) {
        this(extractId, null, label);
    }

    public Assay(Extract extract) {
        this(extract.getId(), extract, null);
    }

    public Assay(Extract extract, String label) {
        this(extract.getId(), extract, label);
    }

    private Assay(int extractId, Extract extract, String label) {
        this.extractId = extractId;
        this.extract = extract;
        this.label = label;
        assayProtocols = new ProtocolAssignment();
        labeledExtractProtocols = new ProtocolAssignment();
    }

    @JsonIgnore
    public Extract getExtract() {
        return extract;
    }

    public String getLabel() {
        return label;
    }

    void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return extractId + (label != null && label.length() > 0 ? "_" + label : "");
    }

    @JsonIgnore
    public String getName() {
        return extract.getName() + (label != null && label.length() > 0 ? ":" + label : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Assay assay = (Assay) o;

        if (!extractId.equals(assay.extractId)) return false;
        if (label != null ? !label.equals(assay.label) : assay.label != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = extractId.hashCode();
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }

    void fixMe(ExperimentProfile exp) {
        extract = exp.getExtract(extractId);
        if (extract == null) {
            throw new IllegalStateException("Assay can't exist without extract; (cause: extract with id=" +
                    extractId + " was not found in experiment profile)");
        }
    }

    @JsonIgnore
    public LabeledExtract asLabeledExtract() {
        return new LabeledExtract(this);
    }

    @Override
    public boolean hasProtocol(Protocol protocol) {
        return assayProtocols.contains(protocol);
    }

    @Override
    public void assignProtocol(Protocol protocol, boolean assigned) {
        assayProtocols.set(protocol, assigned);
    }

    @Override
    public AssignmentItem getProtocolAssignmentItem() {
        return new AssignmentItem(getId(), getName());
    }

    boolean hasLabeledExtractProtocol(Protocol protocol) {
        return labeledExtractProtocols.contains(protocol);
    }

    void assignLabelExtractProtocol(Protocol protocol, boolean assigned) {
        labeledExtractProtocols.set(protocol, assigned);
    }
}
