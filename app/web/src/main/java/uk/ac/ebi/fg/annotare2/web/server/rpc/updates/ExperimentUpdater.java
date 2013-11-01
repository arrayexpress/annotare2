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

package uk.ac.ebi.fg.annotare2.web.server.rpc.updates;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import uk.ac.ebi.fg.annotare2.configmodel.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ExperimentUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ExperimentUpdatePerformer;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;

/**
 * @author Olga Melnichuk
 */
public abstract class ExperimentUpdater implements ExperimentUpdatePerformer {

    private final ExperimentProfile exp;

    protected ExperimentUpdater(ExperimentProfile exp) {
        this.exp = exp;
    }

    public static ExperimentUpdater experimentUpdater(ExperimentProfile exp) {
        ExperimentProfileType type = exp.getType();
        switch (type) {
            case ONE_COLOR_MICROARRAY:
                return new OneColorMicroarrayUpdater(exp);
            case TWO_COLOR_MICROARRAY:
                return new TwoColorMicroarrayUpdater(exp);
            case SEQUENCING:
                return new SequencingUpdater(exp);
        }
        throw new IllegalArgumentException("No updater for experiment type: " + type);
    }

    @Override
    public void updateDetails(ExperimentDetailsDto details) {
        exp.setTitle(details.getTitle());
        exp.setDescription(details.getDescription());
        exp.setPublicReleaseDate(details.getPublicReleaseDate());
        exp.setExperimentDate(details.getExperimentDate());
        exp.setAeExperimentType(details.getAeExperimentType());
        exp.setExperimentalDesigns(details.getExperimentalDesigns());
    }

    @Override
    public void createContact() {
        exp.createContact();
    }

    @Override
    public void updateContact(ContactDto dto) {
        Contact contact = exp.getContact(dto.getId());
        contact.setFirstName(dto.getFirstName());
        contact.setLastName(dto.getLastName());
        contact.setMidInitials(dto.getMidInitials());
        contact.setEmail(dto.getEmail());
        contact.setPhone(dto.getPhone());
        contact.setFax(dto.getFax());
        contact.setAddress(dto.getAddress());
        contact.setAffiliation(dto.getAffiliation());
        contact.setRoles(dto.getRoles());
    }

    @Override
    public void removeContacts(List<ContactDto> dtos) {
        for (ContactDto dto : dtos) {
            exp.removeContact(dto.getId());
        }
    }

    @Override
    public void createPublication() {
        exp.createPublication();
    }

    @Override
    public void updatePublication(PublicationDto dto) {
        Publication publication = exp.getPublication(dto.getId());
        publication.setTitle(dto.getTitle());
        publication.setAuthors(dto.getAuthors());
        publication.setPubMedId(dto.getPubMedId());
        publication.setDoi(dto.getDoi());
        publication.setStatus(dto.getStatus());
    }

    @Override
    public void removePublications(List<PublicationDto> dtos) {
        for (PublicationDto dto : dtos) {
            exp.removePublication(dto.getId());
        }
    }

    @Override
    public void updateSampleAttributes(List<SampleColumn> columns) {
        Set<Integer> used = newLinkedHashSet();
        for (SampleColumn column : columns) {
            SampleAttribute attr;
            if (column.getId() > 0) {
                attr = exp.getSampleAttribute(column.getId());
            } else {
                attr = exp.createSampleAttribute();
            }
            attr.setName(column.getName());
            attr.setTerm(column.getTerm());
            attr.setType(column.getType());
            attr.setEditable(column.isEditable());

            ColumnValueTypeVisitor visitor = new ColumnValueTypeVisitor();
            column.getValueType().visit(visitor);
            visitor.getValueType().set(attr);
            used.add(attr.getId());
        }
        List<SampleAttribute> attributes = newArrayList(exp.getSampleAttributes());
        for (SampleAttribute attr : attributes) {
            if (!used.contains(attr.getId())) {
                exp.removeSampleAttribute(attr.getId());
            }
        }
        exp.setSampleAttributeOrder(used);
    }

    @Override
    public void updateSample(SampleRow row) {
        Sample sample = exp.getSample(row.getId());
        sample.setName(row.getName());
        sample.setValues(row.getValues());
    }

    @Override
    public void createSample() {
        createAndReturnSample();
    }

    protected final Sample createAndReturnSample() {
        Collection<String> existedNames = Collections2.transform(
                exp.getSamples(),
                new Function<Sample, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable Sample input) {
                        return input.getName();
                    }
                }
        );

        Sample sample = exp.createSample();
        sample.setName(newName("Sample", existedNames));
        return sample;
    }

    @Override
    public void removeSamples(List<SampleRow> rows) {
        for (SampleRow row : rows) {
            exp.removeSample(row.getId());
        }
    }

    @Override
    public void updateExtractAttributes(ExtractAttributesRow row) {
        Extract extract = exp.getExtract(row.getId());
        if (extract != null) {
            extract.setAttributeValues(row.getValues());
        }
    }

    @Override
    public void updateExtractLabels(ExtractLabelsRow row) {
        Extract extract = exp.getExtract(row.getId());
        if (extract == null) {
            return;
        }
        Collection<LabeledExtract> labeledExtracts = exp.getLabeledExtracts(extract);
        Set<String> newLabels = new HashSet<String>(row.getLabels());
        Set<String> existedLabels = new HashSet<String>();
        for (LabeledExtract labeledExtract : labeledExtracts) {
            if (!newLabels.contains(labeledExtract.getLabel())) {
                exp.removeLabeledExtract(labeledExtract);
            } else {
                existedLabels.add(labeledExtract.getLabel());
            }
        }

        newLabels.removeAll(existedLabels);
        for (String label : newLabels) {
            exp.createLabeledExtract(extract, label);
        }
    }

    @Override
    public void createProtocol(ProtocolType protocolType) {
        Collection<String> existedNames = Collections2.transform(
                exp.getProtocols(),
                new Function<Protocol, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable Protocol input) {
                        return input.getName();
                    }
                }
        );

        Protocol protocol = exp.createProtocol(protocolType.getTerm(), protocolType.getUsageType());
        protocol.setName(newName("Protocol", existedNames));
    }

    private String newName(String prefix, Collection<String> existedNames) {
        Set<String> names = newHashSet(existedNames);
        CompositeName name = new CompositeName(prefix);
        while (names.contains(name.next())) {
        }
        return name.toString();
    }

    @Override
    public void updateProtocol(ProtocolRow row) {
        Protocol protocol = exp.getProtocol(row.getId());
        if (protocol != null) {
            protocol.setName(row.getName());
            protocol.setDescription(row.getDescription());
            protocol.setHardware(row.getHardware());
            protocol.setSoftware(row.getSoftware());
            protocol.setContact(row.getContact());
            protocol.setParameters(row.getParameters());
        }
    }

    @Override
    public void removeProtocols(List<ProtocolRow> rows) {
        for (ProtocolRow row : rows) {
            Protocol protocol = exp.getProtocol(row.getId());
            exp.removeProtocol(protocol);
        }
    }

    @Override
    public void createDataAssignmentColumn(FileType fileType) {
        exp.createFileColumn(fileType);
    }

    @Override
    public void removeDataAssignmentColumns(List<Integer> indices) {
        for (Integer index : indices) {
            exp.removeFileColumn(index);
        }
    }

    @Override
    public void updateDataAssignmentColumn(DataAssignmentColumn column) {
        FileColumn fileColumn = exp.getFileColumn(column.getIndex());
        Set<String> assayIds = new HashSet<String>(column.getAssayIds());
        for (String assayId : assayIds) {
            Assay assay = exp.getAssay(assayId);
            if (assay != null) {
                fileColumn.setFileName(assay, column.getFileName(assayId));
            }
        }
        for (Assay assay : fileColumn.getAssays()) {
            if (!assayIds.contains(assay.getId())) {
                fileColumn.setFileName(assay, null);
            }
        }
    }

    @Override
    public void updateProtocolAssignments(ProtocolAssignmentProfileUpdates updates) {
        Protocol protocol = exp.getProtocol(updates.getProtocolId());
        if (protocol != null) {
            protocol.getTargetType().setProtocolAssignments(protocol, exp, updates.getAssignments());
        }
    }

    @Override
    public void moveProtocolDown(ProtocolRow row) {
        exp.moveProtocolDown(exp.getProtocol(row.getId()));
    }

    @Override
    public void moveProtocolUp(ProtocolRow row) {
        exp.moveProtocolUp(exp.getProtocol(row.getId()));
    }

    @Override
    public void updateSettings(ExperimentSettings settings) {
        // override me
    }

    public void run(List<ExperimentUpdateCommand> commands) {
        for (ExperimentUpdateCommand command : commands) {
            command.execute(this);
        }
    }

    protected ExperimentProfile exp() {
        return exp;
    }

    private static class ColumnValueTypeVisitor implements ColumnValueType.Visitor {

        private AttributeValueType valueType;

        @Override
        public void visitTermValueType(OntologyTermValueType valueType) {
            this.valueType = new TermAttributeValueType(valueType.getEfoTerm());
        }

        @Override
        public void visitTextValueType(TextValueType valueType) {
            this.valueType = new TextAttributeValueType();
        }

        @Override
        public void visitNumericValueType(NumericValueType valueType) {
            this.valueType = new NumericAttributeValueType(valueType.getUnits());
        }

        public AttributeValueType getValueType() {
            return valueType;
        }
    }

    private static class CompositeName {
        private final String prefix;
        private int next;

        private CompositeName(String prefix) {
            this.prefix = prefix;
        }

        public String next() {
            return format(prefix, ++next);
        }

        private static String format(String p, int n) {
            return p + " " + n;
        }

        @Override
        public String toString() {
            return format(prefix, next);
        }
    }
}
