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
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.util.Collections.unmodifiableCollection;

/**
 * @author Olga Melnichuk
 */
public class Experiment {

    @JsonProperty("properties")
    private Map<String, String> properties;

    @JsonProperty("nextId")
    int nextId;

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

    @JsonProperty("contacts")
    private Set<Contact> contacts;

    @JsonProperty("publications")
    private Set<Publication> publications;

    private Set<Source> sources;
    private Set<Sample> samples;
    private Set<Extract> extracts;
    private Set<LabeledExtract> labeledExtracts;
    private Set<Assay> assays;
    private Set<ArrayDataFile> arrayDataFiles;
    private Set<Scan> scans;


    @JsonCreator
    public Experiment(@JsonProperty("properties") Map<String, String> properties) {
        this.properties = newHashMap(properties);
        sources = newLinkedHashSet();
        samples = newLinkedHashSet();
        extracts = newLinkedHashSet();
        labeledExtracts = newLinkedHashSet();
        assays = newLinkedHashSet();
        arrayDataFiles = newLinkedHashSet();
        contacts = newLinkedHashSet();
        publications = newLinkedHashSet();
        scans = newLinkedHashSet();
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
        contacts.add(contact);
        return contact;
    }

    public Publication createPublication(Publication publication) {
        publication.setId(nextId());
        publications.add(publication);
        return publication;
    }

    public Source createSource() {
        Source source = new Source(nextId());
        sources.add(source);
        return source;
    }

    public Sample createSample() {
        Sample sample = new Sample(nextId());
        samples.add(sample);
        return sample;
    }

    public Extract createExtract() {
        Extract extract = new Extract(nextId());
        extracts.add(extract);
        return extract;
    }

    public LabeledExtract createLabeledExtract() {
        LabeledExtract labeledExtract = new LabeledExtract(nextId());
        labeledExtracts.add(labeledExtract);
        return labeledExtract;
    }

    public Assay createAssay() {
        Assay assay = new Assay(nextId());
        assays.add(assay);
        return assay;
    }

    public ArrayDataFile createArrayDataFile() {
        ArrayDataFile arrayDataFile = new ArrayDataFile(nextId());
        arrayDataFiles.add(arrayDataFile);
        return arrayDataFile;
    }

    public Scan createScan() {
        Scan scan = new Scan(nextId());
        scans.add(scan);
        return scan;
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }

    public static Experiment fromJsonString(String str) throws DataSerializationException {
        if (isNullOrEmpty(str)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            ExperimentData data = mapper.readValue(str, ExperimentData.class);
            return data.fixExperiment();
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
            return mapper.writeValueAsString(new ExperimentData(this));
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

    @JsonIgnore
    public Collection<Contact> getContacts() {
        return unmodifiableCollection(contacts);
    }

    @JsonIgnore
    public Collection<Source> getSources() {
        return unmodifiableCollection(sources);
    }

    @JsonIgnore
    public Collection<Sample> getSamples() {
        return unmodifiableCollection(samples);
    }

    @JsonIgnore
    public Collection<Extract> getExtracts() {
        return unmodifiableCollection(extracts);
    }

    @JsonIgnore
    public Collection<LabeledExtract> getLabeledExtracts() {
        return unmodifiableCollection(labeledExtracts);
    }

    @JsonIgnore
    public Collection<Assay> getAssays() {
        return unmodifiableCollection(assays);
    }

    @JsonIgnore
    public Collection<ArrayDataFile> getArrayDataFiles() {
        return unmodifiableCollection(arrayDataFiles);
    }

    @JsonIgnore
    public Collection<Scan> getScans() {
        return unmodifiableCollection(scans);
    }

    void restoreSources(Collection<Source> sources) {
        this.sources = newLinkedHashSet(sources);
    }

    void restoreSamples(Collection<Sample> samples) {
        this.samples = newLinkedHashSet(samples);
    }

    void restoreExtracts(Collection<Extract> extracts) {
        this.extracts = newLinkedHashSet(extracts);
    }

    void restoreLabeledExtracts(Collection<LabeledExtract> labeledExtracts) {
        this.labeledExtracts = newLinkedHashSet(labeledExtracts);
    }

    void restoreAssays(Collection<Assay> assays) {
        this.assays = newLinkedHashSet(assays);
    }

    void restoreScans(Collection<Scan> scans) {
        this.scans = newLinkedHashSet(scans);
    }

    void restoreArrayDataFiles(Collection<ArrayDataFile> files) {
        this.arrayDataFiles = newLinkedHashSet(files);
    }

    public Source getSource(int id) {
        for (Source source : sources) {
            if (id == source.getId()) {
                return source;
            }
        }
        return null;
    }

    public Sample getSample(int id) {
        for (Sample sample : samples) {
            if (id == sample.getId()) {
                return sample;
            }
        }
        return null;
    }


    public Extract getExtract(int id) {
        for (Extract extract : extracts) {
            if (id == extract.getId()) {
                return extract;
            }
        }
        return null;
    }

    public LabeledExtract getLabeledExtract(int id) {
        for (LabeledExtract labeledExtract : labeledExtracts) {
            if (id == labeledExtract.getId()) {
                return labeledExtract;
            }
        }
        return null;
    }

    public Assay getAssay(int id) {
        for (Assay assay : assays) {
            if (id == assay.getId()) {
                return assay;
            }
        }
        return null;
    }


}
