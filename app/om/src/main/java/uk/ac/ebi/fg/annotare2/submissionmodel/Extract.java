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

package uk.ac.ebi.fg.annotare2.submissionmodel;

import com.google.common.base.Function;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static java.util.Collections.unmodifiableList;

/**
 * @author Olga Melnichuk
 */
public class Extract implements GraphNode {

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    private List<Integer> labeledExtractIds;
    private List<LabeledExtract> labeledExtracts;

    private List<Integer> assayIds;
    private List<Assay> assays;

    @JsonCreator
    public Extract(@JsonProperty("id") int id) {
        this.id = id;
        labeledExtracts = newArrayList();
        assays = newArrayList();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addAssay(Assay assay) {
        this.assays.add(assay);
    }

    public void addLabeledExtract(LabeledExtract labeledExtract) {
        this.labeledExtracts.add(labeledExtract);
    }

    @JsonIgnore
    public List<Assay> getAssays() {
        return unmodifiableList(assays);
    }

    @JsonIgnore
    public List<LabeledExtract> getLabeledExtracts() {
        return unmodifiableList(labeledExtracts);
    }

    @JsonProperty("assays")
    public List<Integer> getAssayIds() {
        return assayIds != null ? assayIds :
                transform(assays, new Function<Assay, Integer>() {
                    @Nullable
                    @Override
                    public Integer apply(@Nullable Assay assay) {
                        return assay.getId();
                    }
                });
    }

    @JsonProperty("assays")
    public void setAssayIds(List<Integer> assayIds) {
        this.assayIds = assayIds;
    }

    @JsonProperty("labeledExtracts")
    List<Integer> getLabeledExtractIds() {
        return labeledExtractIds != null ? labeledExtractIds :
                transform(labeledExtracts, new Function<LabeledExtract, Integer>() {
                    @Nullable
                    @Override
                    public Integer apply(@Nullable LabeledExtract labeledExtract) {
                        return labeledExtract.getId();
                    }
                });
    }

    @JsonProperty("labeledExtracts")
    void setLabeledExtractIds(List<Integer> labeledExtractIds) {
        this.labeledExtractIds = labeledExtractIds;
    }

    void setAllLabeledExtracts(List<LabeledExtract> extracts) {
        this.labeledExtracts = newArrayList(extracts);
        this.labeledExtractIds = null;
    }

    void setAllAssays(List<Assay> assays) {
        this.assays = newArrayList(assays);
        this.assayIds = null;
    }
}
