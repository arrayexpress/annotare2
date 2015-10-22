/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.submission.transform;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import uk.ac.ebi.fg.annotare2.submission.model.*;
import uk.ac.ebi.fg.annotare2.submission.transform.util.ValueSetter;

import java.io.IOException;
import java.util.*;

import static uk.ac.ebi.fg.annotare2.submission.transform.ExperimentProfileSerializer12.EXPERIMENT_PROFILE_JSON_FIELDS;
import static uk.ac.ebi.fg.annotare2.submission.transform.util.ClassUtilities.setFieldValue;
import static uk.ac.ebi.fg.annotare2.submission.transform.util.JsonUtilities.parseJson;

class ExperimentProfileDeserializer12 extends JsonDeserializer<ExperimentProfile> {

    @Override
    public ExperimentProfile deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ExperimentProfile experimentProfile = parseJson(jp, ExperimentProfile.class, EXPERIMENT_PROFILE_JSON_FIELDS,
                new ValueSetter<ExperimentProfile>("contacts", new TypeReference<Collection<Contact>>() {
                }) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void setValue(ExperimentProfile obj, Object value) {
                        Collection<Contact> contacts = (Collection<Contact>) value;
                        Map<Integer, Contact> map = new LinkedHashMap<>();
                        for (Contact contact : contacts) {
                            map.put(contact.getId(), contact);
                        }
                        setFieldValue(obj, "contactMap", map);
                    }
                },
                new ValueSetter<ExperimentProfile>("publications", new TypeReference<Collection<Publication>>() {
                }) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void setValue(ExperimentProfile obj, Object value) {
                        Collection<Publication> publications = (Collection<Publication>) value;
                        Map<Integer, Publication> map = new LinkedHashMap<>();
                        for (Publication publication : publications) {
                            map.put(publication.getId(), publication);
                        }
                        setFieldValue(obj, "publicationMap", map);
                    }
                },
                new ValueSetter<ExperimentProfile>("protocols", new TypeReference<Collection<Protocol>>() {
                }) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void setValue(ExperimentProfile obj, Object value) {
                        Collection<Protocol> protocols = (Collection<Protocol>) value;
                        List<Integer> order = new ArrayList<>();
                        Map<Integer, Protocol> map = new HashMap<>();
                        for (Protocol protocol : protocols) {
                            map.put(protocol.getId(), protocol);
                            order.add(protocol.getId());
                        }
                        setFieldValue(obj, "protocolMap", map);
                        setFieldValue(obj, "protocolOrder", order);
                    }
                },
                new ValueSetter<ExperimentProfile>("sampleAttributes", new TypeReference<Collection<SampleAttribute>>() {
                }) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void setValue(ExperimentProfile obj, Object value) {
                        Collection<SampleAttribute> sampleAttributes = (Collection<SampleAttribute>) value;
                        List<Integer> order = new ArrayList<>();
                        Map<Integer, SampleAttribute> map = new HashMap<>();
                        for (SampleAttribute attr : sampleAttributes) {
                            map.put(attr.getId(), attr);
                            order.add(attr.getId());
                        }
                        setFieldValue(obj, "sampleAttributeMap", map);
                        setFieldValue(obj, "sampleAttributeOrder", order);
                    }
                },
                new ValueSetter<ExperimentProfile>("samples", new TypeReference<Collection<Sample>>() {
                }) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void setValue(ExperimentProfile obj, Object value) {
                        Collection<Sample> samples = (Collection<Sample>) value;
                        Map<Integer, Sample> map = new LinkedHashMap<>();
                        for (Sample sample : samples) {
                            map.put(sample.getId(), sample);
                        }
                        setFieldValue(obj, "sampleMap", map);
                    }
                },
                new ValueSetter<ExperimentProfile>("extracts", new TypeReference<Collection<Extract>>() {
                }) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void setValue(ExperimentProfile obj, Object value) {
                        Collection<Extract> extracts = (Collection<Extract>) value;
                        Map<Integer, Extract> map = new LinkedHashMap<>();
                        for (Extract extract : extracts) {
                            map.put(extract.getId(), extract);
                        }
                        setFieldValue(obj, "extractMap", map);
                    }
                },
                new ValueSetter<ExperimentProfile>("labeledExtracts", new TypeReference<Collection<LabeledExtract>>() {
                }) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void setValue(ExperimentProfile obj, Object value) {
                        Collection<LabeledExtract> labeledExtracts = (Collection<LabeledExtract>) value;
                        Map<String, LabeledExtract> map = new LinkedHashMap<>();
                        for (LabeledExtract labeledExtract : labeledExtracts) {
                            map.put(labeledExtract.getId(), labeledExtract);
                        }
                        setFieldValue(obj, "labeledExtractMap", map);
                    }
                },
                new ValueSetter<ExperimentProfile>("labels", new TypeReference<Collection<Label>>() {
                }) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void setValue(ExperimentProfile obj, Object value) {
                        Collection<Label> labels = (Collection<Label>) value;
                        Map<Integer, Label> map = new LinkedHashMap<>();
                        for (Label label : labels) {
                            map.put(label.getId(), label);
                        }
                        setFieldValue(obj, "labelMap", map);
                    }
                },
                new ValueSetter<ExperimentProfile>("anonymousReview", new TypeReference<Boolean>() {
                }) {
                    @Override
                    public void setValue(ExperimentProfile obj, Object value) {
                        obj.setAnonymousReview(null != value && (Boolean)value);
                    }
                }
        );

        experimentProfile.restoreObjects();
        return experimentProfile;
    }
}
