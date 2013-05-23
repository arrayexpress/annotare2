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

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.Nonnull;

/**
 * @author Olga Melnichuk
 */
public class LabeledExtractConfig {

    @JsonProperty("id")
    private int id;

    @JsonProperty("label")
    private String label;

    private SampleConfig sample;
    private int sampleId;

    @JsonCreator
    public LabeledExtractConfig(@JsonProperty("id") int id, @JsonProperty("sample") int sampleId, @JsonProperty("label") String label) {
        this.id = id;
        this.sampleId = sampleId;
        this.label = label;
    }

    public LabeledExtractConfig(int id, @Nonnull SampleConfig sample, String label) {
        this(id, sample.getId(), label);
        this.sample = sample;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @JsonIgnore
    public SampleConfig getSample() {
        return sample;
    }

    @JsonProperty("sample")
    Integer getSampleId() {
        return sampleId;
    }

    void fix(ExperimentConfig config) {
        this.sample = config.getSample(sampleId);
    }

    @JsonIgnore
    public String getName() {
        return sample.getName() + "@" + label;
    }
}
