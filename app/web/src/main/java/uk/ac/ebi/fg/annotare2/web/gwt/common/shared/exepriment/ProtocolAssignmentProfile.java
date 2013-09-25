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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.configmodel.AssignmentItem;
import uk.ac.ebi.fg.annotare2.configmodel.Protocol;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class ProtocolAssignmentProfile implements IsSerializable {

    private int protocolId;

    private String protocolName;

    private Map<String, String> names;
    private Map<String, Boolean> assignments;

    ProtocolAssignmentProfile() {
        /* used by GWT serialization */
    }

    public ProtocolAssignmentProfile(Protocol protocol, Map<AssignmentItem, Boolean> protocolAssignments) {
        protocolId = protocol.getId();
        protocolName = protocol.getName();
        names = new LinkedHashMap<String, String>();
        assignments = new LinkedHashMap<String, Boolean>();
        for (AssignmentItem item : protocolAssignments.keySet()) {
            names.put(item.getId(), item.getName());
            assignments.put(item.getId(), protocolAssignments.get(item));
        }
    }

    public String getProtocolName() {
        return protocolName;
    }

    public int getProtocolId() {
        return protocolId;
    }

    public Map<String, Boolean> getAssignments() {
        return assignments;
    }

    public Map<String, String> getNames() {
        return names;
    }
}
