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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;

import java.util.Date;

/**
 * @author Olga Melnichuk
 */
public class ArrayDesignDetailsDto implements IsSerializable {

    private String name;
    private String description;
    private String version;
    private OntologyTerm organism;
    private Date releaseDate;
    private PrintingProtocolDto printingProtocol;


    ArrayDesignDetailsDto() {
      /*used by GWT serialization*/
    }

    public ArrayDesignDetailsDto(String name, String description, String version, OntologyTerm organism, Date releaseDate,
                                 PrintingProtocolDto printingProtocol) {
        this.name = name;
        this.description = description;
        this.version = version;
        this.organism = organism;
        this.releaseDate = releaseDate;
        this.printingProtocol = printingProtocol;
    }

    public String getArrayDesignName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public Date getPublicReleaseDate() {
        return releaseDate;
    }

    public OntologyTerm getOrganism() {
        return organism;
    }

    public PrintingProtocolDto getPrintingProtocol() {
        return printingProtocol;
    }
}
