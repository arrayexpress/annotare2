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

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExperimentProfile implements Serializable {

    @JsonProperty("nextId")
    int nextId;

    @JsonProperty("type")
    private ExperimentConfigType type;

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
    private Map<Integer, Sample> samples;

    @JsonProperty("extractMap")
    private Map<Integer, Extract> extracts;

    @JsonProperty("sampleAttributeMap")
    private Map<Integer, SampleAttribute> sampleAttributeMap;

    @JsonProperty("sampleAttributeOrder")
    private List<Integer> sampleAttributeOrder;

    private Set<String> labels;

    private Set<LabeledExtract> labeledExtracts;

    ExperimentProfile() {
        /* used by GWT serialization */
    }

    public ExperimentProfile(@JsonProperty("type") ExperimentConfigType type) {
        this.type = type;
        contacts = newLinkedHashMap();
        publications = newLinkedHashMap();

        samples = newLinkedHashMap();
        sampleAttributeMap = newLinkedHashMap();
        sampleAttributeOrder = newArrayList();

        extracts = newHashMap();
        labels = newHashSet();
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

    public Sample createSample() {
        Sample sample = new Sample(nextId());
        samples.put(sample.getId(), sample);
        return sample;
    }

    public void removeSample(int id) {
        samples.remove(id);
    }

    public void removeLabeledExtract(int id) {
        labeledExtracts.remove(id);
    }

    public Contact getContact(int id) {
        return contacts.get(id);
    }

    public Publication getPublication(int id) {
        return publications.get(id);
    }

    public Sample getSample(int id) {
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
        for (Sample sample : getSamples()) {
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
    public Collection<Sample> getSamples() {
        return unmodifiableCollection(samples.values());
    }

    private int nextId() {
        return ++nextId;
    }

    public void fixMe() {
       /* for (LabeledExtract labeledExtract : labeledExtracts.values()) {
            labeledExtract.fix(this);
        }*/
    }

}
