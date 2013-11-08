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

import com.google.common.annotations.GwtCompatible;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public enum ProtocolTargetType {
    EXTRACTS("extracts") {
        @Override
        Collection<? extends HasProtocolAssignment> getAssignmentItems(ExperimentProfile exp) {
            return exp.getExtracts();
        }
    },
    LABELED_EXTRACTS("labeled extracts") {
        @Override
        Collection<? extends HasProtocolAssignment> getAssignmentItems(ExperimentProfile exp) {
            return exp.getLabeledExtracts();
        }
    },
    ASSAYS("assays") {
        @Override
        Collection<? extends HasProtocolAssignment> getAssignmentItems(ExperimentProfile exp) {
            return exp.getAssays();
        }
    },
    RAW_FILES("raw files") {
        @Override
        Collection<? extends HasProtocolAssignment> getAssignmentItems(ExperimentProfile exp) {
            return exp.getRawFileRefs();
        }
    },
    PROCESSED_AND_MATRIX_FILES("processed files") {
        @Override
        Collection<? extends HasProtocolAssignment> getAssignmentItems(ExperimentProfile exp) {
            return exp.getProcessedFileRefs();
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

    abstract Collection<? extends HasProtocolAssignment> getAssignmentItems(ExperimentProfile exp);

    public Map<AssignmentItem, Boolean> getProtocolAssignments(Protocol protocol, ExperimentProfile exp) {
        Map<AssignmentItem, Boolean> assigned = new LinkedHashMap<AssignmentItem, Boolean>();
        for (HasProtocolAssignment item : getAssignmentItems(exp)) {
            assigned.put(item.getProtocolAssignmentItem(), item.hasProtocol(protocol) || protocol.isAssigned2All());
        }
        return assigned;
    }

    public void setProtocolAssignments(Protocol protocol, ExperimentProfile exp, Set<String> assignments) {
        Collection<? extends HasProtocolAssignment> items = getAssignmentItems(exp);
        boolean assign2All = assignments.size() == items.size();
        if (assign2All) {
            assign2All(protocol, items);
            return;
        }
        for (HasProtocolAssignment item : items) {
            item.assignProtocol(protocol, assignments.contains(item.getProtocolAssignmentItem().getId()));
        }
        protocol.setAssigned2All(false);
    }

    public void removeProtocolAssignments(Protocol protocol, ExperimentProfile exp) {
        assign2All(protocol, getAssignmentItems(exp));
    }

    private void assign2All(Protocol protocol, Collection<? extends HasProtocolAssignment> items) {
        for (HasProtocolAssignment item : items) {
            item.assignProtocol(protocol, false);
        }
        protocol.setAssigned2All(true);
    }
}
