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

import uk.ac.ebi.fg.annotare2.submission.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Olga Melnichuk
 */
public abstract class ProtocolAssignment {

    public static class SampleProtocolAssignment extends ProtocolAssignment {
        private SampleProtocolAssignment(ExperimentProfile exp, Protocol protocol) {
            super(exp, protocol);
        }

        @Override
        public ProtocolAssignmentProfile getProfile(ExperimentProfile exp, Protocol protocol){
            Set<Sample> assigned = exp.getSamples(protocol);
            Map<Item, Boolean> assignments = new HashMap<Item, Boolean>();
            for(Sample sample : exp.getSamples()) {
                assignments.put(new Item(sample.getId(), sample.getName()), assigned.contains(sample));
            }
            return new ProtocolAssignmentProfile(protocol, assignments);
        }
    }

    public static class ExtractProtocolAssignment extends ProtocolAssignment {
        private ExtractProtocolAssignment(ExperimentProfile exp, Protocol protocol) {
            super(exp, protocol);
        }

        @Override
        protected ProtocolAssignmentProfile getProfile(ExperimentProfile exp, Protocol protocol) {
            Set<Extract> assigned = exp.getExtracts(protocol);
            Map<Item, Boolean> assignments = new HashMap<Item, Boolean>();
            for(Extract extract : exp.getExtracts()) {
                assignments.put(new Item(extract.getId(), extract.getName()), assigned.contains(extract));
            }
            return new ProtocolAssignmentProfile(protocol, assignments);
        }
    }

    public static class LabeledExtractProtocolAssignment extends ProtocolAssignment {
        private LabeledExtractProtocolAssignment(ExperimentProfile exp, Protocol protocol) {
            super(exp, protocol);
        }

        @Override
        protected ProtocolAssignmentProfile getProfile(ExperimentProfile exp, Protocol protocol) {
            Set<LabeledExtract> assigned = exp.getLabeledExtracts(protocol);
            Map<Item, Boolean> assignments = new HashMap<Item, Boolean>();
            for(LabeledExtract labeledExtract : exp.getLabeledExtracts()) {
                assignments.put(new Item(labeledExtract.getId(), labeledExtract.getName()), assigned.contains(labeledExtract));
            }
            return new ProtocolAssignmentProfile(protocol, assignments);
        }
    }

    public static class Item {
        private String id;
        private String name;

        public Item(int id, String name) {
            this(Integer.toString(id), name);
        }

        public Item(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item that = (Item) o;

            if (!id.equals(that.id)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    private final ExperimentProfile exp;
    private final Protocol protocol;

    protected ProtocolAssignment(ExperimentProfile exp, Protocol protocol) {
        this.exp = exp;
        this.protocol = protocol;
    }

    protected abstract ProtocolAssignmentProfile getProfile(ExperimentProfile exp, Protocol protocol);

    public ProtocolAssignmentProfile getProfile() {
        return getProfile(exp, protocol);
    }

    public static ProtocolAssignment createProtocolAssignment(ExperimentProfile exp, Protocol protocol) {
        ProtocolSubjectType subjectType = protocol.getSubjectType();
        switch(subjectType) {
            case SAMPLE:
                return new SampleProtocolAssignment(exp, protocol);
            case EXTRACT:
                return new ExtractProtocolAssignment(exp, protocol);
            case LABELED_EXTRACT:
                return new LabeledExtractProtocolAssignment(exp, protocol);
            default:
               return new ProtocolAssignment(exp, protocol) {
                   @Override
                   protected ProtocolAssignmentProfile getProfile(ExperimentProfile exp, Protocol protocol) {
                       return ProtocolAssignmentProfile.EMPTY;
                   }
               };
        }
    }
}
