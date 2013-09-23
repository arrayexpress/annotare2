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
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.util.Collections.unmodifiableCollection;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExperimentProfile implements Serializable {

    @JsonProperty("nextId")
    int nextId;

    @JsonProperty("type")
    private ExperimentProfileType type;

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

    @JsonProperty("protocolMap")
    private Map<Integer, Protocol> protocolMap;

    @JsonProperty("sampleMap")
    private Map<Integer, Sample> sampleMap;

    @JsonProperty("extractMap")
    private Map<Integer, Extract> extractMap;

    @JsonProperty("sampleAttributeMap")
    private Map<Integer, SampleAttribute> sampleAttributeMap;

    @JsonProperty("sampleAttributeOrder")
    private List<Integer> sampleAttributeOrder;

    @JsonProperty("labels")
    private Set<String> labels;

    @JsonProperty("sample2Extracts")
    private MultiSets<Integer, Integer> sampleId2ExtractsIds;
    private MultiSets<Sample, Extract> sample2Extracts;

    @JsonProperty("assayMap")
    private Map<String, Assay> assayMap;

    @JsonProperty("fileColumns")
    private List<FileColumn> fileColumns;

    ExperimentProfile() {
        /* used by GWT serialization */
    }

    public ExperimentProfile(@JsonProperty("type") ExperimentProfileType type) {
        this.type = type;
        contactMap = newLinkedHashMap();
        publicationMap = newLinkedHashMap();

        protocolMap = newLinkedHashMap();
        sampleMap = newLinkedHashMap();
        sampleAttributeMap = newLinkedHashMap();
        sampleAttributeOrder = newArrayList();

        extractMap = newLinkedHashMap();
        labels = newLinkedHashSet();

        sample2Extracts = new MultiSets<Sample, Extract>();

        assayMap = newLinkedHashMap();
        fileColumns = newArrayList();
    }

    @JsonProperty("sample2Extracts")
    MultiSets<Integer, Integer> getSampleId2ExtractsIds() {
        if (sampleId2ExtractsIds != null) {
            return sampleId2ExtractsIds;
        }
        MultiSets<Integer, Integer> map = new MultiSets<Integer, Integer>();
        for (Sample sample : sample2Extracts.keySet()) {
            Set<Extract> extracts = sample2Extracts.get(sample);
            for (Extract extract : extracts) {
                map.put(sample.getId(), extract.getId());
            }
        }
        return map;
    }

    @JsonProperty("sample2Extracts")
    void setSampleId2ExtractsIds(MultiSets<Integer, Integer> sampleId2ExtractsIds) {
        this.sampleId2ExtractsIds = sampleId2ExtractsIds;
    }

    public ExperimentProfileType getType() {
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

    public Protocol createProtocol(OntologyTerm term, ProtocolTargetType usageType) {
        Protocol protocol = new Protocol(nextId());
        protocol.setType(term);
        protocol.setUsage(usageType);
        protocolMap.put(protocol.getId(), protocol);
        return protocol;
    }

    public Sample createSample() {
        Sample sample = new Sample(nextId());
        sampleMap.put(sample.getId(), sample);
        return sample;
    }

    public Extract createExtract(boolean createAssay, Sample... samples) {
        if (samples.length == 0) {
            throw new IllegalArgumentException("Can't create empty extract");
        }
        Extract extract = new Extract(nextId());
        extractMap.put(extract.getId(), extract);
        for (Sample sample : samples) {
            link(sample, extract);
        }
        if (createAssay) {
            createAssay(extract, null);
        }
        return extract;
    }

    public LabeledExtract createLabeledExtract(Extract extract, String label) {
        Assay assay = createAssay(extract, label);
        return new LabeledExtract(assay.getId(), extract, label);
    }

    private Assay createAssay(Extract extract, String label) {
        if (label != null && label.length() > 0) {
            addLabel(label);
        }
        Assay assay = new Assay(extract, label);
        assayMap.put(assay.getId(), assay);
        return assay;
    }

    public FileColumn createFileColumn(FileType fileType) {
        FileColumn fileColumn = new FileColumn(fileType);
        fileColumns.add(fileColumn);
        return fileColumn;
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
            Set<Extract> extracts = sample2Extracts.get(s);
            if (extracts != null) {
                toBeRemoved.removeAll(extracts);
            }
        }
        for (Extract e : toBeRemoved) {
            removeExtract(e);
        }
        sampleMap.remove(sample.getId());
    }

    private void removeExtract(Extract extract) {
        for (Sample sample : sample2Extracts.keySet()) {
            Set<Extract> extracts = sample2Extracts.get(sample);
            extracts.remove(extract);
        }
        removeAssays(extract);
        extractMap.remove(extract.getId());
    }

    public void removeLabeledExtract(LabeledExtract labeledExtract) {
        removeAssay(new Assay(labeledExtract.getExtract(), labeledExtract.getLabel()));
    }

    private void removeAssays(Extract extract) {
        List<Assay> assays = new ArrayList<Assay>(assayMap.values());
        for (Assay assay : assays) {
            if (assay.getExtract().equals(extract)) {
                removeAssay(assay);
            }
        }
    }

    private void removeAssay(Assay assay) {
        removeFileMappings(assay);
        assayMap.remove(assay.getId());
    }

    private void removeFileMappings(Assay assay) {
        for(FileColumn fileColumn : fileColumns) {
            fileColumn.removeFileId(assay);
        }
    }

    public void removeFileColumn(int index) {
        fileColumns.remove(index);
    }

    public void removeFile(long fileId) {
        for(FileColumn fileColumn : fileColumns) {
            fileColumn.removeFileId(fileId);
        }
    }

    public void removeProtocol(int id) {
        protocolMap.remove(id);
    }

    public void link(Sample sample, Extract extract) {
        sample2Extracts.put(sample, extract);
    }

    public Contact getContact(int id) {
        return contactMap.get(id);
    }

    public Publication getPublication(int id) {
        return publicationMap.get(id);
    }

    public Protocol getProtocol(int id) {
        return protocolMap.get(id);
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
    public Collection<Protocol> getProtocols() {
        return unmodifiableCollection(protocolMap.values());
    }

    public Collection<Protocol> getProtocols(ProtocolTargetType usageType) {
        return usageType.filter(getProtocols());
    }

    @JsonIgnore
    public Collection<Sample> getSamples() {
        return unmodifiableCollection(sampleMap.values());
    }

    @JsonIgnore
    public Collection<Extract> getExtracts() {
        return unmodifiableCollection(extractMap.values());
    }

    @JsonIgnore
    public Collection<Extract> getExtracts(Sample sample) {
        return unmodifiableCollection(sample2Extracts.get(sample));
    }

    @JsonIgnore
    public Collection<LabeledExtract> getLabeledExtracts() {
        List<LabeledExtract> labeledExtracts = newArrayList();
        for (Assay assay : assayMap.values()) {
            labeledExtracts.add(assay.asLabeledExtract());
        }
        return labeledExtracts;
    }

    @JsonIgnore
    public Collection<LabeledExtract> getLabeledExtracts(Extract extract) {
        List<LabeledExtract> labeledExtracts = newArrayList();
        for (Assay assay : assayMap.values()) {
            if (assay.getExtract().equals(extract)) {
                labeledExtracts.add(assay.asLabeledExtract());
            }
        }
        return labeledExtracts;
    }

    @JsonIgnore
    public LabeledExtract getLabeledExtract(String id) {
        Assay assay = getAssay(id);
        return assay == null ? null : assay.asLabeledExtract();
    }

    @JsonIgnore
    public Collection<Assay> getAssays() {
        return unmodifiableCollection(assayMap.values());
    }

    @JsonIgnore
    public Assay getAssay(String assayId) {
        return assayMap.get(assayId);
    }

    @JsonIgnore
    public Collection<FileColumn> getFileColumns() {
        return unmodifiableCollection(fileColumns);
    }

    @JsonIgnore
    public FileColumn getFileColumn(int index) {
        return fileColumns.get(index);
    }

    public Collection<String> getLabels() {
        return unmodifiableCollection(labels);
    }

    public void addLabel(String label) {
        labels.add(label);
    }

    private int nextId() {
        return ++nextId;
    }

    protected void fixMe() {
        fixSample2Extracts();
        fixAssays();
    }

    private void fixSample2Extracts() {
        MultiSets<Integer, Integer> sample2ExtractsIds = getSampleId2ExtractsIds();
        for (Integer sampleId : sample2ExtractsIds.keySet()) {
            Sample sample = sampleMap.get(sampleId);
            for (Integer extractId : sample2ExtractsIds.get(sampleId)) {
                sample2Extracts.put(sample, extractMap.get(extractId));
            }
        }
        this.sampleId2ExtractsIds = null;
    }

    private void fixAssays() {
        for(Assay assay : assayMap.values()) {
            assay.fixMe(this);
        }

        for(FileColumn fileColumn : fileColumns) {
            fileColumn.fixMe(this);
        }
    }
}
