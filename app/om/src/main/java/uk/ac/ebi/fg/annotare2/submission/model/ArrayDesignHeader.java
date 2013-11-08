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

package uk.ac.ebi.fg.annotare2.submission.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Olga Melnichuk
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArrayDesignHeader implements Serializable {

    private static PrintingProtocol DEFAULT = new PrintingProtocol(0, "", "");

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

    @JsonProperty("printingProtocolId")
    private int printingProtocolId;

    @JsonProperty("printingProtocolBackup")
    private PrintingProtocol printingProtocolBackup;

    public ArrayDesignHeader() {
        printingProtocolBackup = DEFAULT;
    }

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

    public int getPrintingProtocolId() {
        return printingProtocolId;
    }

    public void setPrintingProtocolId(int id) {
        this.printingProtocolId = id;
    }

    public PrintingProtocol getPrintingProtocolBackup() {
        return printingProtocolBackup;
    }

    public void setPrintingProtocolBackup(PrintingProtocol protocol) {
        this.printingProtocolBackup = protocol == null ? DEFAULT : protocol;
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
}
