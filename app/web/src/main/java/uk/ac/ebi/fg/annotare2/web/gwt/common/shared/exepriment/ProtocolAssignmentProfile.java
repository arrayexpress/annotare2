/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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
import uk.ac.ebi.fg.annotare2.submission.model.Protocol;
import uk.ac.ebi.fg.annotare2.submission.model.ProtocolSubjectType;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;

/**
 * @author Olga Melnichuk
 */
public class ProtocolAssignmentProfile implements IsSerializable {

    public static final class Empty extends ProtocolAssignmentProfile {

        @SuppressWarnings("unused")
        Empty() {
            super.target = "unknown";
        /* used by GWT serialization */
        }

        public Empty(ProtocolSubjectType subjectType) {
            super.target = subjectType.getTitle();
        }

        @Override
        public String getProtocolName() {
            return "unknown";
        }

        @Override
        public int getProtocolId() {
            return 0;
        }

        @Override
        public Map<String, Boolean> getAssignments() {
            return emptyMap();
        }

        @Override
        public Map<String, String> getNames() {
            return emptyMap();
        }
    }

    private int protocolId;
    private String protocolName;
    private String target;

    private Map<String, String> names;
    private Map<String, Boolean> assignments;

    private boolean isDefault;

    ProtocolAssignmentProfile() {
        /* used by GWT serialization */
    }

    public ProtocolAssignmentProfile(Protocol protocol, Map<ProtocolAssignment.Item, Boolean> protocolAssignments) {
        protocolId = protocol.getId();
        protocolName = protocol.getName();
        target = protocol.getSubjectType().getTitle();
        names = new LinkedHashMap<String, String>();
        assignments = new LinkedHashMap<String, Boolean>();
        isDefault = !protocolAssignments.values().contains(true);
        for (ProtocolAssignment.Item item : protocolAssignments.keySet()) {
            names.put(item.getId(), item.getName());
            assignments.put(item.getId(), isDefault || protocolAssignments.get(item));
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

    public String getProtocolSubjectType() {
        return target;
    }

    public boolean isDefault() {
        return isDefault;
    }
}
