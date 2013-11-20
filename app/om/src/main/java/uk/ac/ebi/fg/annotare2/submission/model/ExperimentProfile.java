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

package uk.ac.ebi.fg.annotare2.submission.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.util.Collections.unmodifiableCollection;

/**
 * @author Olga Melnichuk
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExperimentProfile implements Serializable {

    int nextId;

    private ExperimentProfileType type;

    private String accession;

    private String title;

    private String description;

    private Date experimentDate;

    private Date publicReleaseDate;

    private String arrayDesign;

    private String aeExperimentType;

    private Set<String> labels;

    private List<OntologyTerm> experimentalDesigns;

    private Map<Integer, Contact> contactMap;

    private Map<Integer, Publication> publicationMap;

    private Map<Integer, Protocol> protocolMap;
    private List<Integer> protocolOrder;

    private Map<Integer, SampleAttribute> sampleAttributeMap;
    private List<Integer> sampleAttributeOrder;

    private Map<Integer, Sample> sampleMap;

    private Map<Integer, Extract> extractMap;

    private Map<String, Assay> assayMap;

    private List<FileColumn> fileColumns;

    private Map<Integer, Set<Integer>> sampleId2ExtractsIds;
    private MultiSets<Sample, Extract> sample2Extracts;

    ExperimentProfile() {
        /* used by GWT serialization */
        this(null);
    }

    public ExperimentProfile(ExperimentProfileType type) {
        this.type = type;
        experimentalDesigns = newArrayList();
        contactMap = newLinkedHashMap();
        publicationMap = newLinkedHashMap();

        protocolMap = newHashMap();
        protocolOrder = newArrayList();
        sampleMap = newLinkedHashMap();
        sampleAttributeMap = newLinkedHashMap();
        sampleAttributeOrder = newArrayList();

        extractMap = newLinkedHashMap();
        labels = newLinkedHashSet();

        sample2Extracts = new MultiSets<Sample, Extract>();

        assayMap = newLinkedHashMap();
        fileColumns = newArrayList();
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

    public String getArrayDesign() {
        return arrayDesign;
    }

    public void setArrayDesign(String arrayDesign) {
        this.arrayDesign = arrayDesign;
    }

    public String getAeExperimentType() {
        return aeExperimentType;
    }

    public void setAeExperimentType(String aeExperimentType) {
        this.aeExperimentType = aeExperimentType;
    }

    public Collection<OntologyTerm> getExperimentalDesigns() {
        return unmodifiableCollection(experimentalDesigns);
    }

    public void setExperimentalDesigns(Collection<OntologyTerm> experimentalDesigns) {
        this.experimentalDesigns = new ArrayList<OntologyTerm>(experimentalDesigns);
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
        protocol.setTargetType(usageType);
        protocolMap.put(protocol.getId(), protocol);
        protocolOrder.add(protocol.getId());
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
        return new LabeledExtract(createAssay(extract, label));
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
        for (FileColumn fileColumn : fileColumns) {
            fileColumn.removeFileName(assay);
        }
    }

    public void removeFileColumn(int index) {
        fileColumns.remove(index);
    }

    public void removeFile(String fileName) {
        for (FileColumn fileColumn : fileColumns) {
            fileColumn.removeFileName(fileName);
        }
    }

    public void removeProtocol(Protocol protocol) {
        if (protocol == null) {
            return;
        }
        protocol.getTargetType().removeProtocolAssignments(protocol, this);
        protocolMap.remove(protocol.getId());
        protocolOrder.remove(Integer.valueOf(protocol.getId()));
    }

    public void moveProtocolUp(Protocol protocol) {
        if (protocol == null) {
            return;
        }
        int from = protocolOrder.indexOf(protocol.getId());
        moveProtocol(from, from - 1);
    }

    public void moveProtocolDown(Protocol protocol) {
        if (protocol == null) {
            return;
        }
        int from = protocolOrder.indexOf(protocol.getId());
        moveProtocol(from, from + 1);
    }

    private void moveProtocol(int from, int to) {
        if (from < 0 || to < 0 || to >= protocolOrder.size()) {
            return;
        }
        Integer swap = protocolOrder.get(from);
        protocolOrder.set(from, protocolOrder.get(to));
        protocolOrder.set(to, swap);
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
        List<Protocol> list = newArrayList();
        for (Integer id : protocolOrder) {
            list.add(protocolMap.get(id));
        }
        return list;
    }

    public Collection<Protocol> getProtocols(ProtocolTargetType usageType) {
        return usageType.filter(getProtocols());
    }

    @JsonIgnore
    public Collection<Sample> getSamples() {
        return unmodifiableCollection(sampleMap.values());
    }

    public Collection<Sample> getSamples(Extract extract) {
        List<Sample> samples = new ArrayList<Sample>();
        for (Sample sample : sample2Extracts.keySet()) {
            if (sample2Extracts.get(sample).contains(extract)) {
                samples.add(sample);
            }
        }
        return samples;
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

    @JsonIgnore
    Collection<FileRef> getRawFileRefs() {
        Set<FileRef> fileRefs = new HashSet<FileRef>();
        for (FileColumn fileColumn : fileColumns) {
            if (fileColumn.getType().isRaw()) {
                fileRefs.addAll(fileColumn.getFileRefs());
            }
        }
        return fileRefs;
    }

    @JsonIgnore
    Collection<FileRef> getProcessedFileRefs() {
        Set<FileRef> fileRefs = new HashSet<FileRef>();
        for (FileColumn fileColumn : fileColumns) {
            if (fileColumn.getType().isProcessed()) {
                fileRefs.addAll(fileColumn.getFileRefs());
            }
        }
        return fileRefs;
    }

    public Collection<String> getLabels() {
        return unmodifiableCollection(labels);
    }

    public void addLabel(String label) {
        if (label == null) {
            return;
        }
        labels.add(label);
    }

    public void removeLabel(String label) {
        if (label == null) {
            return;
        }
        List<Assay> assays = new ArrayList<Assay>(getAssays());
        for (Assay assay : assays) {
            if (label.equals(assay.getLabel())) {
                removeAssay(assay);
            }
        }
        labels.remove(label);
    }

    public void addOrReLabel(String oldLabel, String newLabel) {
        if (oldLabel == null) {
            addLabel(newLabel);
            return;
        }
        if (oldLabel.equals(newLabel)) {
            return;
        }
        addLabel(newLabel);
        for (Assay assay : getAssays()) {
            if (oldLabel.equals(assay.getLabel())) {
                reLabelAssay(assay, newLabel);
            }
        }
        removeLabel(oldLabel);
    }

    private void reLabelAssay(Assay assay, String newLabel) {
        assayMap.remove(assay.getId());
        assay.setLabel(newLabel);
        assayMap.put(assay.getId(), assay);
    }

    public Map<AssignmentItem, Boolean> getProtocolAssignments(Protocol protocol) {
        return protocol.getTargetType().getProtocolAssignments(protocol, this);
    }

    private int nextId() {
        return ++nextId;
    }

    public void fixMe() {
        fixSample2Extracts();
        fixAssays();
    }

    private void fixSample2Extracts() {
        if (sampleId2ExtractsIds == null) {
            return;
        }
        for (Integer sampleId : sampleId2ExtractsIds.keySet()) {
            Sample sample = sampleMap.get(sampleId);
            for (Integer extractId : sampleId2ExtractsIds.get(sampleId)) {
                sample2Extracts.put(sample, extractMap.get(extractId));
            }
        }
        this.sampleId2ExtractsIds = null;
    }

    private void fixAssays() {
        for (Assay assay : assayMap.values()) {
            assay.fixMe(this);
        }

        for (FileColumn fileColumn : fileColumns) {
            fileColumn.fixMe(this);
        }
    }
}
