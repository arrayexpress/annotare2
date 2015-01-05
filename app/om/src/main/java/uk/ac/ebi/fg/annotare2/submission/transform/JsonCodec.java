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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ebi.fg.annotare2.submission.model.ArrayDesignHeader;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.ImportedExperimentProfile;

import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.ac.ebi.fg.annotare2.submission.transform.SerializationModule.createSubmissionSerializationModule;

/**
 * @author Olga Melnichuk
 */
public class JsonCodec {

    private static ObjectMapper createMapper(ModelVersion version) throws DataSerializationException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(createSubmissionSerializationModule(version));
        return mapper;
    }

    public static ExperimentProfile readExperiment(String jsonString, ModelVersion version) throws DataSerializationException {
        if (isNullOrEmpty(jsonString)) {
            return null;
        }
        try {
            return createMapper(version).readValue(jsonString, ExperimentProfile.class);
        } catch (JsonGenerationException e) {
            throw new DataSerializationException(e);
        } catch (JsonMappingException e) {
            throw new DataSerializationException(e);
        } catch (IOException e) {
            throw new DataSerializationException(e);
        }
    }

    public static ImportedExperimentProfile readImportedExperiment(String jsonString, ModelVersion version) throws DataSerializationException {
        if (isNullOrEmpty(jsonString)) {
            return null;
        }
        try {
            return createMapper(version).readValue(jsonString, ImportedExperimentProfile.class);
        } catch (JsonGenerationException e) {
            throw new DataSerializationException(e);
        } catch (JsonMappingException e) {
            throw new DataSerializationException(e);
        } catch (IOException e) {
            throw new DataSerializationException(e);
        }
    }

    public static ArrayDesignHeader readArrayDesign(String jsonString, ModelVersion version) throws DataSerializationException {
        if (isNullOrEmpty(jsonString)) {
            return null;
        }
        try {
            return createMapper(version).readValue(jsonString, ArrayDesignHeader.class);
        } catch (JsonGenerationException e) {
            throw new DataSerializationException(e);
        } catch (JsonMappingException e) {
            throw new DataSerializationException(e);
        } catch (IOException e) {
            throw new DataSerializationException(e);
        }
    }

    public static String writeExperiment(ExperimentProfile exp, ModelVersion version) throws DataSerializationException {
        try {
            return createMapper(version).writeValueAsString(exp);
        } catch (JsonGenerationException e) {
            throw new DataSerializationException(e);
        } catch (JsonMappingException e) {
            throw new DataSerializationException(e);
        } catch (IOException e) {
            throw new DataSerializationException(e);
        }
    }

    public static String writeImportedExperiment(ImportedExperimentProfile exp, ModelVersion version) throws DataSerializationException {
        try {
            return createMapper(version).writeValueAsString(exp);
        } catch (JsonGenerationException e) {
            throw new DataSerializationException(e);
        } catch (JsonMappingException e) {
            throw new DataSerializationException(e);
        } catch (IOException e) {
            throw new DataSerializationException(e);
        }
    }

    public static String writeArrayDesign(ArrayDesignHeader header, ModelVersion version) throws DataSerializationException {
        try {
            return createMapper(version).writeValueAsString(header);
        } catch (JsonGenerationException e) {
            throw new DataSerializationException(e);
        } catch (JsonMappingException e) {
            throw new DataSerializationException(e);
        } catch (IOException e) {
            throw new DataSerializationException(e);
        }
    }
}
