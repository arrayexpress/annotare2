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
import static com.google.common.collect.Sets.newLinkedHashSet;
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
    private Map<Integer, Contact> contactMap;

    @JsonProperty("publicationMap")
    private Map<Integer, Publication> publicationMap;

    @JsonProperty("sampleMap")
    private Map<Integer, Sample> sampleMap;

    @JsonProperty("extractMap")
    private Map<Integer, Extract> extractMap;

    @JsonProperty("sampleAttributeMap")
    private Map<Integer, SampleAttribute> sampleAttributeMap;

    @JsonProperty("sampleAttributeOrder")
    private List<Integer> sampleAttributeOrder;

    private Set<String> labels;

    @JsonProperty("sample2Extracts")
    private Map<Integer, Set<Integer>> sample2ExtractsIds;
    private Map<Sample, Set<Extract>> sample2Extracts;

    @JsonProperty("extract2Labels")
    private Map<Integer, Set<String>> extractId2Labels;
    private Map<Extract, Set<String>> extract2Labels;

    ExperimentProfile() {
        /* used by GWT serialization */
    }

    public ExperimentProfile(@JsonProperty("type") ExperimentConfigType type) {
        this.type = type;
        contactMap = newLinkedHashMap();
        publicationMap = newLinkedHashMap();

        sampleMap = newLinkedHashMap();
        sampleAttributeMap = newLinkedHashMap();
        sampleAttributeOrder = newArrayList();

        extractMap = newLinkedHashMap();
        labels = newLinkedHashSet();

        sample2Extracts = newLinkedHashMap();
        extract2Labels = newLinkedHashMap();
    }

    @JsonProperty("sample2Extracts")
    Map<Integer, Set<Integer>> getSample2ExtractsIds() {
        if (sample2ExtractsIds != null) {
            return sample2ExtractsIds;
        }
        Map<Integer, Set<Integer>> map = newLinkedHashMap();
        for (Sample sample : sample2Extracts.keySet()) {
            Set<Extract> extracts = sample2Extracts.get(sample);
            Set<Integer> extractIds = newLinkedHashSet();
            for (Extract extract : extracts) {
                extractIds.add(extract.getId());
            }
            map.put(sample.getId(), extractIds);
        }
        return map;
    }

    @JsonProperty("sample2Extracts")
    void setSample2ExtractsIds(Map<Integer, Set<Integer>> sample2ExtractsIds) {
        this.sample2ExtractsIds = sample2ExtractsIds;
    }

    @JsonProperty("extract2Labels")
    Map<Integer, Set<String>> getExtractId2Labels() {
        if (extractId2Labels != null) {
            return extractId2Labels;
        }
        Map<Integer, Set<String>> map = newLinkedHashMap();
        for (Extract extract : extract2Labels.keySet()) {
            map.put(extract.getId(), new LinkedHashSet<String>(extract2Labels.get(extract)));
        }
        return map;
    }

    @JsonProperty("extract2Labels")
    void setExtractId2Labels(Map<Integer, Set<String>> extractId2Labels) {
        this.extractId2Labels = extractId2Labels;
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
        contactMap.put(contact.getId(), contact);
        return contact;
    }

    public Contact removeContact(int id) {
        return contactMap.remove(id);
    }

    public Publication createPublication() {
        Publication publication = new Publication(nextId());
        publicationMap.put(publication.getId(), publication);
        return publication;
    }

    public Publication removePublication(int id) {
        return publicationMap.remove(id);
    }

    public Sample createSample() {
        Sample sample = new Sample(nextId());
        sampleMap.put(sample.getId(), sample);
        return sample;
    }

    public Extract createExtract(Sample... samples) {
        if (samples.length == 0) {
            throw new IllegalArgumentException("Can't create empty extract");
        }
        Extract extract = new Extract(nextId());
        extractMap.put(extract.getId(), extract);
        for (Sample sample : samples) {
            link(extract, sample);
        }
        return extract;
    }

    public LabeledExtract createLabeledExtract(Extract extract, String label) {
        Set<String> labels = extract2Labels.get(extract);
        if (labels == null) {
            labels = new HashSet<String>();
            extract2Labels.put(extract, labels);
        }
        labels.add(label);
        return new LabeledExtract(extract, label);
    }

    public void removeSample(int id) {
        Sample sample = sampleMap.get(id);
        if (sample != null) {
            removeSample(sample);
        }
    }

    public void removeSample(Sample sample) {
        Set<Extract> toBeRemoved = sample2Extracts.remove(sample);

        for (Sample s : sample2Extracts.keySet()) {
            toBeRemoved.removeAll(sample2Extracts.get(s));
        }
        for (Extract e : toBeRemoved) {
            removeExtract(e);
        }
        sampleMap.remove(sample.getId());
    }

    private void removeExtract(Extract extract) {
        //TODO clear files
        for (Sample sample : sample2Extracts.keySet()) {
            Set<Extract> extracts = sample2Extracts.get(sample);
            extracts.remove(extract);
        }
        Set<String> labels = extract2Labels.remove(extract);
        if (labels != null) {
            for (String label : labels) {
                removeLabeledExtract(extract, label);
            }
        }
        extractMap.remove(extract.getId());
    }

    public void removeLabeledExtract(Extract extract, String label) {
        //TODO clear files
        Set<String> labels = extract2Labels.get(extract);
        if (labels != null) {
            labels.remove(label);
        }
    }

    public void link(Extract extract, Sample sample) {
        Set<Extract> extracts = sample2Extracts.get(sample);
        if (extracts == null) {
            extracts = new HashSet<Extract>();
            sample2Extracts.put(sample, extracts);
        }
        extracts.add(extract);
    }

    public Contact getContact(int id) {
        return contactMap.get(id);
    }

    public Publication getPublication(int id) {
        return publicationMap.get(id);
    }

    public Sample getSample(int id) {
        return sampleMap.get(id);
    }

    public Extract getExtract(int id) {
        return extractMap.get(id);
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
        return unmodifiableList(sampleAttributeOrder);
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
        return unmodifiableCollection(contactMap.values());
    }

    @JsonIgnore
    public Collection<Publication> getPublications() {
        return unmodifiableCollection(publicationMap.values());
    }

    @JsonIgnore
    public Collection<Sample> getSamples() {
        return unmodifiableCollection(sampleMap.values());
    }

    @JsonIgnore
    public Collection<Extract> getExtracts() {
        return unmodifiableCollection(extractMap.values());
    }

    private int nextId() {
        return ++nextId;
    }

    public void fixMe() {
        fixSample2Extracts();
        fixExtract2Labels();
    }

    private void fixSample2Extracts() {
        Map<Integer, Set<Integer>> sample2ExtractsIds = getSample2ExtractsIds();
        for (Integer sampleId : sample2ExtractsIds.keySet()) {
            Set<Extract> extracts = newLinkedHashSet();
            for (Integer extractId : sample2ExtractsIds.get(sampleId)) {
                extracts.add(extractMap.get(extractId));
            }
            Sample sample = sampleMap.get(sampleId);
            sample2Extracts.put(sample, extracts);
        }
        sample2ExtractsIds = null;
    }

    private void fixExtract2Labels() {
        Map<Integer, Set<String>> extractId2Labels = getExtractId2Labels();
        for (Integer extractId : extractId2Labels.keySet()) {
            extract2Labels.put(extractMap.get(extractId), new HashSet<String>(extractId2Labels.get(extractId)));
        }
        extractId2Labels = null;
    }
}
