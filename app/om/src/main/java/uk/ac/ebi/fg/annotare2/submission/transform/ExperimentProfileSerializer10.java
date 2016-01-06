/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.submission.transform;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.ebi.fg.annotare2.submission.model.*;
import uk.ac.ebi.fg.annotare2.submission.transform.util.ValueGetter;

import java.io.IOException;
import java.util.*;

import static java.util.Arrays.asList;
import static uk.ac.ebi.fg.annotare2.submission.transform.util.JsonUtilities.generateJson;

/**
 * @author Olga Melnichuk
 */
class ExperimentProfileSerializer10 extends JsonSerializer<ExperimentProfile> {

    static List<String> EXPERIMENT_PROFILE_JSON_FIELDS = asList("nextId",
            "type",
            "title",
            "description",
            "experimentDate",
            "publicReleaseDate",
            "arrayDesign",
            "aeExperimentType",
            "labels",
            "experimentalDesigns",
            "contacts",
            "publications",
            "protocols",
            "sampleAttributes",
            "samples",
            "extracts",
            "labeledExtracts",
            "sampleId2ExtractIds",
            "fileColumns",
            "protocolId2SampleIds",
            "protocolId2ExtractIds",
            "protocolId2LabeledExtractIds"
    );

    @Override
    public void serialize(ExperimentProfile experimentProfile, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        generateJson(jgen, experimentProfile, EXPERIMENT_PROFILE_JSON_FIELDS,
                new ValueGetter<ExperimentProfile>("contacts") {
                    @Override
                    public Object getValue(ExperimentProfile obj) {
                        return obj.getContacts();
                    }
                },
                new ValueGetter<ExperimentProfile>("publications") {
                    @Override
                    public Object getValue(ExperimentProfile obj) {
                        return obj.getPublications();
                    }
                },
                new ValueGetter<ExperimentProfile>("protocols") {
                    @Override
                    public Object getValue(ExperimentProfile obj) {
                        return obj.getProtocols();
                    }
                },
                new ValueGetter<ExperimentProfile>("sampleAttributes") {
                    @Override
                    public Object getValue(ExperimentProfile obj) {
                        return obj.getSampleAttributes();
                    }
                },
                new ValueGetter<ExperimentProfile>("samples") {
                    @Override
                    public Object getValue(ExperimentProfile obj) {
                        return obj.getSamples();
                    }
                },
                new ValueGetter<ExperimentProfile>("extracts") {
                    @Override
                    public Object getValue(ExperimentProfile obj) {
                        return obj.getExtracts();
                    }
                },
                new ValueGetter<ExperimentProfile>("labeledExtracts") {
                    @Override
                    public Object getValue(ExperimentProfile obj) {
                        return obj.getLabeledExtracts();
                    }
                },
                new ValueGetter<ExperimentProfile>("sampleId2ExtractIds") {
                    @Override
                    public Object getValue(ExperimentProfile obj) {
                        Map<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
                        for (Sample sample : obj.getSamples()) {
                            Collection<Extract> extracts = obj.getExtracts(sample);
                            Set<Integer> extractIds = new HashSet<Integer>();
                            for (Extract extract : extracts) {
                                 extractIds.add(extract.getId());
                            }
                            map.put(sample.getId(), extractIds);
                        }
                        return map;
                    }
                },
                new ValueGetter<ExperimentProfile>("labels") {
                    @Override
                    public Object getValue(ExperimentProfile obj) {
                        return obj.getLabels();
                    }
                },
                new ValueGetter<ExperimentProfile>("protocolId2SampleIds") {
                    @Override
                    public Object getValue(ExperimentProfile obj) {
                        Map<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
                        for (Protocol protocol : obj.getProtocols()) {
                            Collection<Sample> samples = obj.getSamples(protocol);
                            Set<Integer> sampleIds = new HashSet<Integer>();
                            for (Sample sample : samples) {
                                sampleIds.add(sample.getId());
                            }
                            map.put(protocol.getId(), sampleIds);
                        }
                        return map;
                    }
                },
                new ValueGetter<ExperimentProfile>("protocolId2ExtractIds") {
                    @Override
                    public Object getValue(ExperimentProfile obj) {
                        Map<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
                        for (Protocol protocol : obj.getProtocols()) {
                            Collection<Extract> extracts = obj.getExtracts(protocol);
                            Set<Integer> extractIds = new HashSet<Integer>();
                            for (Extract extract : extracts) {
                                extractIds.add(extract.getId());
                            }
                            map.put(protocol.getId(), extractIds);
                        }
                        return map;
                    }
                },
                new ValueGetter<ExperimentProfile>("protocolId2LabeledExtractIds") {
                    @Override
                    public Object getValue(ExperimentProfile obj) {
                        Map<Integer, Set<String>> map = new HashMap<Integer, Set<String>>();
                        for (Protocol protocol : obj.getProtocols()) {
                            Collection<LabeledExtract> labeledExtracts = obj.getLabeledExtracts(protocol);
                            Set<String> labeledExtractIds = new HashSet<String>();
                            for (LabeledExtract labeledExtract : labeledExtracts) {
                                labeledExtractIds.add(labeledExtract.getId());
                            }
                            map.put(protocol.getId(), labeledExtractIds);
                        }
                        return map;
                    }
                }
        );
    }
}
