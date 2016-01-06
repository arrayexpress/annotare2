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
import uk.ac.ebi.fg.annotare2.submission.model.FileColumn;
import uk.ac.ebi.fg.annotare2.submission.model.FileRef;
import uk.ac.ebi.fg.annotare2.submission.transform.util.ValueGetter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static uk.ac.ebi.fg.annotare2.submission.transform.util.JsonUtilities.generateJson;

/**
 * @author Olga Melnichuk
 */
class FileColumnSerializer10 extends JsonSerializer<FileColumn> {

    static final List<String> FILE_COLUMN_JSON_FIELDS = asList(
            "type",
            "leId2FileNameMap"
    );

    @Override
    public void serialize(FileColumn fileColumn, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        generateJson(jgen, fileColumn, FILE_COLUMN_JSON_FIELDS,
                new ValueGetter<FileColumn>("leId2FileNameMap") {
                    @Override
                    public Object getValue(FileColumn obj) {
                        Map<String, String> map = new HashMap<String, String>();
                        for (String labeledExtractId : obj.getLabeledExtractIds()) {
                            FileRef file = obj.getFileRef(labeledExtractId);
                            map.put(labeledExtractId, file.getName());
                        }
                        return map;
                    }
                });
    }
}
