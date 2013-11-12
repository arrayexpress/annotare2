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

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.module.SimpleModule;

/**
 * @author Olga Melnichuk
 */
public class SerializationModule extends SimpleModule {

    private final SerializationFactory factory;

    public SerializationModule(SerializationFactory factory) {
        super(SerializationModule.class.getName(), Version.unknownVersion());
        this.factory = factory;
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(factory.getSerializers());
        context.addDeserializers(factory.getDeserializers());
    }

    public static Module createSubmissionSerializationModule(ModelVersion version) {
        return new SerializationModule(SerializationFactory.createFactory(version));
    }
}
