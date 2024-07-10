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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import uk.ac.ebi.fg.annotare2.submission.model.*;

import java.util.*;

import static java.lang.Integer.parseInt;

/**
 * @author Olga Melnichuk
 */
public abstract class ProtocolAssignment {

    public static class SampleProtocolAssignment extends ProtocolAssignment {
        private SampleProtocolAssignment(ExperimentProfile exp, Protocol protocol) {
            super(exp, protocol);
        }

        @Override
        public ProtocolAssignmentProfile getProfile(ExperimentProfile exp, Protocol protocol) {
            Set<Sample> assignees = exp.getSamples(protocol);
            Map<Item, Boolean> assignments = new HashMap<Item, Boolean>();
            for (Sample sample : exp.getSamples()) {
                assignments.put(new Item(sample.getId(), sample.getName()), assignees.contains(sample));
            }
            return new ProtocolAssignmentProfile(protocol, assignments);
        }

        @Override
        protected void update(ExperimentProfile exp, Protocol protocol, Set<String> assignments) {
            List<Sample> samples = new ArrayList<Sample>();
            for (String id : assignments) {
                Sample sample = exp.getSample(parseInt(id));
                if (sample != null) {
                    samples.add(sample);
                }
            }
            exp.assignProtocol2Samples(protocol, samples);
        }
    }

    public static class ExtractProtocolAssignment extends ProtocolAssignment {
        private ExtractProtocolAssignment(ExperimentProfile exp, Protocol protocol) {
            super(exp, protocol);
        }

        @Override
        protected ProtocolAssignmentProfile getProfile(ExperimentProfile exp, Protocol protocol) {
            Set<Extract> assignees = exp.getExtracts(protocol);
            Map<Item, Boolean> assignments = new HashMap<Item, Boolean>();
            for (Extract extract : exp.getExtracts()) {
                assignments.put(new Item(extract.getId(), extract.getName()), assignees.contains(extract));
            }
            return new ProtocolAssignmentProfile(protocol, assignments);
        }

        @Override
        protected void update(ExperimentProfile exp, Protocol protocol, Set<String> assignments) {
            List<Extract> extracts = new ArrayList<Extract>();
            for (String id : assignments) {
                Extract extract = exp.getExtract(parseInt(id));
                if (extract != null) {
                    extracts.add(extract);
                }
            }
            exp.assignProtocol2Extracts(protocol, extracts);
        }
    }

    public static class LabeledExtractProtocolAssignment extends ProtocolAssignment {
        private LabeledExtractProtocolAssignment(ExperimentProfile exp, Protocol protocol) {
            super(exp, protocol);
        }

        @Override
        protected ProtocolAssignmentProfile getProfile(ExperimentProfile exp, Protocol protocol) {
            Set<LabeledExtract> assignees = exp.getLabeledExtracts(protocol);
            Map<Item, Boolean> assignments = new HashMap<Item, Boolean>();
            for (LabeledExtract labeledExtract : exp.getLabeledExtracts()) {
                assignments.put(new Item(labeledExtract.getId(), labeledExtract.getName()), assignees.contains(labeledExtract));
            }
            return new ProtocolAssignmentProfile(protocol, assignments);
        }

        @Override
        protected void update(ExperimentProfile exp, Protocol protocol, Set<String> assignments) {
            List<LabeledExtract> labeledExtracts = new ArrayList<LabeledExtract>();
            for (String id : assignments) {
                LabeledExtract labeledExtract = exp.getLabeledExtract(id);
                if (labeledExtract != null) {
                    labeledExtracts.add(labeledExtract);
                }
            }
            exp.assignProtocol2LabeledExtracts(protocol, labeledExtracts);
        }
    }

    public static class BaseFileProtocolAssignment extends ProtocolAssignment {
        private BaseFileProtocolAssignment(ExperimentProfile exp, Protocol protocol) {
            super(exp, protocol);
        }

        @Override
        protected ProtocolAssignmentProfile getProfile(ExperimentProfile exp, Protocol protocol) {
            Set<FileRef> assignees = exp.getFileRefs(protocol);
            Map<Item, Boolean> assignments = new HashMap<Item, Boolean>();
            for (FileColumn col : exp.getFileColumns()) {
                if (isAllowed(col.getType())) {
                    for (FileRef fileRef : col.getFileRefs()) {
                        assignments.put(new Item(fileRef.asString(), fileRef.getName()), assignees.contains(fileRef));
                    }
                }
            }
            return new ProtocolAssignmentProfile(protocol, assignments);
        }

        @Override
        protected void update(ExperimentProfile exp, Protocol protocol, Set<String> assignments) {
            List<FileRef> fileRefs = new ArrayList<FileRef>();
            for (String refString : assignments) {
                fileRefs.add(FileRef.fromString(refString));
            }
            exp.assignProtocol2FileRefs(protocol, fileRefs);
        }

        protected boolean isAllowed(FileType type) {
            return true;
        }
    }

    public static class RawFileProtocolAssignment extends BaseFileProtocolAssignment {
        private RawFileProtocolAssignment(ExperimentProfile exp, Protocol protocol) {
            super(exp, protocol);
        }

        @Override
        protected ProtocolAssignmentProfile getProfile(ExperimentProfile exp, Protocol protocol) {
            Collection<FileRef> assignees = exp.getFileRefs(protocol);
            Map<Item, Boolean> assignments = new HashMap<Item, Boolean>();
            FileType firstRawType = null;
            for (FileColumn col : exp.getFileColumns()) {
                if (null == firstRawType && col.getType().isRaw()) {
                    firstRawType = col.getType();
                }
                if (col.getType() == firstRawType) {
                    for (FileRef fileRef : col.getFileRefs()) {
                        assignments.put(new Item(fileRef.asString(), fileRef.getName()), assignees.contains(fileRef));
                    }
                }
            }
            return new ProtocolAssignmentProfile(protocol, assignments);
        }
    }

    public static class ProcessedFileProtocolAssignment extends BaseFileProtocolAssignment {
        private ProcessedFileProtocolAssignment(ExperimentProfile exp, Protocol protocol) {
            super(exp, protocol);
        }

        @Override
        protected boolean isAllowed(FileType type) {
            return type.isProcessed() || type.isProcessedMatrix();
        }
    }

    public static class RawOrProcessedFileProtocolAssignment extends BaseFileProtocolAssignment {
        private RawOrProcessedFileProtocolAssignment(ExperimentProfile exp, Protocol protocol) {
            super(exp, protocol);
        }

        @Override
        protected ProtocolAssignmentProfile getProfile(ExperimentProfile exp, Protocol protocol) {
            Collection<FileRef> assignees = exp.getFileRefs(protocol);
            Map<Item, Boolean> assignments = new HashMap<Item, Boolean>();
            boolean isRawOnly = false;
            for (FileColumn col : exp.getFileColumns()) {
                if (!isRawOnly & col.getType().isRaw()) {
                    isRawOnly = true;
                }
                if (!isRawOnly || col.getType().isRaw()) {
                    for (FileRef fileRef : col.getFileRefs()) {
                        assignments.put(new Item(fileRef.asString(), fileRef.getName()), assignees.contains(fileRef));
                    }
                }
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

            return id.equals(that.id);
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

    protected abstract void update(ExperimentProfile exp, Protocol protocol, Set<String> assignments);

    public ProtocolAssignmentProfile getProfile() {
        return getProfile(exp, protocol);
    }

    public void update(Set<String> assignments) {
        update(exp, protocol, assignments);
    }

    public static ProtocolAssignment createProtocolAssignment(ExperimentProfile exp, Protocol protocol) {
        final ProtocolSubjectType subjectType = protocol.getSubjectType();
        switch (subjectType) {
            case SAMPLE:
                return new SampleProtocolAssignment(exp, protocol);
            case EXTRACT:
                return new ExtractProtocolAssignment(exp, protocol);
            case LABELED_EXTRACT:
                return new LabeledExtractProtocolAssignment(exp, protocol);
            case FILE:
                return new RawOrProcessedFileProtocolAssignment(exp, protocol);
            case RAW_FILE:
                return new RawFileProtocolAssignment(exp, protocol);
            case PROCESSED_FILE:
                return new ProcessedFileProtocolAssignment(exp, protocol);
            default:
                return new ProtocolAssignment(exp, protocol) {
                    @Override
                    protected ProtocolAssignmentProfile getProfile(ExperimentProfile exp, Protocol protocol) {
                        return new ProtocolAssignmentProfile.Empty(subjectType);
                    }

                    @Override
                    protected void update(ExperimentProfile exp, Protocol protocol, Set<String> assignments) {
                        // do nothing
                    }
                };
        }
    }
}
