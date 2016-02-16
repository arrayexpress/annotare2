/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.db.model;

import uk.ac.ebi.fg.annotare2.submission.model.ArrayDesignHeader;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.InputStream;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.ac.ebi.fg.annotare2.submission.transform.JsonCodec.readArrayDesign;
import static uk.ac.ebi.fg.annotare2.submission.transform.JsonCodec.writeArrayDesign;

/**
 * @author Olga Melnichuk
 */
@Entity
@DiscriminatorValue("ARRAY_DESIGN")
public class ArrayDesignSubmission extends Submission {

    @Column(name = "arrayDesignHeader")
    private String header;

    @Column(name = "arrayDesignBody")
    private String body;

    public ArrayDesignSubmission() {
        this(null);
    }

    public ArrayDesignSubmission(User createdBy) {
        super(createdBy);
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public InputStream getBody() {
        return asStream(body);
    }

    public ArrayDesignHeader getHeader() throws DataSerializationException {
        ArrayDesignHeader adHeader = readArrayDesign(header, getVersion());
        return adHeader == null ? new ArrayDesignHeader() : adHeader;
    }

    public void setHeader(ArrayDesignHeader header) throws DataSerializationException {
        this.header = writeArrayDesign(header, getVersion());
    }

    @Override
    public boolean hasNoData() {
        return isNullOrEmpty(header) || isNullOrEmpty(body);
    }
}
