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

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.unmodifiableList;

/**
 * @author Olga Melnichuk
 */
public class Experiment {

    @JsonProperty("properties")
    private Map<String, String> properties;

    @JsonProperty("nextId")
    int nextId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("experimentDate")
    private Date experimentDate;

    @JsonProperty("publicReleaseDate")
    private Date publicReleaseDate;

    @JsonProperty("contacts")
    private List<Contact> contacts;

    @JsonProperty("publications")
    private List<Publication> publications;

    private List<Sample> samples;
    private List<Extract> extracts;
    private List<LabeledExtract> labeledExtracts;
    private List<Assay> assays;
    private List<ArrayDataFile> arrayDataFiles;

    @JsonCreator
    public Experiment(@JsonProperty("properties") Map<String, String> properties) {
        this.properties = newHashMap(properties);
        samples = newArrayList();
        extracts = newArrayList();
        labeledExtracts = newArrayList();
        assays = newArrayList();
        arrayDataFiles = newArrayList();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getExperimentDate() {
        return experimentDate;
    }

    public Date getPublicReleaseDate() {
        return publicReleaseDate;
    }

    public Sample createSample(Sample sample) {
        sample.setId(nextId());
        samples.add(sample);
        return sample;
    }

    public Extract createExtract(Extract extract) {
        extract.setId(nextId());
        extracts.add(extract);
        return extract;
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }

    public static Experiment fromJsonString(String str) throws DataSerializationExcepetion {
        if (isNullOrEmpty(str)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            ExperimentData data = mapper.readValue(str, ExperimentData.class);
            return data.fixExperiment();
        } catch (JsonGenerationException e) {
            throw new DataSerializationExcepetion(e);
        } catch (JsonMappingException e) {
            throw new DataSerializationExcepetion(e);
        } catch (IOException e) {
            throw new DataSerializationExcepetion(e);
        }
    }

    public String toJsonString() throws DataSerializationExcepetion {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(new ExperimentData(this));
        } catch (JsonGenerationException e) {
            throw new DataSerializationExcepetion(e);
        } catch (JsonMappingException e) {
            throw new DataSerializationExcepetion(e);
        } catch (IOException e) {
            throw new DataSerializationExcepetion(e);
        }
    }

    private int nextId() {
        return nextId++;
    }

    @JsonIgnore
    public List<Sample> getSamples() {
        return unmodifiableList(samples);
    }

    @JsonIgnore
    public List<Extract> getExtracts() {
        return unmodifiableList(extracts);
    }

    @JsonIgnore
    public List<LabeledExtract> getLabeledExtracts() {
        return unmodifiableList(labeledExtracts);
    }

    @JsonIgnore
    public List<Assay> getAssays() {
        return unmodifiableList(assays);
    }

    @JsonIgnore
    public List<ArrayDataFile> getArrayDataFiles() {
        return unmodifiableList(arrayDataFiles);
    }

    void restoreSamples(List<Sample> samples) {
        this.samples = newArrayList(samples);
    }

    void restoreExtracts(List<Extract> extracts) {
        this.extracts = newArrayList(extracts);
    }

    void restoreLabeledExtracts(List<LabeledExtract> labeledExtracts) {
        this.labeledExtracts = newArrayList(labeledExtracts);
    }

    void restoreAssays(List<Assay> assays) {
        this.assays = newArrayList(assays);
    }

    void restoreArrayDataFiles(List<ArrayDataFile> files) {
        this.arrayDataFiles = newArrayList(files);
    }

}
