/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package uk.ac.ebi.fg.annotare2.submission.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newLinkedHashMap;

public class ExperimentProfile10 {

    private static final long serialVersionUID = 7357226309547876375L;

    int nextId;

    private ExperimentProfileType type;

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

    private Map<Integer, Set<Integer>> sampleId2ExtractIds;
    private MultiSets<Sample, Extract> sample2Extracts;

    private Map<Integer, Set<Integer>> protocolId2SampleIds;
    private MultiSets<Protocol, Sample> protocol2Samples;

    private Map<Integer, Set<Integer>> protocolId2ExtractIds;
    private MultiSets<Protocol, Extract> protocol2Extracts;

    private Map<Integer, Set<String>> protocolId2LabeledExtractIds;
    private MultiSets<Protocol, LabeledExtract> protocol2LabeledExtracts;

    @SuppressWarnings("unused")
    ExperimentProfile10() {
        /* used by GWT serialization */
        this(null);
    }

    public ExperimentProfile10(ExperimentProfileType type) {
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
}
