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

import com.google.common.annotations.GwtCompatible;

import java.util.*;

import static java.util.Collections.EMPTY_MAP;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public enum ProtocolTargetType {
    EXTRACTS("extracts") {
        @Override
        public Map<AssignmentItem, Boolean> getProtocolAssignments(Protocol protocol, ExperimentProfile exp) {
            return getAssignments(protocol, exp.getExtracts());
        }

        @Override
        public void setProtocolAssignments(Protocol protocol, ExperimentProfile exp, Set<String> assignments) {
            setAssignments(protocol, assignments, exp.getExtracts());
        }
    },
    LABELED_EXTRACTS("labeled extracts") {
        @Override
        public Map<AssignmentItem, Boolean> getProtocolAssignments(Protocol protocol, ExperimentProfile exp) {
            return getAssignments(protocol, exp.getLabeledExtracts());
        }

        @Override
        public void setProtocolAssignments(Protocol protocol, ExperimentProfile exp, Set<String> assignments) {
            setAssignments(protocol, assignments, exp.getLabeledExtracts());
        }
    },
    ASSAYS("assays") {
        @Override
        public Map<AssignmentItem, Boolean> getProtocolAssignments(Protocol protocol, ExperimentProfile exp) {
            return getAssignments(protocol, exp.getAssays());
        }

        @Override
        public void setProtocolAssignments(Protocol protocol, ExperimentProfile exp, Set<String> assignments) {
            setAssignments(protocol, assignments, exp.getAssays());
        }
    },
    RAW_FILES("raw files") {
        @Override
        public Map<AssignmentItem, Boolean> getProtocolAssignments(Protocol protocol, ExperimentProfile exp) {
            //TODO
            return EMPTY_MAP;
        }

        @Override
        public void setProtocolAssignments(Protocol protocol, ExperimentProfile exp, Set<String> assignments) {
            //TODO
        }
    },
    PROCESSED_AND_MATRIX_FILES("processed and matrix files") {
        @Override
        public Map<AssignmentItem, Boolean> getProtocolAssignments(Protocol protocol, ExperimentProfile exp) {
            //TODO
            return EMPTY_MAP;
        }

        @Override
        public void setProtocolAssignments(Protocol protocol, ExperimentProfile exp, Set<String> assignments) {
            //TODO
        }
    };

    private final String title;

    ProtocolTargetType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Collection<Protocol> filter(Collection<Protocol> protocols) {
        List<Protocol> filtered = new ArrayList<Protocol>();
        for (Protocol protocol : protocols) {
            if (protocol.getTargetType() == this) {
                filtered.add(protocol);
            }
        }
        return filtered;
    }

    private static Map<AssignmentItem, Boolean> getAssignments(Protocol protocol, Collection<? extends HasProtocolAssignment> items) {
        Map<AssignmentItem, Boolean> assigned = new LinkedHashMap<AssignmentItem, Boolean>();
        for (HasProtocolAssignment item : items) {
            assigned.put(item.getProtocolAssignmentItem(), item.hasProtocol(protocol) || protocol.isAssigned2All());
        }
        return assigned;
    }

    private static void setAssignments(Protocol protocol, Set<String> assignments, Collection<? extends HasProtocolAssignment> items) {
        boolean assign2All = assignments.isEmpty();
        for (HasProtocolAssignment item : items) {
            item.assignProtocol(protocol, !assign2All && assignments.contains(item.getProtocolAssignmentItem().getId()));
        }
        protocol.setAssigned2All(assign2All);
    }

    public abstract Map<AssignmentItem, Boolean> getProtocolAssignments(Protocol protocol, ExperimentProfile exp);

    public abstract void setProtocolAssignments(Protocol protocol, ExperimentProfile exp, Set<String> assignments);
}
