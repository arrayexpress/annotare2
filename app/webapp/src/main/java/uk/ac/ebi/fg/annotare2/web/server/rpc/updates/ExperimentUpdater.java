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

package uk.ac.ebi.fg.annotare2.web.server.rpc.updates;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import uk.ac.ebi.fg.annotare2.core.utils.NamingPatternUtil;
import uk.ac.ebi.fg.annotare2.submission.model.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ExperimentUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ExperimentUpdatePerformer;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolAssignment.createProtocolAssignment;

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
        exp.setAnonymousReview(details.isAnonymousReviewEnabled());
        exp.setRelatedAccessionNumber(removeDuplicateAccessionNumber(details.getRelatedAccessionNumber()));
    }

    private String removeDuplicateAccessionNumber(String accessionNumber)
    {
        return new LinkedHashSet<String>(Arrays.asList(accessionNumber.split(",\\s*\\t*"))).toString().replaceAll("(^\\[|\\]$)", "");
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
                attr = exp.createSampleAttribute(column.getTemplate().name());
            }
            attr.setName(column.getName());
            attr.setType(column.getType());
            attr.setTerm(column.getTerm());
            attr.setUnits(column.getUnits());
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
    public void createSamples(int numOfSamples, String namingPattern, int startingNumber) {
        String format = NamingPatternUtil.convert(namingPattern);

        int index = startingNumber;
        for (int i = 0; i < numOfSamples; ++i) {
            String name;
            do {
                name = String.format(format, index++);
            } while (null != exp.getSampleByName(name));

            createSample(name);
        }
    }

    protected Sample createSample(String name) {
        Sample sample = exp.createSample();
        sample.setName(name);
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
    public void updateExtractLabels(LabeledExtractsRow row) {
        Extract extract = exp.getExtract(row.getId());
        if (extract == null) {
            return;
        }
        Collection<LabeledExtract> labeledExtracts = exp.getLabeledExtracts(extract);
        Set<String> newLabels = new HashSet<>(row.getLabels());
        Set<String> existedLabels = new HashSet<>();
        for (LabeledExtract labeledExtract : labeledExtracts) {
            if (!newLabels.contains(labeledExtract.getLabel().getName())) {
                exp.removeLabeledExtract(labeledExtract.getId());
            } else {
                existedLabels.add(labeledExtract.getLabel().getName());
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
                        return null != input ? input.getName() : null;
                    }
                }
        );

        //Protocol protocol = exp.createProtocol(protocolType.getTerm(), protocolType.getSubjectType());
        //protocol.setName(newName("Protocol", existedNames));
    }

    @Override
    public void createProtocol(List<ProtocolDetail> protocolDetails)
    {
        Collection<String> existedNames = Collections2.transform(
                exp.getProtocols(),
                new Function<Protocol, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable Protocol input) {
                        return null != input ? input.getName() : null;
                    }
                }
        );

        ArrayList<String> newNames = new ArrayList<>();

        for (ProtocolDetail detail:
             protocolDetails) {
            Protocol protocol = exp.createProtocol(detail.getProtocolType().getTerm(), detail.getProtocolType().getSubjectType(), detail.getProtocolDescription());
            protocol.setName(newName("Protocol", existedNames, newNames));
            newNames.add(protocol.getName());
        }
    }




    private String newName(String prefix, Collection<String> existedNames, ArrayList<String> newNames) {
        Set<String> names = newHashSet(existedNames);
        CompositeName name = new CompositeName(prefix);

        name.next();

        while (names.contains(name.toString()) || newNames.contains(name.toString()) ) {
            name.next();
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
            protocol.setPerformer(row.getPerformer());
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
        Collections.sort(indices, Collections.reverseOrder());
        for (Integer index : indices) {
            exp.removeFileColumn(index);
        }
    }

    @Override
    public void updateDataAssignmentColumn(DataAssignmentColumn column) {
        FileColumn fileColumn = exp.getFileColumn(column.getIndex());
        fileColumn.removeAll();

        for (String labeledExtractId : column.getLabeledExtractIds()) {
            fileColumn.setFileRef(labeledExtractId, column.getFileRef(labeledExtractId));
        }
    }

    @Override
    public void updateProtocolAssignments(ProtocolAssignmentProfileUpdates updates) {
        Protocol protocol = exp.getProtocol(updates.getProtocolId());
        createProtocolAssignment(exp, protocol).update(updates.getAssignments());
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

    private static class CompositeName {
        private String prefix;
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
