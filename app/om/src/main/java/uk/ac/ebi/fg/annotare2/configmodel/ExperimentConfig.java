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

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import uk.ac.ebi.fg.annotare2.configmodel.enums.ExperimentConfigType;
import uk.ac.ebi.fg.annotare2.submissionmodel.DataSerializationException;
import uk.ac.ebi.fg.annotare2.submissionmodel.LabeledExtract;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static java.util.Collections.unmodifiableCollection;

/**
 * @author Olga Melnichuk
 */
public class ExperimentConfig {

    @JsonProperty("nextId")
    int nextId;

    @JsonProperty("type")
    private final ExperimentConfigType type;

    @JsonProperty("accession")
    private String accession;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("experimentDate")
    private Date experimentDate;

    @JsonProperty("publicReleaseDate")
    private Date publicReleaseDate;

    @JsonProperty("contactMap")
    private Map<Integer, Contact> contacts;

    @JsonProperty("publicationMap")
    private Map<Integer, Publication> publications;

    @JsonProperty("sampleMap")
    private Map<Integer, SampleConfig> samples;

    @JsonProperty("labeledExtractMap")
    private Map<Integer, LabeledExtractConfig> labeledExtracts;

    private List<SampleAttribute> sampleAttributes;

    public ExperimentConfig(@JsonProperty("type") ExperimentConfigType type) {
        this.type = type;
        samples = newLinkedHashMap();
        labeledExtracts = newLinkedHashMap();

        contacts = newLinkedHashMap();
        publications = newLinkedHashMap();

        sampleAttributes = newArrayList();
    }

    public ExperimentConfigType getType() {
        return type;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getExperimentDate() {
        return experimentDate;
    }

    public void setExperimentDate(Date experimentDate) {
        this.experimentDate = experimentDate;
    }

    public Date getPublicReleaseDate() {
        return publicReleaseDate;
    }

    public void setPublicReleaseDate(Date publicReleaseDate) {
        this.publicReleaseDate = publicReleaseDate;
    }

    public Contact createContact() {
        Contact contact = new Contact(nextId());
        contacts.put(contact.getId(), contact);
        return contact;
    }

    public Contact removeContact(int id) {
        return contacts.remove(id);
    }

    public Publication createPublication() {
        Publication publication = new Publication(nextId());
        publications.put(publication.getId(), publication);
        return publication;
    }

    public Publication removePublication(int id) {
        return publications.remove(id);
    }

    public SampleConfig createSampleConfig() {
        SampleConfig sample = new SampleConfig(nextId());
        samples.put(sample.getId(), sample);
        return sample;
    }

    public void assignLabel(SampleConfig config, String label) {
        LabeledExtractConfig labeledExtract = new LabeledExtractConfig(nextId(), config, label);
        labeledExtracts.put(labeledExtract.getId(), labeledExtract);
    }

    public Contact getContact(int id) {
        return contacts.get(id);
    }

    public Publication getPublication(int id) {
        return publications.get(id);
    }

    public SampleConfig getSample(int id) {
        return samples.get(id);
    }

    public Collection<SampleAttribute> getSampleAttributes() {
        return unmodifiableCollection(sampleAttributes);
    }

    public void setSampleAttributes(List<SampleAttribute> sampleAttributes) {
        this.sampleAttributes = newArrayList(sampleAttributes);
    }

    @JsonIgnore
    public Collection<Contact> getContacts() {
        return unmodifiableCollection(contacts.values());
    }

    @JsonIgnore
    public Collection<Publication> getPublications() {
        return unmodifiableCollection(publications.values());
    }

    @JsonIgnore
    public Collection<SampleConfig> getSamples() {
        return unmodifiableCollection(samples.values());
    }

    @JsonIgnore
    public Collection<LabeledExtractConfig> getLabeledExtracts() {
        return unmodifiableCollection(labeledExtracts.values());
    }

    public static ExperimentConfig fromJsonString(String str) throws DataSerializationException {
        if (isNullOrEmpty(str)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            ExperimentConfig exp = mapper.readValue(str, ExperimentConfig.class);
            exp.fixMe();
            return exp;
        } catch (JsonGenerationException e) {
            throw new DataSerializationException(e);
        } catch (JsonMappingException e) {
            throw new DataSerializationException(e);
        } catch (IOException e) {
            throw new DataSerializationException(e);
        }
    }

    public String toJsonString() throws DataSerializationException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonGenerationException e) {
            throw new DataSerializationException(e);
        } catch (JsonMappingException e) {
            throw new DataSerializationException(e);
        } catch (IOException e) {
            throw new DataSerializationException(e);
        }
    }

    private int nextId() {
        return ++nextId;
    }

    private void fixMe() {
        for (LabeledExtractConfig labeledExtract : labeledExtracts.values()) {
            labeledExtract.fix(this);
        }
    }
}
