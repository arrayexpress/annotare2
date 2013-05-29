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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import uk.ac.ebi.fg.annotare2.configmodel.enums.ExperimentConfigType;
import uk.ac.ebi.fg.annotare2.submissionmodel.DataSerializationException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;

/**
 * @author Olga Melnichuk
 */
public class ExperimentProfile {

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
    private Map<Integer, SampleProfile> samples;

    @JsonProperty("labeledExtractMap")
    private Map<Integer, LabeledExtractProfile> labeledExtracts;

    @JsonProperty("sampleAttributeMap")
    private Map<Integer, SampleAttribute> sampleAttributeMap;

    @JsonProperty("sampleAttributeOrder")
    private List<Integer> sampleAttributeOrder;

    public ExperimentProfile(@JsonProperty("type") ExperimentConfigType type) {
        this.type = type;
        samples = newLinkedHashMap();
        labeledExtracts = newLinkedHashMap();

        contacts = newLinkedHashMap();
        publications = newLinkedHashMap();

        sampleAttributeMap = newLinkedHashMap();
        sampleAttributeOrder = newArrayList();
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

    public SampleProfile createSample() {
        SampleProfile sample = new SampleProfile(nextId());
        samples.put(sample.getId(), sample);
        return sample;
    }

    public void removeSample(int id) {
        for (LabeledExtractProfile labeledExtract : labeledExtracts.values()) {
            if (labeledExtract.getSample().getId() == id) {
                removeLabeledExtract(labeledExtract.getId());
            }
        }
        samples.remove(id);
    }

    public void removeLabeledExtract(int id) {
        labeledExtracts.remove(id);
    }

    public void assignLabel(SampleProfile config, String label) {
        LabeledExtractProfile labeledExtract = new LabeledExtractProfile(nextId(), config, label);
        labeledExtracts.put(labeledExtract.getId(), labeledExtract);
    }

    public Contact getContact(int id) {
        return contacts.get(id);
    }

    public Publication getPublication(int id) {
        return publications.get(id);
    }

    public SampleProfile getSample(int id) {
        return samples.get(id);
    }

    public SampleAttribute getSampleAttribute(int id) {
        return sampleAttributeMap.get(id);
    }

    public SampleAttribute createSampleAttribute() {
        SampleAttribute attr = new SampleAttribute(nextId());
        sampleAttributeMap.put(attr.getId(), attr);
        sampleAttributeOrder.add(attr.getId());
        return attr;
    }

    public void removeSampleAttribute(int id) {
        for (SampleProfile sample : getSamples()) {
            sample.removeAttributeValue(id);
        }
        sampleAttributeMap.remove(id);
        sampleAttributeOrder.remove(Integer.valueOf(id));
    }

    public void setSampleAttributeOrder(Collection<Integer> order) {
        sampleAttributeOrder = newArrayList(order);
    }

    public Collection<Integer> getSampleAttributeOrder() {
        return  unmodifiableList(sampleAttributeOrder);
    }

    @JsonIgnore
    public Collection<SampleAttribute> getSampleAttributes() {
        return Lists.transform(sampleAttributeOrder, new Function<Integer, SampleAttribute>() {
            @Nullable
            @Override
            public SampleAttribute apply(@Nullable Integer input) {
                return sampleAttributeMap.get(input);
            }
        });
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
    public Collection<SampleProfile> getSamples() {
        return unmodifiableCollection(samples.values());
    }

    @JsonIgnore
    public Collection<LabeledExtractProfile> getLabeledExtracts() {
        return unmodifiableCollection(labeledExtracts.values());
    }

    public static ExperimentProfile fromJsonString(String str) throws DataSerializationException {
        if (isNullOrEmpty(str)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            ExperimentProfile exp = mapper.readValue(str, ExperimentProfile.class);
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
        for (LabeledExtractProfile labeledExtract : labeledExtracts.values()) {
            labeledExtract.fix(this);
        }
    }

}
