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

import org.codehaus.jackson.map.Deserializers;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.Serializers;
import org.codehaus.jackson.map.module.SimpleDeserializers;
import org.codehaus.jackson.map.module.SimpleSerializers;

/**
 * @author Olga Melnichuk
 */
public abstract class SerializationFactory {

    private final SimpleSerializers serializers;
    private final SimpleDeserializers deserializers;

    protected SerializationFactory() {
        serializers = new SimpleSerializers();
        deserializers = new SimpleDeserializers();
    }

    protected <T> void register(Class<T> clazz, JsonSerializer<T> serializer, JsonDeserializer<T> deserializer) {
        serializers.addSerializer(clazz, serializer);
        deserializers.addDeserializer(clazz, deserializer);
    }

    public Serializers getSerializers() {
        return serializers;
    }

    public Deserializers getDeserializers() {
        return deserializers;
    }

    public static SerializationFactory createFactory(ModelVersion version) {
        switch (version) {
            case VERSION_1_0:
                return new SerializationFactory10();
            default:
                throw new UnsupportedOperationException("Unsupported model version: " + version);
        }
    }
}
