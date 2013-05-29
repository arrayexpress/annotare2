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

package uk.ac.ebi.fg.annotare2.magetab.integration;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.ExtractNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.LabeledExtractNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.SDRFNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.SampleNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.CharacteristicsAttribute;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.MaterialTypeAttribute;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.UnitAttribute;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.fg.annotare2.configmodel.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Maps.newHashMap;
import static uk.ac.ebi.fg.annotare2.magetab.integration.MageTabUtils.formatDate;

/**
 * @author Olga Melnichuk
 */
public class MageTabGenerator {

    private final ExperimentProfile exp;

    public MageTabGenerator(ExperimentProfile exp) {
        this.exp = exp;
    }

    public MAGETABInvestigation generate() throws ParseException {
        MAGETABInvestigation inv = new MAGETABInvestigation();
        generateIdf(inv.IDF);
        generateSdrf(inv.SDRF);
        return inv;
    }

    private void generateIdf(IDF idf) {
        idf.investigationTitle = notNull(exp.getTitle());
        idf.experimentDescription = notNull(exp.getDescription());
        idf.publicReleaseDate = notNull(formatDate(exp.getPublicReleaseDate()));
        idf.dateOfExperiment = notNull(formatDate(exp.getExperimentDate()));
        idf.accession = notNull(exp.getAccession());

        for (Contact contact : exp.getContacts()) {
            idf.personFirstName.add(notNull(contact.getFirstName()));
            idf.personLastName.add(notNull(contact.getLastName()));
            idf.personMidInitials.add(notNull(contact.getMidInitials()));
            idf.personEmail.add(notNull(contact.getEmail()));
            idf.personPhone.add(notNull(contact.getPhone()));
            idf.personFax.add(notNull(contact.getFax()));
            idf.personAddress.add(notNull(contact.getAddress()));
            idf.personAffiliation.add(notNull(contact.getAffiliation()));
            idf.personRoles.add(notNull(contact.getRoles()));
        }

        for (Publication publication : exp.getPublications()) {
            idf.publicationTitle.add(notNull(publication.getTitle()));
            idf.publicationAuthorList.add(notNull(publication.getAuthors()));
            idf.pubMedId.add(notNull(publication.getPubMedId()));
            //idf.publicationDOI.add(notNull(publication.getPubMedId()));
            //idf.publicationStatus.add(notNull(publication.getStatus()));
        }
    }

    private void generateSdrf(SDRF sdrf) throws ParseException {
        Map<Integer, SDRFNode> map = newHashMap();
        for (SampleProfile sample : exp.getSamples()) {
            SampleNode sampleNode = new SampleNode();
            sampleNode.setNodeName(sample.getName());
            sampleNode.characteristics.addAll(extractCharacteristicsAttributes(sample));
            sampleNode.materialType = extractMaterialTypeAttribute(sample);
            sdrf.addNode(sampleNode);
            map.put(sample.getId(), sampleNode);
        }

        for (LabeledExtractProfile labeledExtract : exp.getLabeledExtracts()) {
            SDRFNode sampleNode = map.get(labeledExtract.getSample().getId());

            ExtractNode extractNode = new ExtractNode();
            extractNode.setNodeName(labeledExtract.getSample().getName());
            sampleNode.addChildNode(extractNode);
            extractNode.addParentNode(sampleNode);

            LabeledExtractNode labeledExtractNode = new LabeledExtractNode();
            labeledExtractNode.setNodeName(labeledExtract.getName());
        }
    }

    private MaterialTypeAttribute extractMaterialTypeAttribute(SampleProfile sample) {
        for (SampleAttribute attribute : exp.getSampleAttributes()) {
            if (attribute.getType().isMaterialType()) {
                MaterialTypeAttribute attr = new MaterialTypeAttribute();
                attr.setAttributeValue(sample.getValue(attribute));
                return attr;
            }
        }
        return null;
    }

    private List<CharacteristicsAttribute> extractCharacteristicsAttributes(SampleProfile sample) {
        List<CharacteristicsAttribute> attributes = new ArrayList<CharacteristicsAttribute>();
        for (SampleAttribute attribute : exp.getSampleAttributes()) {
            if (attribute.getType().isCharacteristic()) {
                CharacteristicsAttribute attr = new CharacteristicsAttribute();
                attr.type = attribute.getName();
                attribute.getValueType().visit(new AttributeValueTypeVisitor(attr));
                attr.setAttributeValue(sample.getValue(attribute));
            }
        }
        return attributes;
    }

    private static String notNull(String str) {
        return str == null || str.trim().isEmpty() ? "" : str;
    }

    private static String notNull(Collection<String> collection) {
        return on(",").join(collection);
    }

    private static class AttributeValueTypeVisitor implements AttributeValueType.Visitor {

        private final CharacteristicsAttribute attribute;

        private AttributeValueTypeVisitor(CharacteristicsAttribute attribute) {
            this.attribute = attribute;
        }

        @Override
        public void visitNumericValueType(NumericAttributeValueType valueType) {
            UnitAttribute unitAttribute = new UnitAttribute();
            unitAttribute.type = valueType.getUnits().getLabel();
            unitAttribute.termAccessionNumber = valueType.getUnits().getAccession();
            //TODO unitAttribute.termSourceREF = ??
            this.attribute.unit = unitAttribute;
        }

        @Override
        public void visitTextValueType(TextAttributeValueType valueType) {
        }

        @Override
        public void visitTermValueType(TermAttributeValueType valueType) {
            attribute.type = valueType.getBranch().getLabel();
            attribute.termAccessionNumber = valueType.getBranch().getAccession();
            // TODO attribute.termSourceREF = term.getSource().getId();
        }
    }
}
