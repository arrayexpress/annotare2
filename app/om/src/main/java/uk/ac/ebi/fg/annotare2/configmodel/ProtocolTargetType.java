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

import static java.util.Collections.EMPTY_SET;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public enum ProtocolTargetType {
    EXTRACTS {
        @Override
        public Set<String> getAssignments(Protocol protocol, ExperimentProfile exp) {
            Set<String> assigned = new HashSet<String>();
            for (Extract extract : exp.getExtracts()) {
                String id = Integer.toString(extract.getId());
                if (extract.hasProtocol(protocol)) {
                    assigned.add(id);
                }
            }
            return assigned;
        }

        @Override
        public void setAssignments(Protocol protocol, ExperimentProfile exp, Set<String> assignments) {
            boolean assign2All = assignments.isEmpty();
            for (Extract extract : exp.getExtracts()) {
                extract.assign(protocol, !assign2All);
            }
            protocol.setAssign2All(assign2All);
        }
    },
    LABELED_EXTRACTS {
        @Override
        public Set<String> getAssignments(Protocol protocol, ExperimentProfile exp) {
            //TODO
            return EMPTY_SET;
        }

        @Override
        public void setAssignments(Protocol protocol, ExperimentProfile exp, Set<String> assignments) {
            //TODO
        }
    },
    ASSAYS {
        @Override
        public Set<String> getAssignments(Protocol protocol, ExperimentProfile exp) {
            //TODO
            return EMPTY_SET;
        }

        @Override
        public void setAssignments(Protocol protocol, ExperimentProfile exp, Set<String> assignments) {
            //TODO
        }
    },
    RAW_FILES {
        @Override
        public Set<String> getAssignments(Protocol protocol, ExperimentProfile exp) {
            //TODO
            return EMPTY_SET;
        }

        @Override
        public void setAssignments(Protocol protocol, ExperimentProfile exp, Set<String> assignments) {
            //TODO
        }
    },
    PROCESSED_AND_MATRIX_FILES {
        @Override
        public Set<String> getAssignments(Protocol protocol, ExperimentProfile exp) {
            //TODO
            return EMPTY_SET;
        }

        @Override
        public void setAssignments(Protocol protocol, ExperimentProfile exp, Set<String> assignments) {
            //TODO
        }
    };

    public Collection<Protocol> filter(Collection<Protocol> protocols) {
        List<Protocol> filtered = new ArrayList<Protocol>();
        for (Protocol protocol : protocols) {
            if (protocol.getUsage() == this) {
                filtered.add(protocol);
            }
        }
        return filtered;
    }

    public abstract Set<String> getAssignments(Protocol protocol, ExperimentProfile exp);

    public abstract void setAssignments(Protocol protocol, ExperimentProfile exp, Set<String> assignments);
}
