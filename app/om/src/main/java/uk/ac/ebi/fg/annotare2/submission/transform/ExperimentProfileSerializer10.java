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

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.transform.util.ValueGetter;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static uk.ac.ebi.fg.annotare2.submission.transform.util.JsonUtilities.generateJson;

/**
 * @author Olga Melnichuk
 */
public class ExperimentProfileSerializer10 extends JsonSerializer<ExperimentProfile> {

    static List<String> JSON_FIELDS = asList("nextId",
            "type",
            "title",
            "description",
            "experimentDate",
            "publicReleaseDate",
            "arrayDesign",
            "aeExperimentType",
            "experimentalDesigns",
            "contacts",
            "publications"
            );

    @Override
    public void serialize(ExperimentProfile experimentProfile, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        generateJson(jgen, experimentProfile, JSON_FIELDS,
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
                }
        );
    }
}
