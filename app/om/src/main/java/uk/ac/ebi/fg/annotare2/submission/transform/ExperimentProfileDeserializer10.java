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
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.type.TypeReference;
import org.fest.reflect.reference.TypeRef;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.fest.reflect.core.Reflection.constructor;
import static org.fest.reflect.core.Reflection.field;

/**
 * @author Olga Melnichuk
 */
public class ExperimentProfileDeserializer10 extends JsonDeserializer<ExperimentProfile> {

    @Override
    public ExperimentProfile deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ExperimentProfile experimentProfile = constructor().in(ExperimentProfile.class).newInstance();

        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = jp.getCurrentName();
            jp.nextToken();

            if ("nextId".equals(fieldname)) {
                field("nextId").ofType(Integer.TYPE).in(experimentProfile).set(jp.getIntValue());
            } else if("type".equals(fieldname)) {
                field("type").ofType(ExperimentProfileType.class).in(experimentProfile).set(ExperimentProfileType.valueOf(jp.getText()));
            } else if ("accession".equals(fieldname)) {
                field("accession").ofType(String.class).in(experimentProfile).set(jp.getText());
            } else if ("title".equals(fieldname)) {
                field("title").ofType(String.class).in(experimentProfile).set(jp.getText());
            } else if ("description".equals(fieldname)) {
                field("description").ofType(String.class).in(experimentProfile).set(jp.getText());
            } else if ("experimentDate".equals(fieldname)) {
                field("experimentDate").ofType(Date.class).in(experimentProfile).set(jp.readValueAs(Date.class));
            } else if ("publicReleaseDate".equals(fieldname)) {
                field("publicReleaseDate").ofType(Date.class).in(experimentProfile).set(jp.readValueAs(Date.class));
            } else if ("arrayDesign".equals(fieldname)) {
                field("arrayDesign").ofType(String.class).in(experimentProfile).set(jp.getText());
            } else if("aeExperimentType".equals(fieldname)) {
                field("aeExperimentType").ofType(String.class).in(experimentProfile).set(jp.getText());
            } else if("experimentalDesigns".equals(fieldname)) {
                field("experimentalDesigns").ofType(new TypeRef<List<OntologyTerm>>() {})
                        .in(experimentProfile)
                        .set(jp.<List<OntologyTerm>>readValueAs(new TypeReference<List<OntologyTerm>>() {}));
            }
        }
        return experimentProfile;
    }
}
