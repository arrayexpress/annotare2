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
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
public class Experiment {

    private Map<String, String> properties;

    @JsonProperty("nextId")
    private int nextId;

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

    @JsonProperty("samples")
    private List<Sample> samples;

    @JsonProperty("extracts")
    private List<Extract> extracts;

    @JsonProperty("labeledExtracts")
    private List<LabeledExtract> labeledExtracts;

    @JsonProperty("assays")
    private List<Assay> assays;

    @JsonProperty("arrayDataFiles")
    private List<ArrayDataFile> arrayDataFiles;

    public Experiment(@JsonProperty("properties") Map<String, String> properties) {
        this.properties = new HashMap<String, String>();
        this.properties.putAll(properties);
    }

    public Sample addSample(Sample sample) {
        sample.setId(nextId());
        getSamples().add(sample);
        return sample;
    }

    public Extract addExtract(Extract extract) {
        extract.setId(nextId());
        getExtracts().add(extract);
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
            return mapper.readValue(str, Experiment.class);
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
            return mapper.writeValueAsString(this);
        } catch (JsonGenerationException e) {
            throw new DataSerializationExcepetion(e);
        } catch (JsonMappingException e) {
            throw new DataSerializationExcepetion(e);
        } catch (IOException e) {
            throw new DataSerializationExcepetion(e);
        }
    }

    private List<Sample> getSamples() {
        if (samples == null) {
            samples = newArrayList();
        }
        return samples;
    }

    private List<Extract> getExtracts() {
        if (extracts == null) {
            extracts = newArrayList();
        }
        return extracts;
    }

    private int nextId() {
        return nextId++;
    }
}
