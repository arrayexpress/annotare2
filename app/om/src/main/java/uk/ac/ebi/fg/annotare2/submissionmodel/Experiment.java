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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newLinkedHashMap;
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

    @JsonProperty("contactMap")
    private Map<Integer, Contact> contacts;

    @JsonProperty("publicationMap")
    private Map<Integer, Publication> publications;

    @JsonProperty("sourceMap")
    private Map<Integer, Source> sources;

    @JsonProperty("sampleMap")
    private Map<Integer, Sample> samples;

    @JsonProperty("extractMap")
    private Map<Integer, Extract> extracts;

    @JsonProperty("labeledExtractMap")
    private Map<Integer, LabeledExtract> labeledExtracts;

    @JsonProperty("assayMap")
    private Map<Integer, Assay> assays;

    @JsonProperty("arrayDataFileMap")
    private Map<Integer, ArrayDataFile> arrayDataFiles;

    @JsonProperty("scanMap")
    private Map<Integer, Scan> scans;

    @JsonCreator
    public Experiment(@JsonProperty("properties") Map<String, String> properties) {
        this.properties = newHashMap(properties);
        sources = newLinkedHashMap();
        samples = newLinkedHashMap();
        extracts = newLinkedHashMap();
        labeledExtracts = newLinkedHashMap();
        assays = newLinkedHashMap();
        arrayDataFiles = newLinkedHashMap();
        scans = newLinkedHashMap();

        contacts = newLinkedHashMap();
        publications = newLinkedHashMap();
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

    public Publication createPublication(Publication publication) {
        publication.setId(nextId());
        publications.put(publication.getId(), publication);
        return publication;
    }

    public Source createSource() {
        Source source = new Source(nextId());
        sources.put(source.getId(), source);
        return source;
    }

    public Sample createSample() {
        Sample sample = new Sample(nextId());
        samples.put(sample.getId(), sample);
        return sample;
    }

    public Extract createExtract() {
        Extract extract = new Extract(nextId());
        extracts.put(extract.getId(), extract);
        return extract;
    }

    public LabeledExtract createLabeledExtract() {
        LabeledExtract labeledExtract = new LabeledExtract(nextId());
        labeledExtracts.put(labeledExtract.getId(), labeledExtract);
        return labeledExtract;
    }

    public Assay createAssay() {
        Assay assay = new Assay(nextId());
        assays.put(assay.getId(), assay);
        return assay;
    }

    public ArrayDataFile createArrayDataFile() {
        ArrayDataFile arrayDataFile = new ArrayDataFile(nextId());
        arrayDataFiles.put(arrayDataFile.getId(), arrayDataFile);
        return arrayDataFile;
    }

    public Scan createScan() {
        Scan scan = new Scan(nextId());
        scans.put(scan.getId(), scan);
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
            Experiment exp = mapper.readValue(str, Experiment.class);
            exp.fixReferences();
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

    @JsonIgnore
    public Collection<Contact> getContacts() {
        return unmodifiableCollection(contacts.values());
    }

    @JsonIgnore
    public Collection<Publication> getPublications() {
        return unmodifiableCollection(publications.values());
    }

    @JsonIgnore
    public Collection<Source> getSources() {
        return unmodifiableCollection(sources.values());
    }

    @JsonIgnore
    public Collection<Sample> getSamples() {
        return unmodifiableCollection(samples.values());
    }

    @JsonIgnore
    public Collection<Extract> getExtracts() {
        return unmodifiableCollection(extracts.values());
    }

    @JsonIgnore
    public Collection<LabeledExtract> getLabeledExtracts() {
        return unmodifiableCollection(labeledExtracts.values());
    }

    @JsonIgnore
    public Collection<Assay> getAssays() {
        return unmodifiableCollection(assays.values());
    }

    @JsonIgnore
    public Collection<ArrayDataFile> getArrayDataFiles() {
        return unmodifiableCollection(arrayDataFiles.values());
    }

    @JsonIgnore
    public Collection<Scan> getScans() {
        return unmodifiableCollection(scans.values());
    }


    public Source getSource(int id) {
        return sources.get(id);
    }

    public Sample getSample(int id) {
        return samples.get(id);
    }


    public Extract getExtract(int id) {
        return extracts.get(id);
    }

    public LabeledExtract getLabeledExtract(int id) {
        return labeledExtracts.get(id);
    }

    public Assay getAssay(int id) {
        return assays.get(id);
    }


    public Contact getContact(int id) {
        return contacts.get(id);
    }

    private void fixReferences() {
        for (Source s : sources.values()) {
            fix(s);
        }
        for (Sample s : samples.values()) {
            fix(s);
        }
        for (Extract e : extracts.values()) {
            fix(e);
        }
        for (LabeledExtract e : labeledExtracts.values()) {
            fix(e);
        }
        for (Assay a : assays.values()) {
            fix(a);
        }
        for (Scan s : scans.values()) {
            fix(s);
        }
        for (ArrayDataFile a : arrayDataFiles.values()) {
            fix(a);
        }
    }

    private Source fix(Source source) {
        source.setAllSamples(
                Lists.transform(source.getSampleIds(), new Function<Integer, Sample>() {
                    @Nullable
                    @Override
                    public Sample apply(@Nullable Integer id) {
                        return samples.get(id);
                    }
                }));
        source.setAllExtracts(
                Lists.transform(source.getExtractIds(), new Function<Integer, Extract>() {
                    @Nullable
                    @Override
                    public Extract apply(@Nullable Integer id) {
                        return extracts.get(id);
                    }
                }));
        return source;
    }

    private Sample fix(Sample sample) {
        sample.setAllExtracts(
                Lists.transform(sample.getExtractIds(), new Function<Integer, Extract>() {
                    @Nullable
                    @Override
                    public Extract apply(@Nullable Integer id) {
                        return extracts.get(id);
                    }
                }));
        return sample;
    }

    private Extract fix(Extract extract) {
        extract.setAllAssays(
                Lists.transform(extract.getAssayIds(), new Function<Integer, Assay>() {
                    @Nullable
                    @Override
                    public Assay apply(@Nullable Integer id) {
                        return assays.get(id);
                    }
                }));
        extract.setAllLabeledExtracts(
                Lists.transform(extract.getLabeledExtractIds(), new Function<Integer, LabeledExtract>() {
                    @Nullable
                    @Override
                    public LabeledExtract apply(@Nullable Integer id) {
                        return labeledExtracts.get(id);
                    }
                }));
        return extract;
    }

    private LabeledExtract fix(LabeledExtract labeledExtract) {
        labeledExtract.setAllAssays(
                Lists.transform(labeledExtract.getAssayIds(), new Function<Integer, Assay>() {
                    @Nullable
                    @Override
                    public Assay apply(@Nullable Integer id) {
                        return assays.get(id);
                    }
                }));
        return labeledExtract;
    }

    private Assay fix(Assay assay) {
        assay.setAllArrayDataFiles(
                Lists.transform(assay.getArrayDataFileIds(), new Function<Integer, ArrayDataFile>() {
                    @Nullable
                    @Override
                    public ArrayDataFile apply(@Nullable Integer id) {
                        return arrayDataFiles.get(id);
                    }
                }));
        assay.setAllScans(
                Lists.transform(assay.getScansIds(), new Function<Integer, Scan>() {
                    @Nullable
                    @Override
                    public Scan apply(@Nullable Integer id) {
                        return scans.get(id);
                    }
                }));
        return assay;
    }

    private Scan fix(Scan scan) {
        return scan;
    }

    private ArrayDataFile fix(ArrayDataFile file) {
        return file;
    }
}
