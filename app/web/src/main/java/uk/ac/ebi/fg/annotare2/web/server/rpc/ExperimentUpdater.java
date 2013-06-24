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

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import uk.ac.ebi.fg.annotare2.configmodel.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoTermDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ExperimentUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ExperimentUpdatePerformer;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
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
            case TWO_COLOR_MICROARRAY:
            case SEQUENCING:
                return new BasicExperimentUpdater(exp);
        }
        throw new IllegalArgumentException("No updater for experiment type: " + type);
    }

    @Override
    public void updateDetails(ExperimentDetailsDto details) {
        exp.setTitle(details.getTitle());
        exp.setDescription(details.getDescription());
        exp.setPublicReleaseDate(details.getPublicReleaseDate());
        exp.setExperimentDate(details.getExperimentDate());
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
            attr.setType(column.getType());
            attr.setEditable(column.isEditable());

            ColumnValueTypeVisitor visitor = new ColumnValueTypeVisitor();
            column.getValueType().visit(visitor);
            attr.setValueType(visitor.getValueType());
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
        createSample("New Sample");
    }

    public Sample createSample(String name) {
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
            extract.setValues(row.getValues());
        }
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
        public void visitTermValueType(EfoTermValueType valueType) {
            EfoTermDto term = valueType.getEfoTerm();
            this.valueType = new TermAttributeValueType(new OntologyTerm(term.getAccession(), term.getLabel()));
        }

        @Override
        public void visitTextValueType(TextValueType valueType) {
            this.valueType = new TextAttributeValueType();
        }

        @Override
        public void visitNumericValueType(NumericValueType valueType) {
            EfoTermDto term = valueType.getUnits();
            this.valueType = new NumericAttributeValueType(new OntologyTerm(term.getAccession(), term.getLabel()));
        }

        public AttributeValueType getValueType() {
            return valueType;
        }
    }
}
