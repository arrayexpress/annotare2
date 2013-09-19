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

import com.google.common.base.Function;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.*;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.*;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.fg.annotare2.configmodel.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Ordering.natural;
import static uk.ac.ebi.fg.annotare2.configmodel.ProtocolUsageType.*;
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

        for (Protocol protocol : exp.getProtocols()) {
            idf.protocolName.add(notNull(protocol.getName()));
            idf.protocolDescription.add(notNull(protocol.getDescription()));
            idf.protocolType.add(notNull(protocol.getType().getLabel()));
            idf.protocolTermAccession.add(notNull(protocol.getType().getAccession()));
            //todo: idf.protocolTermSourceREF
            idf.protocolHardware.add(notNull(protocol.getHardware()));
            idf.protocolSoftware.add(notNull(protocol.getSoftware()));
        }
    }

    private void generateSdrf(SDRF sdrf) throws ParseException {
        for (Sample sample : exp.getSamples()) {
            sdrf.addNode(createSourceNode(sample));
        }
    }

    private void connect(SDRFNode source, SDRFNode destination, ProtocolUsageType type) {
        Collection<Protocol> protocols = exp.getProtocols(type);
        if (protocols.isEmpty()) {
            source.addChildNode(destination);
            destination.addParentNode(source);
            return;
        }

        SDRFNode prev = source;
        for (Protocol protocol : protocols) {
            ProtocolApplicationNode protocolNode = new ProtocolApplicationNode();
            // protocol node name must be unique
            protocolNode.setNodeName(prev.getNodeName() + ":" + protocol.getId());
            protocolNode.protocol = protocol.getName();
            protocolNode.addParentNode(prev);
            prev.addChildNode(protocolNode);
            prev = protocolNode;
        }
        prev.addChildNode(destination);
        destination.addParentNode(prev);
    }

    private SourceNode createSourceNode(Sample sample) {
        SourceNode sourceNode = new SourceNode();
        sourceNode.setNodeName(sample.getName());
        sourceNode.characteristics.addAll(extractCharacteristicsAttributes(sample));
        sourceNode.materialType = extractMaterialTypeAttribute(sample);
        sourceNode.provider = extractProviderAttribute(sample);
        sourceNode.description = extractDescriptionAttribute(sample);
        addComments(sourceNode, sample);

        Collection<Extract> extracts = exp.getExtracts(sample);
        for (Extract extract : extracts) {
            ExtractNode extractNode = createExtractNode(extract);
            connect(sourceNode, extractNode, SAMPLE_AND_EXTRACT);
        }
        return sourceNode;
    }

    private ExtractNode createExtractNode(Extract extract) {
        ExtractNode extractNode = new ExtractNode();
        extractNode.setNodeName(extract.getName());
        for (ExtractAttribute attr : ExtractAttribute.values()) {
            String value = extract.getAttributeValue(attr);
            if (!isNullOrEmpty(value)) {
                extractNode.comments.put(getSdrfFriendlyName(attr), value);
            }
        }

        Collection<LabeledExtract> labeledExtracts = exp.getLabeledExtracts(extract);
        for (LabeledExtract labeledExtract : labeledExtracts) {
            LabeledExtractNode labeledExtractNode = createLabeledExtractNode(labeledExtract);
            connect(extractNode, labeledExtractNode, EXTRACT_AND_LABELED_EXTRACT);
        }

        Assay assay = findAssay(extract);
        if (assay != null) {
            AssayNode assayNode = createAssayNode(assay);
            connect(extractNode, assayNode, EXTRACT_AND_ASSAY);
        }
        return extractNode;
    }

    private static String getSdrfFriendlyName(ExtractAttribute attr) {
        switch (attr) {
            case LIBRARY_LAYOUT:
                return "LIBRARY_LAYOUT";
            case LIBRARY_SELECTION:
                return "LIBRARY_SELECTION";
            case LIBRARY_SOURCE:
                return "LIBRARY_SOURCE";
            case LIBRARY_STRATEGY:
                return "LIBRARY_STRATEGY";
            default:
                return attr.getTitle();
        }
    }

    private LabeledExtractNode createLabeledExtractNode(LabeledExtract labeledExtract) {
        LabeledExtractNode labeledExtractNode = new LabeledExtractNode();
        labeledExtractNode.setNodeName(labeledExtract.getName());
        LabelAttribute label = new LabelAttribute();
        label.setAttributeValue(labeledExtract.getLabel());
        labeledExtractNode.label = label;

        Assay assay = findAssay(labeledExtract);
        if (assay != null) {
            AssayNode assayNode = createAssayNode(assay);
            connect(labeledExtractNode, assayNode, LABELED_EXTRACT_AND_ASSAY);
        }
        return labeledExtractNode;
    }

    private Assay findAssay(Extract extract) {
        return findAssay(extract, null);
    }

    private Assay findAssay(LabeledExtract labeledExtract) {
        return findAssay(labeledExtract.getExtract(), labeledExtract.getLabel());
    }

    private Assay findAssay(Extract extract, String label) {
        return exp.getAssay(new Assay(extract, label).getId());
    }

    private AssayNode createAssayNode(Assay assay) {
        AssayNode assayNode = new AssayNode();
        assayNode.setNodeName(assay.getName());
        TechnologyTypeAttribute technologyType = new TechnologyTypeAttribute();
        technologyType.setAttributeValue(
                exp.getType().isMicroarray() ? "array assay" : "sequencing assay");
        assayNode.technologyType = technologyType;

        Collection<SDRFNode> fileNodes = createFileNodes(assay);
        for (SDRFNode fileNode : fileNodes) {
            assayNode.addChildNode(fileNode);
            fileNode.addParentNode(assayNode);
        }
        return assayNode;
    }

    private Collection<SDRFNode> createFileNodes(Assay assay) {
        Collection<FileColumn> fileColumns = getFileColumns();

        SDRFNode start = null;
        SDRFNode end = null;
        List<SDRFNode> rootNodes = new ArrayList<SDRFNode>();
        for (FileColumn fileColumn : fileColumns) {
            FileType type = fileColumn.getType();
            Long fileId = fileColumn.getFileId(assay);
            SDRFNode current;
            switch (type) {
                case RAW_FILE:
                    current = new ArrayDataNode();
                    break;
                case RAW_MATRIX_FILE:
                    current = new ArrayDataMatrixNode();
                    break;
                case PROCESSED_FILE:
                    current = new DerivedArrayDataNode();
                    break;
                case PROCESSED_MATRIX_FILE:
                    current = new DerivedArrayDataMatrixNode();
                    break;
                default:
                    throw new IllegalStateException("Unsupported file type: " + type);
            }

            current.setNodeName(fileId == null ? "none" : fileId + "");

            if (type.isRaw()) {
                rootNodes.add(current);
            } else if (start == null) {
                start = current;
                end = current;
            } else {
                end.addChildNode(current);
                current.addParentNode(end);
                end = current;
            }
        }

        if (start != null) {
            for (SDRFNode rootNode : rootNodes) {
                rootNode.addChildNode(start);
                start.addParentNode(rootNode);
            }
            if (rootNodes.isEmpty()) {
                rootNodes.add(start);
            }
        }
        return rootNodes;
    }

    private Collection<FileColumn> getFileColumns() {
        return natural().onResultOf(new Function<FileColumn, Integer>() {
            @Nullable
            @Override
            public Integer apply(@Nullable FileColumn input) {
                return input.getType().ordinal();
            }
        }).immutableSortedCopy(exp.getFileColumns());
    }

    private MaterialTypeAttribute extractMaterialTypeAttribute(Sample sample) {
        for (SampleAttribute attribute : exp.getSampleAttributes()) {
            if (attribute.getType().isMaterialType()) {
                MaterialTypeAttribute attr = new MaterialTypeAttribute();
                attr.setAttributeValue(sample.getValue(attribute));
                return attr;
            }
        }
        return null;
    }

    private ProviderAttribute extractProviderAttribute(Sample sample) {
        for (SampleAttribute attribute : exp.getSampleAttributes()) {
            if (attribute.getType().isProvider()) {
                ProviderAttribute attr = new ProviderAttribute();
                attr.setAttributeValue(sample.getValue(attribute));
                return attr;
            }
        }
        return null;
    }

    private String extractDescriptionAttribute(Sample sample) {
        for (SampleAttribute attribute : exp.getSampleAttributes()) {
            if (attribute.getType().isDescription()) {
                return sample.getValue(attribute);
            }
        }
        return null;
    }

    private List<CharacteristicsAttribute> extractCharacteristicsAttributes(Sample sample) {
        List<CharacteristicsAttribute> attributes = new ArrayList<CharacteristicsAttribute>();
        for (SampleAttribute attribute : exp.getSampleAttributes()) {
            if (attribute.getType().isCharacteristic()) {
                CharacteristicsAttribute attr = new CharacteristicsAttribute();
                attr.type = attribute.getName();
                attribute.getValueType().visit(new AttributeValueTypeVisitor(attr));
                attr.setAttributeValue(sample.getValue(attribute));
                attributes.add(attr);
            }
        }
        return attributes;
    }

    private void addComments(SourceNode node, Sample sample) {
        for (SampleAttribute attribute : exp.getSampleAttributes()) {
            if (attribute.getType().isComment()) {
                String value = sample.getValue(attribute);
                if (!isNullOrEmpty(value)) {
                    node.comments.put(attribute.getName(), value);
                }
            }
        }
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
            if (valueType.getUnits() == null) {
                return;
            }
            UnitAttribute unitAttribute = new UnitAttribute();
            unitAttribute.type = valueType.getUnits().getLabel();
            unitAttribute.termAccessionNumber = valueType.getUnits().getAccession();
            unitAttribute.setAttributeValue(valueType.getUnits().getLabel());
            //TODO unitAttribute.termSourceREF = ??
            this.attribute.unit = unitAttribute;
        }

        @Override
        public void visitTextValueType(TextAttributeValueType valueType) {
        }

        @Override
        public void visitTermValueType(TermAttributeValueType valueType) {
            attribute.type = valueType.getBranch().getLabel();
        }
    }
}
