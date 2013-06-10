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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.PrintingProtocolDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoTermDto;

import java.util.Date;

/**
 * @author Olga Melnichuk
 */
public class ArrayDesignDetailsDto implements IsSerializable {

    private String name;
    private String description;
    private String version;
    private EfoTermDto organism;
    private Date releaseDate;
    private int printingProtocolId;
    private PrintingProtocolDto otherPrintingProtocol;


    ArrayDesignDetailsDto() {
      /*used by GWT serialization*/
    }

    public ArrayDesignDetailsDto(String name, String description, String version, EfoTermDto organism, Date releaseDate,
                                 int printingProtocolId, PrintingProtocolDto otherPrintingProtocol) {
        this.name = name;
        this.description = description;
        this.version = version;
        this.organism = organism;
        this.releaseDate = releaseDate;
        this.printingProtocolId = printingProtocolId;
        this.otherPrintingProtocol = otherPrintingProtocol;
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

    public EfoTermDto getOrganism() {
        return organism;
    }

    public int getPrintingProtocolId() {
        return printingProtocolId;
    }

    public PrintingProtocolDto getOtherPrintingProtocol() {
        return otherPrintingProtocol;
    }
}
