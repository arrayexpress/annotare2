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

package uk.ac.ebi.fg.annotare2.submission.transform.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static uk.ac.ebi.fg.annotare2.submission.transform.util.ClassUtilities.*;

/**
 * @author Olga Melnichuk
 */
public class JsonUtilities {

    public static <T> T parseJson(JsonParser jp, Class<T> targetClass, Collection<String> fieldNames, ValueSetter<T>... customSetters) throws IOException {
        Map<String, ValueSetter<T>> setters = new HashMap<String, ValueSetter<T>>();
        for (ValueSetter<T> setter : customSetters) {
            setters.put(setter.getFieldName(), setter);
        }

        T target = newInstance(targetClass);
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jp.getCurrentName();
            jp.nextToken();

            if (fieldNames.contains(fieldName)) {
                ValueSetter<T> setter = setters.get(fieldName);
                if (setter == null) {

                    Class<?> type = getFieldType(target, fieldName);
                    final Type genericType = getGenericFieldType(target, fieldName);

                    if (genericType instanceof ParameterizedType) {
                        Object v = jp.readValueAs(new TypeReference<Object>() {
                            @Override
                            public Type getType() {
                                return genericType;
                            }
                        });
                        setFieldValue(target, fieldName, v);
                    } else {
                        setFieldValue(target, fieldName, jp.readValueAs(type));
                    }
                } else {
                    setter.setValue(target, jp);
                }
            }
        }
        return target;
    }

    public static <T> void generateJson(JsonGenerator jgen, T target, Collection<String> fieldNames, ValueGetter<T>... customGetters) throws IOException {
        Map<String, ValueGetter<T>> getters = new HashMap<String, ValueGetter<T>>();
        for (ValueGetter<T> getter : customGetters) {
            getters.put(getter.getFieldName(), getter);
        }

        jgen.writeStartObject();
        for (String fieldName : fieldNames) {
            ValueGetter<T> getter = getters.get(fieldName);
            Object value = (getter != null) ? getter.getValue(target) : getFieldValue(target, fieldName);
            generateField(jgen, fieldName, value);
        }
        jgen.writeEndObject();
    }

    private static void generateField(JsonGenerator jgen, String fieldName, Object value) throws IOException {
        if (value != null) {
            jgen.writeObjectField(fieldName, value);
        }
    }
}
