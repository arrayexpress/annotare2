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

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import uk.ac.ebi.fg.annotare2.configmodel.ArrayDesignHeader;
import uk.ac.ebi.fg.annotare2.configmodel.OntologyTerm;
import uk.ac.ebi.fg.annotare2.configmodel.PrintingProtocol;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.PrintingProtocolDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.ArrayDesignDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdatePerformer;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateResult;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ArrayDesignUpdatePerformerImpl implements ArrayDesignUpdatePerformer {

    private final ArrayDesignHeader header;

    private ArrayDesignUpdateResult result = new ArrayDesignUpdateResult();

    public ArrayDesignUpdatePerformerImpl(ArrayDesignHeader header) {
        this.header = header;
    }

    public ArrayDesignUpdateResult run(List<ArrayDesignUpdateCommand> commands) {
        for (ArrayDesignUpdateCommand command : commands) {
            command.execute(this);
        }
        return result;
    }

    @Override
    public void updateDetails(ArrayDesignDetailsDto details) {
        OntologyTerm organism = details.getOrganism();
        PrintingProtocolDto otherProtocol = details.getOtherPrintingProtocol();

        header.setName(details.getArrayDesignName());
        header.setDescription(details.getDescription());
        header.setVersion(details.getVersion());
        header.setPublicReleaseDate(details.getPublicReleaseDate());
        header.setOrganism(organism == null ? null : new OntologyTerm(organism.getAccession(), organism.getLabel()));
        header.setPrintingProtocolId(details.getPrintingProtocolId());
        header.setPrintingProtocolBackup(new PrintingProtocol(otherProtocol.getId(), otherProtocol.getName(), otherProtocol.getDescription()));
        result.setUpdatedDetails(details);
    }
}
