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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private List<Contact> contacts;

    @JsonProperty("publications")
    private List<Publication> publications;

    private List<Source> sources;
    private List<Sample> samples;
    private List<Extract> extracts;
    private List<LabeledExtract> labeledExtracts;
    private List<Assay> assays;
    private List<ArrayDataFile> arrayDataFiles;
    private List<Scan> scans;


    @JsonCreator
    public Experiment(@JsonProperty("properties") Map<String, String> properties) {
        this.properties = newHashMap(properties);
        sources = newArrayList();
        samples = newArrayList();
        extracts = newArrayList();
        labeledExtracts = newArrayList();
        assays = newArrayList();
        arrayDataFiles = newArrayList();
        contacts = newArrayList();
        publications = newArrayList();
        scans = newArrayList();
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
    public List<Source> getSources() {
        return unmodifiableList(sources);
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

    @JsonIgnore
    public List<Scan> getScans() {
        return unmodifiableList(scans);
    }

    void restoreSources(List<Source> sources) {
        this.sources = newArrayList(sources);
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

    void restoreScans(List<Scan> scans) {
        this.scans = newArrayList(scans);
    }

    void restoreArrayDataFiles(List<ArrayDataFile> files) {
        this.arrayDataFiles = newArrayList(files);
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
