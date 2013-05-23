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

import uk.ac.ebi.fg.annotare2.configmodel.ArrayDesignHeader;
import uk.ac.ebi.fg.annotare2.submissionmodel.DataSerializationException;

import java.io.InputStream;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class ArrayDesignSubmission extends Submission {

    @Deprecated
    private String header;

    private String body;

    private String headerString;

    public ArrayDesignSubmission(User createdBy, Acl acl) {
        super(createdBy, acl);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public InputStream getBody() {
        return asStream(body);
    }

    @Deprecated
    public void setHeader(String header) {
        this.header = header;
    }

    @Deprecated
    public InputStream getHeader() {
        return asStream(header);
    }


    public ArrayDesignHeader getArrayDesignHeader() throws DataSerializationException {
        return ArrayDesignHeader.fromJsonString(headerString);
    }

    public void setArrayDesignHeader(ArrayDesignHeader adHeader) throws DataSerializationException {
        this.headerString = adHeader.toJsonString();
    }

    @Override
    public boolean hasNoData() {
        return isNullOrEmpty(header) || isNullOrEmpty(body);
    }

    @Override
    public void discardAll() {
        throw new UnsupportedOperationException("Can't discard data for ArrayDesign");
    }
}
