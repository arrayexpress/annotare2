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
import uk.ac.ebi.fg.annotare2.submission.model.Publication;

import java.io.IOException;

import static uk.ac.ebi.fg.annotare2.submission.transform.PublicationSerializer10.JSON_FIELDS;
import static uk.ac.ebi.fg.annotare2.submission.transform.util.JsonUtilities.parseJson;

/**
 * @author Olga Melnichuk
 */
public class PublicationDeserializer10 extends JsonDeserializer<Publication> {

    @Override
    public Publication deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return parseJson(jp, Publication.class, JSON_FIELDS);
    }
}
