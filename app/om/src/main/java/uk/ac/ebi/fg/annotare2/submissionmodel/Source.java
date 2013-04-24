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

/**
 * @author Olga Melnichuk
 */
public class Source implements GraphNode {
    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    private List<Integer> sampleIds;
    private List<Sample> samples;

    private List<Integer> extractIds;
    private List<Extract> extracts;

    @JsonCreator
    public Source(@JsonProperty("id") int id) {
        this.id = id;
        samples = newArrayList();
        extracts = newArrayList();
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

    @JsonIgnore
    public List<Sample> getSamples() {
        return samples;
    }

    public void addSample(Sample sample) {
        samples.add(sample);
    }

    public void addExtract(Extract extract) {
        extracts.add(extract);
    }

    @JsonProperty("samples")
    List<Integer> getSampleIds() {
        return sampleIds != null ? sampleIds :
                transform(samples, new Function<Sample, Integer>() {
                    @Nullable
                    @Override
                    public Integer apply(@Nullable Sample input) {
                        return input.getId();
                    }
                });
    }

    @JsonProperty("samples")
    void setSampleIds(List<Integer> sampleIds) {
        this.sampleIds = newArrayList(sampleIds);
    }

    @JsonProperty("extracts")
    List<Integer> getExtractIds() {
        return extractIds != null ? extractIds :
                transform(extracts, new Function<Extract, Integer>() {
                    @Nullable
                    @Override
                    public Integer apply(@Nullable Extract extract) {
                        return extract.getId();
                    }
                });
    }

    @JsonProperty("extracts")
    void setExtractIds(List<Integer> extractIds) {
        this.extractIds = newArrayList(extractIds);
    }

    void setAllSamples(List<Sample> samples) {
        this.samples = newArrayList(samples);
        sampleIds = null;
    }

    void setAllExtracts(List<Extract> extracts) {
        this.extracts = newArrayList(extracts);
        extractIds = null;
    }
}
