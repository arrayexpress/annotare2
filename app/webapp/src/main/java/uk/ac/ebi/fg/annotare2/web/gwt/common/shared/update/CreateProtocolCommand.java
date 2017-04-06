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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update;

import uk.ac.ebi.fg.annotare2.submission.model.Protocol;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolDetail;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolType;


import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class CreateProtocolCommand implements ExperimentUpdateCommand {

    private ProtocolType protocolType;

    private List<ProtocolDetail> protocolTypes;

    @SuppressWarnings("unused")
    CreateProtocolCommand() {
        /* used by GWT serialization*/
    }

    public CreateProtocolCommand(ProtocolType protocolType) {
        this.protocolType = protocolType;
    }

    public CreateProtocolCommand(List<ProtocolDetail> protocolDetails) {
        this.protocolTypes = protocolDetails;
    }

    @Override
    public void execute(ExperimentUpdatePerformer performer) {
       // performer.createProtocol(protocolType);
        performer.createProtocol(protocolTypes);
    }

    @Override
    public boolean isCritical() {
        return true;
    }
}