/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.om;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submissionmodel.DataSerializationException;

import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class ExperimentSubmission extends Submission {

    private String experimentString;

    public ExperimentSubmission(User user, Acl acl) {
        super(user, acl);
    }

    public ExperimentProfile getExperimentProfile() throws DataSerializationException {
        return fromJsonString(experimentString);
    }

    public void setExperimentProfile(ExperimentProfile exp) throws DataSerializationException {
        this.experimentString = toJsonString(exp);
    }

    @Override
    public boolean hasNoData() {
        return isNullOrEmpty(experimentString);
    }

    @Override
    public void discardAll() {
        experimentString = null;
    }

    public static ExperimentProfile fromJsonString(String str) throws DataSerializationException {
        if (isNullOrEmpty(str)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            ExperimentProfile exp = mapper.readValue(str, ExperimentProfile.class);
            exp.fixMe();
            return exp;
        } catch (JsonGenerationException e) {
            throw new DataSerializationException(e);
        } catch (JsonMappingException e) {
            throw new DataSerializationException(e);
        } catch (IOException e) {
            throw new DataSerializationException(e);
        }
    }

    public static String toJsonString(ExperimentProfile exp) throws DataSerializationException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(exp);
        } catch (JsonGenerationException e) {
            throw new DataSerializationException(e);
        } catch (JsonMappingException e) {
            throw new DataSerializationException(e);
        } catch (IOException e) {
            throw new DataSerializationException(e);
        }
    }
}
