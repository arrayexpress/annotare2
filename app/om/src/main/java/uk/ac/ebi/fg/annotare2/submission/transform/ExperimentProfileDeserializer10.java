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

package uk.ac.ebi.fg.annotare2.submission.transform;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.type.TypeReference;
import uk.ac.ebi.fg.annotare2.submission.model.Contact;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.Publication;
import uk.ac.ebi.fg.annotare2.submission.transform.util.ValueSetter;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static uk.ac.ebi.fg.annotare2.submission.transform.ExperimentProfileSerializer10.JSON_FIELDS;
import static uk.ac.ebi.fg.annotare2.submission.transform.util.ClassUtilities.setFieldValue;
import static uk.ac.ebi.fg.annotare2.submission.transform.util.JsonUtilities.parseJson;

/**
 * @author Olga Melnichuk
 */
public class ExperimentProfileDeserializer10 extends JsonDeserializer<ExperimentProfile> {

    @Override
    public ExperimentProfile deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return parseJson(jp, ExperimentProfile.class, JSON_FIELDS,
                new ValueSetter<ExperimentProfile>("contacts", new TypeReference<Collection<Contact>>() {
                }) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void setValue(ExperimentProfile obj, Object value) {
                        Collection<Contact> contacts = (Collection<Contact>) value;
                        Map<Integer, Contact> map = new LinkedHashMap<Integer, Contact>();
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
                        Map<Integer, Publication> map = new LinkedHashMap<Integer, Publication>();
                        for (Publication publication : publications) {
                            map.put(publication.getId(), publication);
                        }
                        setFieldValue(obj, "publicationMap", map);
                    }
                }
        );
    }
}
