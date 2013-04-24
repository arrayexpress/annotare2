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
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;

/**
 * @author Olga Melnichuk
 */
public class LabeledExtract implements GraphNode {

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("label")
    private String label;

    private List<Integer> assayIds;

    private List<Assay> assays;

    public LabeledExtract() {
        assays = newArrayList();
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @JsonIgnore
    public List<Assay> getAssays() {
        return Collections.unmodifiableList(assays);
    }

    @JsonProperty("assays")
    List<Integer> getAssayIds() {
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
    void setAssayIds(List<Integer> assayIds) {
        this.assayIds = assayIds;
    }

    public void addAssay(Assay assay) {
        assays.add(assay);
    }
}
