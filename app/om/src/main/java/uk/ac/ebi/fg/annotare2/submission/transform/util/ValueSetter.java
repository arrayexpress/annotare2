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

package uk.ac.ebi.fg.annotare2.submission.transform.util;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;

/**
 * @author Olga Melnichuk
 */
public abstract class ValueSetter<T> {

    private final String fieldName;
    private final JsonValueReader fieldValueParser;

    private ValueSetter(String fieldName, JsonValueReader fieldValueParser) {
        this.fieldName = fieldName;
        this.fieldValueParser = fieldValueParser;
    }

    public ValueSetter(String fieldName, final Class<?> fieldType) {
        this(fieldName, new JsonValueReader() {
            @Override
            public Object readValue(JsonParser jp) throws IOException {
                return jp.readValueAs(fieldType);
            }
        });
    }

    public ValueSetter(String fieldName, final TypeReference<?> fieldTypeRef) {
        this(fieldName, new JsonValueReader() {
            @Override
            public Object readValue(JsonParser jp) throws IOException {
                return jp.readValueAs(fieldTypeRef);
            }
        });
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setValue(T obj, JsonParser jp) throws IOException {
        setValue(obj, fieldValueParser.readValue(jp));
    }

    public abstract void setValue(T obj, Object value);

    private interface JsonValueReader {
        Object readValue(JsonParser jp) throws IOException;
    }
}
