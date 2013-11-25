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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import uk.ac.ebi.fg.annotare2.submission.model.Extract;

import java.io.IOException;

import static uk.ac.ebi.fg.annotare2.submission.transform.util.JsonUtilities.parseJson;

/**
 * @author Olga Melnichuk
 */
class ExtractDeserializer10 extends JsonDeserializer<Extract> {

    @Override
    public Extract deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return parseJson(jp, Extract.class, ExtractSerializer10.EXTRACT_JSON_FIELDS);
    }
}
