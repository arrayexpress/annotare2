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
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ContactDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.PublicationDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.UpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.UpdatePerformer;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.UpdateResult;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

/**
 * @author Olga Melnichuk
 */
public class ExperimentUpdatePerformer implements UpdatePerformer {

    private final ExperimentProfile exp;

    private final UpdateResult result = new UpdateResult();

    public ExperimentUpdatePerformer(ExperimentProfile exp) {
        this.exp = exp;
    }

    @Override
    public void updateDetails(DetailsDto details) {
        exp.setTitle(details.getTitle());
        exp.setDescription(details.getDescription());
        exp.setPublicReleaseDate(details.getPublicReleaseDate());
        exp.setExperimentDate(details.getExperimentDate());
        result.updated(details);
    }

    @Override
    public void createContact(ContactDto dto) {
        Contact contact = exp.createContact();
        result.created(new ContactDto(contact.getId()).updatedCopy(dto));
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
        result.updated(dto);
    }

    @Override
    public void removeContact(ContactDto dto) {
        Contact contact = exp.removeContact(dto.getId());
        if (contact != null) {
            result.removed(dto);
        }
    }

    @Override
    public void createPublication(PublicationDto dto) {
        Publication publication = exp.createPublication();
        result.updated(new PublicationDto(publication.getId()).updatedCopy(dto));
    }

    @Override
    public void updatePublication(PublicationDto dto) {
        Publication publication = exp.getPublication(dto.getId());
        publication.setTitle(dto.getTitle());
        publication.setAuthors(dto.getAuthors());
        publication.setPubMedId(dto.getPubMedId());
        result.updated(dto);
    }

    @Override
    public void removePublication(PublicationDto dto) {
        Publication publication = exp.removePublication(dto.getId());
        if (publication != null) {
            result.removed(dto);
        }
    }

    @Override
    public void updateSampleColumns(List<SampleColumn> columns) {
        Set<Integer> used = newHashSet();
        int order = 0;
        for (SampleColumn column : columns) {
            SampleAttribute attr;
            if (column.getId() < 0) {
                attr = exp.createSampleAttribute();
                result.created(new SampleColumn(attr.getId(), column));
            } else {
                attr = exp.getSampleAttribute(column.getId());
                result.updated(column);
            }
            attr.setName(column.getName());
            attr.setType(column.getType());
            attr.setEditable(column.isEditable());
            attr.setOrder(order);

            ColumnValueTypeVisitor visitor = new ColumnValueTypeVisitor();
            column.getValueType().visit(visitor);
            attr.setValueType(visitor.getValueType());

            used.add(attr.getId());
            order++;
        }
        for (SampleAttribute attr : exp.getSampleAttributes()) {
            if (!used.contains(attr.getId())) {
                exp.removeSampleAttribute(attr.getId());
                result.sampleColumnRemoved(attr.getId());
            }
        }
    }

    @Override
    public void updateSampleRow(SampleRow row) {
        SampleProfile sample = exp.getSample(row.getId());
        sample.setName(row.getName());
        sample.setValues(row.getValues());
        result.updated(row);
    }

    @Override
    public void createSample(SampleRow row) {
        SampleProfile sample = exp.createSample();
        sample.setName(row.getName());

        result.created(new SampleRow(sample.getId(), row));
    }

    @Override
    public void removeSample(SampleRow row) {
        exp.removeSample(row.getId());
        result.removed(row);
    }

    public UpdateResult run(List<UpdateCommand> commands) {
        for (UpdateCommand command : commands) {
            command.execute(this);
        }
        return result;
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
