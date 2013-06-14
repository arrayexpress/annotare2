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

package uk.ac.ebi.fg.annotare2.om;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import uk.ac.ebi.fg.annotare2.configmodel.ArrayDesignHeader;
import uk.ac.ebi.fg.annotare2.submissionmodel.DataSerializationException;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class ArrayDesignSubmission extends Submission {

    private String header;

    private String body;

    public ArrayDesignSubmission(User createdBy, Acl acl) {
        super(createdBy, acl);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public InputStream getBody() {
        return asStream(body);
    }

    public ArrayDesignHeader getHeader() throws DataSerializationException {
        return fromJsonString(header);
    }

    public void setHeader(ArrayDesignHeader header) throws DataSerializationException {
        this.header = toJsonString(header);
    }

    @Override
    public boolean hasNoData() {
        return isNullOrEmpty(header) || isNullOrEmpty(body);
    }

    @Override
    public void discardAll() {
        throw new UnsupportedOperationException("Can't discard data for ArrayDesign");
    }

    public static ArrayDesignHeader fromJsonString(String str) throws DataSerializationException {
        if (isNullOrEmpty(str)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(str, ArrayDesignHeader.class);
        } catch (JsonGenerationException e) {
            throw new DataSerializationException(e);
        } catch (JsonMappingException e) {
            throw new DataSerializationException(e);
        } catch (IOException e) {
            throw new DataSerializationException(e);
        }
    }

    public static String toJsonString(ArrayDesignHeader header) throws DataSerializationException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(header);
        } catch (JsonGenerationException e) {
            throw new DataSerializationException(e);
        } catch (JsonMappingException e) {
            throw new DataSerializationException(e);
        } catch (IOException e) {
            throw new DataSerializationException(e);
        }
    }
}
