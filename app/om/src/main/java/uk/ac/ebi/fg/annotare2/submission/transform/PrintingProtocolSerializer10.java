/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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
import uk.ac.ebi.fg.annotare2.submission.model.PrintingProtocol;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static uk.ac.ebi.fg.annotare2.submission.transform.util.JsonUtilities.generateJson;


/**
 * @author Olga Melnichuk
 */
class PrintingProtocolSerializer10 extends JsonSerializer<PrintingProtocol> {

    static final List<String> PRINTING_PROTOCOL_JSON_FIELDS = asList(
            "id",
            "name",
            "description");

    @Override
    public void serialize(PrintingProtocol printingProtocol, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        generateJson(jgen, printingProtocol, PRINTING_PROTOCOL_JSON_FIELDS);
    }
}
