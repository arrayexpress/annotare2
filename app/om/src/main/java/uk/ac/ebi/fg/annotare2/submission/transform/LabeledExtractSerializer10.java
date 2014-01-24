/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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
import uk.ac.ebi.fg.annotare2.submission.model.LabeledExtract;
import uk.ac.ebi.fg.annotare2.submission.transform.util.ValueGetter;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static uk.ac.ebi.fg.annotare2.submission.transform.util.JsonUtilities.generateJson;

/**
 * @author Olga Melnichuk
 */
class LabeledExtractSerializer10 extends JsonSerializer<LabeledExtract> {

    static final List<String> LABELED_EXTRACT_JSON_FIELDS = asList(
            "extractId",
            "labelId",
            "id");

    @Override
    public void serialize(LabeledExtract assay, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        generateJson(jgen, assay, LABELED_EXTRACT_JSON_FIELDS,
                new ValueGetter<LabeledExtract>("extractId") {
                    @Override
                    public Object getValue(LabeledExtract obj) {
                        return obj.getExtract().getId();
                    }
                },
                new ValueGetter<LabeledExtract>("labelId") {
                    @Override
                    public Object getValue(LabeledExtract obj) {
                        return obj.getLabel() == null ? null : obj.getLabel().getId();
                    }
                });
    }
}
