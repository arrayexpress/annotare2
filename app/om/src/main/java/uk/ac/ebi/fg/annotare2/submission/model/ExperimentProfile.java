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

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableSet;

/**
 * @author Olga Melnichuk
 */
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

    private List<OntologyTerm> experimentalDesigns;

    private Map<Integer, Label> labelMap;

    private Map<Integer, Contact> contactMap;

    private Map<Integer, Publication> publicationMap;

    private Map<Integer, Protocol> protocolMap;
    private List<Integer> protocolOrder;

    private Map<Integer, SampleAttribute> sampleAttributeMap;
    private List<Integer> sampleAttributeOrder;

    private Map<Integer, Sample> sampleMap;

    private Map<Integer, Extract> extractMap;

    private Map<String, LabeledExtract> labeledExtractMap;

    private List<FileColumn> fileColumns;

    private Map<Integer, Set<Integer>> sampleId2ExtractsIds;
    private MultiSets<Sample, Extract> sample2Extracts;

    private Map<Integer, Set<Integer>> protocolId2SampleIds;
    private MultiSets<Protocol, Sample> protocol2Samples;

    private Map<Integer, Set<Integer>> protocolId2ExtractIds;
    private MultiSets<Protocol, Extract> protocol2Extracts;

    private Map<Integer, Set<String>> protocolId2LabeledExtractIds;
    private MultiSets<Protocol, LabeledExtract> protocol2LabeledExtracts;

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
        labelMap = newLinkedHashMap();

        sample2Extracts = new MultiSets<Sample, Extract>();

        labeledExtractMap = newLinkedHashMap();
        fileColumns = newArrayList();

        protocol2Samples = new MultiSets<Protocol, Sample>();
        protocol2Extracts = new MultiSets<Protocol, Extract>();
        protocol2LabeledExtracts = new MultiSets<Protocol, LabeledExtract>();
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

    public Protocol createProtocol(OntologyTerm term, ProtocolSubjectType usageType) {
        Protocol protocol = new Protocol(nextId());
        protocol.setType(term);
        protocol.setSubjectType(usageType);
        protocolMap.put(protocol.getId(), protocol);
        protocolOrder.add(protocol.getId());
        return protocol;
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
            link(sample, extract);
        }
        return extract;
    }

    public LabeledExtract createLabeledExtract(Extract extract, String labelName) {
        Label label = addLabel(labelName);
        LabeledExtract labeledExtract = new LabeledExtract(extract, label);
        labeledExtractMap.put(labeledExtract.getId(), labeledExtract);
        return labeledExtract;
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
        removeLabeledExtracts(extract);
        extractMap.remove(extract.getId());
    }

    public void removeLabeledExtract(LabeledExtract labeledExtract) {
        removeFileMappings(labeledExtract);
        labeledExtractMap.remove(labeledExtract.getId());
    }

    private void removeLabeledExtracts(Extract extract) {
        List<LabeledExtract> labeledExtracts = new ArrayList<LabeledExtract>(labeledExtractMap.values());
        for (LabeledExtract labeledExtract : labeledExtracts) {
            if (labeledExtract.getExtract().equals(extract)) {
                removeLabeledExtract(labeledExtract);
            }
        }
    }

    private void removeFileMappings(LabeledExtract labeledExtract) {
        for (FileColumn fileColumn : fileColumns) {
            fileColumn.removeFileName(labeledExtract);
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
        removeProtocolAssignments(protocol);
        protocolMap.remove(protocol.getId());
    }

    private void removeProtocolAssignments(Protocol protocol) {
        protocol2Samples.remove(protocol);
        protocol2Extracts.remove(protocol);
        protocol2LabeledExtracts.remove(protocol);
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

    public Contact getContact(Integer id) {
        return contactMap.get(id);
    }

    public Publication getPublication(Integer id) {
        return publicationMap.get(id);
    }

    public Protocol getProtocol(Integer id) {
        return protocolMap.get(id);
    }

    public Sample getSample(Integer id) {
        return sampleMap.get(id);
    }

    public Extract getExtract(Integer id) {
        return extractMap.get(id);
    }

    public SampleAttribute getSampleAttribute(int id) {
        return sampleAttributeMap.get(id);
    }

    public SampleAttribute createSampleAttribute(String template) {
        SampleAttribute attr = new SampleAttribute(nextId(), template);
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

    public Collection<SampleAttribute> getSampleAttributes() {
        return Lists.transform(sampleAttributeOrder, new Function<Integer, SampleAttribute>() {
            @Nullable
            @Override
            public SampleAttribute apply(@Nullable Integer input) {
                return sampleAttributeMap.get(input);
            }
        });
    }

    public Collection<Contact> getContacts() {
        return unmodifiableCollection(contactMap.values());
    }


    public Collection<Publication> getPublications() {
        return unmodifiableCollection(publicationMap.values());
    }

    public Collection<Protocol> getProtocols() {
        return unmodifiableCollection(protocolMap.values());
    }

    public Collection<Protocol> getProtocols(ProtocolSubjectType type) {
        return type.filter(getProtocols());
    }

    public Collection<Sample> getSamples() {
        return unmodifiableCollection(sampleMap.values());
    }

    public Collection<Extract> getExtracts() {
        return unmodifiableCollection(extractMap.values());
    }

    public Collection<Extract> getExtracts(Sample sample) {
        return unmodifiableCollection(sample2Extracts.get(sample));
    }

    public Collection<LabeledExtract> getLabeledExtracts() {
        return unmodifiableCollection(labeledExtractMap.values());
    }

    public Collection<LabeledExtract> getLabeledExtracts(Extract extract) {
        List<LabeledExtract> labeledExtracts = newArrayList();
        for (LabeledExtract labeledExtract : labeledExtractMap.values()) {
            if (labeledExtract.getExtract().equals(extract)) {
                labeledExtracts.add(labeledExtract);
            }
        }
        return labeledExtracts;
    }

    public LabeledExtract getLabeledExtract(String id) {
        LabeledExtract labeledExtract = labeledExtractMap.get(id);
        return labeledExtract == null ? null : labeledExtract;
    }

    public Collection<FileColumn> getFileColumns() {
        return unmodifiableCollection(fileColumns);
    }

    public FileColumn getFileColumn(int index) {
        return fileColumns.get(index);
    }

    public Set<Sample> getSamples(Protocol protocol) {
        return unmodifiableSet(protocol2Samples.get(protocol));
    }

    public Set<Extract> getExtracts(Protocol protocol) {
        return unmodifiableSet(protocol2Extracts.get(protocol));
    }

    public Set<LabeledExtract> getLabeledExtracts(Protocol protocol) {
        return unmodifiableSet(protocol2LabeledExtracts.get(protocol));
    }

    public Collection<Label> getLabels() {
        return unmodifiableCollection(labelMap.values());
    }

    public Collection<String> getLabelNames() {
        return transform(labelMap.values(), new Function<Label, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Label input) {
                return input.getName();
            }
        });
    }

    public Label addLabel(String labelName) {
        if (isNullOrEmpty(labelName)) {
            return null;
        }
        Label label = getLabel(labelName);
        if (label != null) {
            return label;
        }
        label = new Label(nextId(), labelName);
        labelMap.put(label.getId(), label);
        return label;
    }

    public Label getLabel(Integer id) {
        return labelMap.get(id);
    }

    public Label getLabel(String labelName) {
        for (Label label : labelMap.values()) {
            if (labelName.equals(label.getName())) {
                return label;
            }
        }
        return null;
    }

    public void addOrReLabel(String oldName, String newName) {
        Label oldLabel = getLabel(oldName);
        if (oldLabel == null) {
            addLabel(newName);
            return;
        }
        if (oldName.equals(newName)) {
            return;
        }
        oldLabel.setName(newName);
    }

    private int nextId() {
        return ++nextId;
    }

    public void restoreObjects() {
        restoreSample2Extracts();
        restoreLabeledExtracts();
    }

    private void restoreSample2Extracts() {
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

    private void restoreLabeledExtracts() {
        for (LabeledExtract labeledExtract : labeledExtractMap.values()) {
            labeledExtract.restoreObjects(this);
        }

        for (FileColumn fileColumn : fileColumns) {
            fileColumn.restoreObjects(this);
        }
    }

    public Sample getSampleByName(String name) {
        for (Sample sample : getSamples()) {
            if (sample.getName().equals(name)) {
                return sample;
            }
        }
        return null;
    }
}
