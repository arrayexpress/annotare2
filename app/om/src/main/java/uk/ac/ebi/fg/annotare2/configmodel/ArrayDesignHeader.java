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

package uk.ac.ebi.fg.annotare2.configmodel;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import uk.ac.ebi.fg.annotare2.submissionmodel.DataSerializationException;

import java.io.IOException;
import java.util.Date;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class ArrayDesignHeader {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("version")
    private String version;

    @JsonProperty("publicReleaseDate")
    private Date publicReleaseDate;

    @JsonProperty("organism")
    private OntologyTerm organism;

    @JsonProperty("printingProtocol")
    private PrintingProtocol printingProtocol;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OntologyTerm getOrganism() {
        return organism;
    }

    public void setOrganism(OntologyTerm organism) {
        this.organism = organism;
    }

    public PrintingProtocol getPrintingProtocol() {
        return printingProtocol;
    }

    public void setPrintingProtocol(PrintingProtocol printingProtocol) {
        this.printingProtocol = printingProtocol;
    }

    public Date getPublicReleaseDate() {
        return publicReleaseDate;
    }

    public void setPublicReleaseDate(Date publicReleaseDate) {
        this.publicReleaseDate = publicReleaseDate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public String toJsonString() throws DataSerializationException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonGenerationException e) {
            throw new DataSerializationException(e);
        } catch (JsonMappingException e) {
            throw new DataSerializationException(e);
        } catch (IOException e) {
            throw new DataSerializationException(e);
        }
    }
}
