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
import org.codehaus.jackson.map.module.SimpleDeserializers;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.module.SimpleSerializers;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;

/**
 * @author Olga Melnichuk
 */
public class SerializationModule extends SimpleModule {
    private final ModelVersion version;

    public SerializationModule(ModelVersion version) {
        super(SerializationModule.class.getName(), Version.unknownVersion());
        this.version = version;
    }

    @Override
    public void setupModule(SetupContext context) {
        final SimpleSerializers serializers = new SimpleSerializers();
        final SimpleDeserializers deserializers = new SimpleDeserializers();
        switch (version) {
            case VERSION_1_0:
                serializers.addSerializer(ExperimentProfile.class, new ExperimentProfileSerializer10());
                deserializers.addDeserializer(ExperimentProfile.class, new ExperimentProfileDeserializer10());
                serializers.addSerializer(OntologyTerm.class, new OntologyTermSerializer10());
                deserializers.addDeserializer(OntologyTerm.class, new OntologyTermDeserializer10());
                break;
            default:
                throw new IllegalStateException();
        }
        context.addSerializers(serializers);
        context.addDeserializers(deserializers);
    }

    public static Module createSubmissionSerializationModule(ModelVersion version) {
        return new SerializationModule(version);
    }
}
