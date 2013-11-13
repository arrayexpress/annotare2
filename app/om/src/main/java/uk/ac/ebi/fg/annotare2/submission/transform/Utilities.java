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
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Olga Melnichuk
 */
class Utilities {

    public static <T> T parseJson(JsonParser jp, Class<T> targetClass, Collection<String> fieldNames) throws IOException {
        Set<String> fieldNameSet = new HashSet<String>(fieldNames);

        T target = newInstance(targetClass);
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jp.getCurrentName();
            jp.nextToken();

            if (fieldNameSet.contains(fieldName)) {
                Class<?> fieldType = getFieldType(target, fieldName);
                setFieldValue(target, fieldName, jp.readValueAs(fieldType));
            }
        }
        return target;
    }

    public static void generateJson(JsonGenerator jgen, Object target, Collection<String> fieldNames) throws IOException {
        jgen.writeStartObject();
        for (String fieldName : fieldNames) {
            generateField(jgen, fieldName, target);
        }
        jgen.writeEndObject();
    }

    private static void generateField(JsonGenerator jgen, String fieldName, Object target) throws IOException {
        Object value = getFieldValue(target, fieldName);
        if (value != null) {
            jgen.writeObjectField(fieldName, value);
        }
    }

    private static Object getFieldValue(Object target, String fieldName) {
        try {
            return getField(target, fieldName).get(target);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setFieldValue(Object target, String fieldName, Object value) {
        try {
            getField(target, fieldName).set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> getFieldType(Object target, String fieldName) {
        try {
            return getField(target, fieldName).getType();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static Field getField(Object target, String fieldName) throws NoSuchFieldException {
        Class<?> clazz = target.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    private static <T> T newInstance(Class<T> targetClass) {
        try {
            Constructor<T> constructor = targetClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}

