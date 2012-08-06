/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.magetab.limpopo.idf;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class Investigation {

    private String mageTabVersion;

    private Date dateOfExperiment;

    private Date dateOfPublicRelease;

    private String title;

    private String description;

    private Term accession;

    private final List<ExperimentalDesign> experimentalDesigns = new ArrayList<ExperimentalDesign>();

    private final List<ExperimentalFactor> experimentalFactors = new ArrayList<ExperimentalFactor>();

    private final List<Person> contacts = new ArrayList<Person>();

    private final List<QualityControl> qualityControls = new ArrayList<QualityControl>();

    private final List<Replicate> replicates = new ArrayList<Replicate>();

    private final List<Normalization> normalizations = new ArrayList<Normalization>();

    private final List<Publication> publications = new ArrayList<Publication>();

    private final List<Protocol> protocols = new ArrayList<Protocol>();

    private final Map<String, Set<String>> comments = new HashMap<String, Set<String>>();

    private final List<TermSource> termSources = new ArrayList<TermSource>();

    private final List<String> sdrfFiles = new ArrayList<String>();

    public String getMagTabVersion() {
        return mageTabVersion;
    }

    public Date getDateOfExperiment() {
        return dateOfExperiment == null ? null : new Date(dateOfExperiment.getTime());
    }

    public Date getDateOfPublicRelease() {
        return dateOfPublicRelease == null ? null : new Date(dateOfPublicRelease.getTime());
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Term getAccession() {
        return accession;
    }

    public Collection<String> getSdrfFiles() {
        return Collections.unmodifiableCollection(sdrfFiles);
    }

    public Collection<ExperimentalDesign> getExperimentalDesigns() {
        return Collections.unmodifiableCollection(experimentalDesigns);
    }

    public Collection<ExperimentalFactor> getExperimentalFactors() {
        return Collections.unmodifiableCollection(experimentalFactors);
    }

    public Collection<Person> getContacts() {
        return Collections.unmodifiableCollection(contacts);
    }

    public Collection<QualityControl> getQualityControls() {
        return Collections.unmodifiableCollection(qualityControls);
    }

    public Collection<Replicate> getReplicates() {
        return Collections.unmodifiableCollection(replicates);
    }

    public Collection<Normalization> getNormalizations() {
        return Collections.unmodifiableCollection(normalizations);
    }

    public Collection<Publication> getPublications() {
        return Collections.unmodifiableCollection(publications);
    }

    public Collection<Protocol> getProtocols() {
        return Collections.unmodifiableCollection(protocols);
    }

    public Map<String, Set<String>> getComments() {
        //TODO
        return Collections.unmodifiableMap(comments);
    }

    public Collection<TermSource> getTermSources() {
        return Collections.unmodifiableCollection(termSources);
    }

    public static class Builder {

        private final Investigation inv = new Investigation();

        public Builder setMageTabVersion(String version) {
            inv.mageTabVersion = version;
            return this;
        }

        public Builder setAccession(Term accession) {
            inv.accession = accession;
            return this;
        }

        public Builder setTitle(String title) {
            inv.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            inv.description = description;
            return this;
        }

        public Builder setDateOfExperiment(Date dateOfExperiment) {
            inv.dateOfExperiment = dateOfExperiment;
            return this;
        }

        public Builder setDateOfPublicRelease(Date dateOfPublicRelease) {
            inv.dateOfPublicRelease = dateOfPublicRelease;
            return this;
        }

        public Builder setExperimentalDesigns(Collection<ExperimentalDesign> coll) {
            inv.experimentalDesigns.addAll(coll);
            return this;
        }

        public Builder setExperimentalFactors(Collection<ExperimentalFactor> coll) {
            inv.experimentalFactors.addAll(coll);
            return this;
        }

        public Builder setNormalizations(List<Normalization> coll) {
            inv.normalizations.addAll(coll);
            return this;
        }

        public Builder setQualityControls(List<QualityControl> coll) {
            inv.qualityControls.addAll(coll);
            return this;
        }

        public Builder setReplicates(List<Replicate> coll) {
            inv.replicates.addAll(coll);
            return this;
        }

        public Investigation build() {
            return inv;
        }

        public Builder setContacts(List<Person> contacts) {
            inv.contacts.addAll(contacts);
            return this;
        }

        public Builder setPublications(List<Publication> publications) {
            inv.publications.addAll(publications);
            return this;
        }

        public Builder setProtocols(List<Protocol> protocols) {
            inv.protocols.addAll(protocols);
            return this;
        }

        public Builder setComments(Map<String, Set<String>> comments) {
            for(String key : comments.keySet()) {
                Set<String> values = new HashSet<String>();
                values.addAll(comments.get(key));
                inv.comments.put(key, values);
            }
            return this;
        }

        public Builder setTermSources(List<TermSource> termSources) {
            //TODO add termsources when other objects are added
            inv.termSources.addAll(termSources);
            return this;
        }

        public Builder setSdrfFiles(List<String> sdrfFiles) {
            inv.sdrfFiles.addAll(sdrfFiles);
            return this;
        }
    }
}
