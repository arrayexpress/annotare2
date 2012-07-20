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

package uk.ac.ebi.fg.annotare2.magetab.parser;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.fg.annotare2.magetab.idf.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
class IdfProxy {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD");
    public static final String SEMICOLON = ";";

    private final IDF idf;
    private Map<String, TermSource> termSources;

    public IdfProxy(IDF idf) {
        this.idf = idf;
    }

    public String getMageTabVersion() {
        return idf.magetabVersion;
    }

    public String getTitle() {
        return idf.investigationTitle;
    }

    public String getDescription() {
        return idf.experimentDescription;
    }

    public Term getAccession() {
        return new Term(null, idf.accession, TermSource.DEFAULT);
    }

    public Date getDateOfExperiment() throws MageTabParseException {
        return parseDate(idf.dateOfExperiment);
    }

    public Date getDateOfPublicRelease() throws MageTabParseException {
        return parseDate(idf.publicReleaseDate);
    }

    public Map<String, Set<String>> getComments() {
        return idf.getComments();
    }

    public List<String> getSdrfFiles() {
        return idf.sdrfFile;
    }

    public List<TermSource> getTermSources() throws MageTabParseException {
        int size = idf.termSourceName.size();
        Table table = new Table(size)
                .add(idf.termSourceName, "termSourceName")
                .addOptional(idf.termSourceVersion, "termSourceVersion")
                .add(idf.termSourceFile, "termSourceFile");

        List<TermSource> out = new ArrayList<TermSource>();
        for (int i = 0; i < table.size(); i++) {
            String[] row = table.get(i);
            if (row != null) {
                out.add(new TermSource(
                        row[0],
                        row[1],
                        row[2]
                ));
            }
        }
        return out;
    }

    public List<ExperimentalDesign> getExperimentDesigns() throws MageTabParseException {
        int size = idf.experimentalDesign.size();
        Table table = new Table(size)
                .add(idf.experimentalDesign, "experimentalDesign")
                .addOptional(idf.experimentalDesignTermAccession, "experimentalDesignTermAccession")
                .addOptional(idf.experimentalDesignTermSourceREF, "experimentalDesignTermSourceREF");

        List<ExperimentalDesign> out = new ArrayList<ExperimentalDesign>();
        for (int i = 0; i < table.size(); i++) {
            String[] row = table.get(i);
            out.add(new ExperimentalDesign(
                    new Term(
                            row[0],
                            row[1],
                            lookup(row[2])
                    )
            ));
        }
        return out;
    }

    public List<ExperimentalFactor> getExperimentalFactors() throws MageTabParseException {
        int size = idf.experimentalFactorName.size();
        Table table = new Table(size)
                .add(idf.experimentalFactorName, "experimentalFactorName")
                .add(idf.experimentalFactorType, "experimentalFactorType")
                .addOptional(idf.experimentalFactorTermAccession, "experimentalFactorTermAccession")
                .addOptional(idf.experimentalFactorTermSourceREF, "experimentalFactorTermSourceREF");

        List<ExperimentalFactor> out = new ArrayList<ExperimentalFactor>();
        for (int i = 0; i < table.size(); i++) {
            String[] row = table.get(i);
            out.add(new ExperimentalFactor(
                    row[0],
                    new Term(
                            row[1],
                            row[2],
                            lookup(row[3])
                    )
            ));
        }
        return out;
    }

    public List<Normalization> getNormalizations() throws MageTabParseException {
        int size = idf.normalizationType.size();
        Table table = new Table(size)
                .add(idf.normalizationType, "normalizationType")
                .addOptional(idf.normalizationTermAccession, "normalizationTermAccession")
                .addOptional(idf.normalizationTermSourceREF, "normalizationTermSourceREF");

        List<Normalization> out = new ArrayList<Normalization>();
        for (int i = 0; i < table.size(); i++) {
            String[] row = table.get(i);
            if (row != null) {
                out.add(new Normalization(
                        new Term(
                                row[0],
                                row[1],
                                lookup(row[2]))
                ));
            }
        }
        return out;
    }

    public List<QualityControl> getQualityControls() throws MageTabParseException {
        int size = idf.qualityControlType.size();
        Table table = new Table(size)
                .add(idf.qualityControlType, "qualityControlType")
                .addOptional(idf.qualityControlTermAccession, "qualityControlTermAccession")
                .addOptional(idf.qualityControlTermSourceREF, "qualityControlTermSourceREF");

        List<QualityControl> out = new ArrayList<QualityControl>();
        for (int i = 0; i < table.size(); i++) {
            String[] row = table.get(i);
            if (row != null) {
                out.add(new QualityControl(
                        new Term(
                                row[0],
                                row[1],
                                lookup(row[2]))
                ));
            }
        }
        return out;
    }

    public List<Replicate> getReplicates() throws MageTabParseException {
        int size = idf.replicateType.size();
        Table table = new Table(size)
                .add(idf.replicateType, "replicateType")
                .addOptional(idf.replicateTermAccession, "replicateTermAccession")
                .addOptional(idf.replicateTermSourceREF, "replicateTermSourceREF");

        List<Replicate> out = new ArrayList<Replicate>();
        for (int i = 0; i < table.size(); i++) {
            String[] row = table.get(i);
            if (row != null) {
                out.add(new Replicate(
                        new Term(
                                row[0],
                                row[1],
                                lookup(row[2]))
                ));
            }
        }
        return out;
    }

    public List<Person> getContacts() throws MageTabParseException {
        int size = idf.personFirstName.size();
        Table table = new Table(size)
                .add(idf.personFirstName, "personFirstName")
                .add(idf.personLastName, "personLastName")
                .addOptional(idf.personMidInitials, "personMidInitials")
                .addOptional(idf.personAddress, "personAddress")
                .addOptional(idf.personAffiliation, "personAffiliation")
                .addOptional(idf.personEmail, "personEmail")
                .addOptional(idf.personFax, "personFax")
                .addOptional(idf.personPhone, "personPhone")
                .addOptional(idf.personRoles, "personRoles")
                .addOptional(idf.personRolesTermAccession, "personRolesTermAccession")
                .addOptional(idf.personRolesTermSourceREF, "personRolesTermSourceREF");

        List<Person> out = new ArrayList<Person>();
        for (int i = 0; i < table.size(); i++) {
            String[] row = table.get(i);
            if (row == null) {
                continue;
            }

            List<String> roleNames = row[8] == null ? Collections.<String>emptyList() : asList(row[8].split(SEMICOLON));
            List<String> roleAccessions = row[9] == null ? Collections.<String>emptyList() : asList(row[9].split(SEMICOLON));

            Table tableRoles = new Table(roleNames.size())
                    .add(roleNames, "roleNames")
                    .addOptional(roleAccessions, "roleAccessions");

            TermList.Builder roleBuilder = new TermList.Builder(lookup(row[10]));
            for (int j = 0; j < roleNames.size(); j++) {
                String[] r = tableRoles.get(j);
                roleBuilder.addTerm(r[0], r[1]);
            }

            out.add(new Person.Builder()
                    .setFirstName(row[0])
                    .setLastName(row[1])
                    .setMidInitials(row[2])
                    .setAddress(row[3])
                    .setAffiliation(row[4])
                    .setEmail(row[5])
                    .setFax(row[6])
                    .setPhone(row[7])
                    .setRoles(roleBuilder.build())
                    .build());
        }
        return out;
    }

    public List<Publication> getPublications() throws MageTabParseException {
        int size = idf.publicationTitle.size();
        Table table = new Table(size)
                .add(idf.publicationTitle, "publicationTitle")
                .addOptional(idf.publicationAuthorList, "publicationAuthorList")
                .addOptional(idf.publicationDOI, "publicationDOI")
                .addOptional(idf.pubMedId, "pubMedId")
                .addOptional(idf.publicationStatus, "publicationStatus")
                .addOptional(idf.publicationStatusTermAccession, "publicationStatusTermAccession")
                .addOptional(idf.publicationStatusTermSourceREF, "publicationStatusTermSourceREF");

        List<Publication> out = new ArrayList<Publication>();
        for (int i = 0; i < table.size(); i++) {
            String[] row = table.get(i);
            out.add(new Publication.Builder()
                    .setTitle(row[0])
                    .setAuthors(row[1])
                    .setDoi(row[2])
                    .setPubMedId(row[3])
                    .setStatus(new Term(
                            row[4],
                            row[5],
                            lookup(row[6])
                    )).build());
        }
        return out;
    }

    public List<Protocol> getProtocols() throws MageTabParseException {
        int size = idf.protocolName.size();
        Table table = new Table(size)
                .add(idf.protocolName, "protocolName")
                .addOptional(idf.protocolDescription, "protocolDescription")
                .addOptional(idf.protocolParameters, "protocolParameters")
                .addOptional(idf.protocolHardware, "protocolHardware")
                .addOptional(idf.protocolSoftware, "protocolSoftware")
                .addOptional(idf.protocolContact, "protocolContact")
                .addOptional(idf.protocolType, "protocolType")
                .addOptional(idf.protocolTermAccession, "protocolTermAccession")
                .addOptional(idf.protocolTermSourceREF, "protocolTermSourceREF");

        List<Protocol> out = new ArrayList<Protocol>();
        for (int i = 0; i < table.size(); i++) {
            String[] row = table.get(i);
            String[] params = row[2] == null ? new String[0] : row[2].split(SEMICOLON);

            out.add(new Protocol.Builder()
                    .setName(row[0])
                    .setDescription(row[1])
                    .setParameters(asList(params))
                    .setHardware(row[3])
                    .setSoftware(row[4])
                    .setContact(row[5])
                    .setType(new Term(
                            row[6],
                            row[7],
                            lookup(row[8])
                    )).build());
        }
        return out;
    }

    private TermSource lookup(String name) throws MageTabParseException {
        if (name == null) {
            return TermSource.DEFAULT;
        }

        if (termSources == null) {
            initTermSources();
        }

        TermSource source = termSources.get(name);
        if (source == null) {
            throw new MageTabParseException("Term Source '" + name + "' doesn't exist");
        }
        return source;
    }

    private void initTermSources() throws MageTabParseException {
        termSources = new HashMap<String, TermSource>();
        for (TermSource source : getTermSources()) {
            termSources.put(source.getName(), source);
        }
    }

    private Date parseDate(String date) throws MageTabParseException {
        try {
            return date == null || date.isEmpty() ? null : dateFormat.parse(date);
        } catch (ParseException e) {
            throw new MageTabParseException(e);
        }
    }

    public Investigation toInvestigation() throws MageTabParseException {
        return new Investigation.Builder()
                .setMageTabVersion(getMageTabVersion())
                .setAccession(getAccession())
                .setTitle(getTitle())
                .setDescription(getDescription())
                .setDateOfExperiment(getDateOfExperiment())
                .setDateOfPublicRelease(getDateOfPublicRelease())
                .setExperimentalDesigns(getExperimentDesigns())
                .setExperimentalFactors(getExperimentalFactors())
                .setNormalizations(getNormalizations())
                .setQualityControls(getQualityControls())
                .setReplicates(getReplicates())
                .setContacts(getContacts())
                .setPublications(getPublications())
                .setProtocols(getProtocols())
                .setComments(getComments())
                .setTermSources(getTermSources())
                .setSdrfFiles(getSdrfFiles())
                .build();
    }

    public static class Table {

        private int size;

        private List<TableColumn> lists = new ArrayList<TableColumn>();

        public Table(int size) {
            this.size = size;
        }

        public Table add(List<String> list, String name) throws MageTabParseException {
            if (list.size() != size) {
                throw new MageTabParseException("Expected collection '" + name + "' size is " + size + ", but got " + list);
            }
            lists.add(new TableColumn(list, false, name));
            return this;
        }

        public Table addOptional(List<String> list, String name) {
            lists.add(new TableColumn(list, true, name));
            return this;
        }

        public int size() {
            return size;
        }

        public String[] get(int i) throws MageTabParseException {
            String[] arr = new String[lists.size()];
            int notNulls = 0;
            for (int j = 0; j < lists.size(); j++) {
                arr[j] = (lists.get(j)).get(i);
                if (arr[j] != null) {
                    notNulls++;
                }
            }
            return notNulls == 0 ? null : arr;
        }
    }

    public static class TableColumn {
        private static final String EMPTY = null;
        private final List<String> values;
        private final boolean isOptional;
        private final String name;

        public TableColumn(List<String> values, boolean optional, String name) {
            this.values = values;
            isOptional = optional;
            this.name = name;
        }

        public String get(int i) throws MageTabParseException {
            if (i < values.size()) {
                String v = values.get(i);
                return v == null || v.isEmpty() ? EMPTY : v;
            } else if (isOptional) {
                return EMPTY;
            }
            throw new MageTabParseException("The list '" + name + "' has less values than required");
        }
    }

}
