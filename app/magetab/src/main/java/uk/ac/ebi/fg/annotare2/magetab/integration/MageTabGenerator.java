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
import java.util.*;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Ordering.natural;
import static java.util.Collections.emptyMap;
import static uk.ac.ebi.fg.annotare2.configmodel.ProtocolTargetType.*;
import static uk.ac.ebi.fg.annotare2.configmodel.TermSource.EFO_TERM_SOURCE;
import static uk.ac.ebi.fg.annotare2.magetab.integration.MageTabUtils.formatDate;

/**
 * @author Olga Melnichuk
 */
public class MageTabGenerator {

    private final ExperimentProfile exp;

    private final Map<String, SDRFNode> nodeCache = new HashMap<String, SDRFNode>();
    private final Set<TermSource> usedTermSources = new HashSet<TermSource>();
    private int counter;

    public MageTabGenerator(ExperimentProfile exp) {
        this.exp = exp;
    }

    public MAGETABInvestigation generate() throws ParseException {
        nodeCache.clear();
        counter = 1;

        MAGETABInvestigation inv = new MAGETABInvestigation();
        generateIdf(inv.IDF);
        generateSdrf(inv.SDRF);
        addTermSources(inv.IDF);
        return inv;
    }

    private void generateIdf(IDF idf) {
        if (!isNullOrEmpty(exp.getAeExperimentType())) {
            idf.addComment("AEExperimentType", exp.getAeExperimentType());
        }

        idf.investigationTitle = notNull(exp.getTitle());
        idf.experimentDescription = notNull(exp.getDescription());
        idf.publicReleaseDate = notNull(formatDate(exp.getPublicReleaseDate()));
        idf.dateOfExperiment = notNull(formatDate(exp.getExperimentDate()));
        idf.accession = notNull(exp.getAccession());

        for (OntologyTerm term : exp.getExperimentalDesigns()) {
            idf.experimentalDesign.add(notNull(term.getLabel()));
            idf.experimentalDesignTermAccession.add(notNull(term.getAccession()));
            idf.experimentalDesignTermSourceREF.add(ensureTermSource(EFO_TERM_SOURCE).getName());
        }

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
            idf.publicationDOI.add(notNull(publication.getDoi()));
            OntologyTerm status = publication.getStatus();
            idf.publicationStatus.add(notNull(status == null ? null : status.getLabel()));
            idf.publicationStatusTermAccession.add(notNull(status == null ? null : status.getAccession()));
            idf.publicationStatusTermSourceREF.add(notNull(status == null ? null : ensureTermSource(EFO_TERM_SOURCE).getName()));
        }

        for (Protocol protocol : exp.getProtocols()) {
            idf.protocolName.add(notNull(protocol.getName()));
            idf.protocolDescription.add(notNull(protocol.getDescription()));
            idf.protocolType.add(notNull(protocol.getType().getLabel()));
            idf.protocolTermAccession.add(notNull(protocol.getType().getAccession()));
            idf.protocolTermSourceREF.add(ensureTermSource(EFO_TERM_SOURCE).getName());
            idf.protocolHardware.add(notNull(protocol.getHardware()));
            idf.protocolSoftware.add(notNull(protocol.getSoftware()));
        }

        for (SampleAttribute attribute : exp.getSampleAttributes()) {
            if (!attribute.getType().isFactorValue()) {
                continue;
            }
            OntologyTerm term = attribute.getTerm();
            idf.experimentalFactorName.add(notNull(attribute.getName()));
            idf.experimentalFactorType.add(notNull(term == null ? null : term.getLabel()));
            idf.experimentalFactorTermAccession.add(notNull(term == null ? null : term.getAccession()));
            idf.experimentalFactorTermSourceREF.add(notNull(term == null ? null : ensureTermSource(EFO_TERM_SOURCE).getName()));
        }
    }

    private void addTermSources(IDF idf) {
        for (TermSource termSource : usedTermSources) {
            idf.termSourceName.add(notNull(termSource.getName()));
            idf.termSourceVersion.add(notNull(termSource.getVersion()));
            idf.termSourceFile.add(notNull(termSource.getUrl()));
        }
    }

    private String nodeId(Class<?> clazz, String name) {
        return clazz + "@" + name;
    }

    private <T extends SDRFNode> T createFakeNode(Class<T> clazz) {
        return createNode(clazz, "__UNASSIGNED__@" + (counter++));
    }

    private <T extends SDRFNode> T createNode(Class<T> clazz, String name) {
        try {
            T t = clazz.newInstance();
            t.setNodeName(name);
            nodeCache.put(nodeId(clazz, name), t);
            return t;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends SDRFNode> T getNode(Class<T> clazz, String name) {
        return (T) nodeCache.get(nodeId(clazz, name));
    }

    private void generateSdrf(SDRF sdrf) throws ParseException {
        Map<Integer, SDRFNode> sourceLayer = generateSourceNodes();
        for (SDRFNode node : sourceLayer.values()) {
            sdrf.addNode(node);
        }

        Map<Integer, SDRFNode> extractLayer = generateExtractNodes(sourceLayer);
        Map<String, SDRFNode> assayLayer;
        if (exp.getType().isMicroarray()) {
            Map<String, SDRFNode> labeledExtractLayer = generateLabeledExtractNodes(extractLayer);
            assayLayer = generateAssayNodes(labeledExtractLayer);
        } else {
            assayLayer = generateAssayAndScanNodes(extractLayer);
        }

        generateDataFileNodes(assayLayer);
    }

    private Map<Integer, SDRFNode> generateSourceNodes() {
        Map<Integer, SDRFNode> layer = new LinkedHashMap<Integer, SDRFNode>();
        for (Sample sample : exp.getSamples()) {
            layer.put(sample.getId(), createSourceNode(sample));
        }
        return layer;
    }

    private Map<Integer, SDRFNode> generateExtractNodes(Map<Integer, SDRFNode> sampleLayer) {
        if (sampleLayer.isEmpty() || exp.getExtracts().isEmpty()) {
            return emptyMap();
        }

        Map<Integer, SDRFNode> layer = new LinkedHashMap<Integer, SDRFNode>();
        int fakeId = -1;
        for (Integer sampleId : sampleLayer.keySet()) {
            Sample sample = exp.getSample(sampleId);
            SDRFNode sampleNode = sampleLayer.get(sampleId);
            Collection<Extract> extracts = exp.getExtracts(sample);
            if (extracts.isEmpty()) {
                layer.put(fakeId--, createExtractNode(null, sampleNode));
            }
            for (Extract extract : extracts) {
                layer.put(extract.getId(), createExtractNode(extract, sampleNode));
            }
        }
        return layer;
    }

    private Map<String, SDRFNode> generateLabeledExtractNodes(Map<Integer, SDRFNode> extractLayer) {
        if (extractLayer.isEmpty() || exp.getLabeledExtracts().isEmpty()) {
            return emptyMap();
        }

        Map<String, SDRFNode> layer = new LinkedHashMap<String, SDRFNode>();
        int fakeId = -1;
        for (Integer extractId : extractLayer.keySet()) {
            Extract extract = exp.getExtract(extractId);
            SDRFNode extractNode = extractLayer.get(extractId);
            Collection<LabeledExtract> labeledExtracts = extract == null ?
                    Collections.<LabeledExtract>emptyList() : exp.getLabeledExtracts(extract);
            if (labeledExtracts.isEmpty()) {
                layer.put("" + (fakeId--), createLabeledExtractNode(null, extractNode));
            }
            for (LabeledExtract labeledExtract : labeledExtracts) {
                layer.put(labeledExtract.getId(), createLabeledExtractNode(labeledExtract, extractNode));
            }
        }
        return layer;
    }

    private Map<String, SDRFNode> generateAssayNodes(Map<String, SDRFNode> labeledExtractLayer) {
        if (labeledExtractLayer.isEmpty()) {
            return emptyMap();
        }

        Map<String, SDRFNode> layer = new LinkedHashMap<String, SDRFNode>();
        int fakeId = -1;
        for (String labeledExtractId : labeledExtractLayer.keySet()) {
            LabeledExtract labeledExtract = exp.getLabeledExtract(labeledExtractId);
            SDRFNode labeledExtractNode = labeledExtractLayer.get(labeledExtractId);
            Assay assay = labeledExtract == null ? null : getAssay(labeledExtract);
            if (assay == null) {
                layer.put("" + (fakeId--), createAssayNode(null, labeledExtractNode));
            } else {
                layer.put(assay.getId(), createAssayNode(assay, labeledExtractNode));
            }
        }
        return layer;
    }

    private Map<String, SDRFNode> generateAssayAndScanNodes(Map<Integer, SDRFNode> extractLayer) {
        if (extractLayer.isEmpty()) {
            return emptyMap();
        }

        Map<String, SDRFNode> layer = new LinkedHashMap<String, SDRFNode>();
        int fakeId = -1;
        for (Integer extractId : extractLayer.keySet()) {
            Extract extract = exp.getExtract(extractId);
            SDRFNode extractNode = extractLayer.get(extractId);
            Assay assay = getAssay(extract);
            if (assay == null) {
                SDRFNode assayNode = createAssayNode(null, extractNode);
                layer.put("" + (fakeId--), createScanNode(null, assayNode));
            } else {
                SDRFNode assayNode = createAssayNode(assay, extractNode);
                layer.put(assay.getId(), createScanNode(assay, assayNode));
            }
        }
        return layer;
    }

    private void generateDataFileNodes(Map<String, SDRFNode> assayLayer) {
        if (assayLayer.isEmpty() || exp.getFileColumns().isEmpty()) {
            return;
        }
        for (String assayId : assayLayer.keySet()) {
            Assay assay = exp.getAssay(assayId);
            SDRFNode assayNode = assayLayer.get(assayId);
            createFileNodes(assay, assayNode);
        }
    }

    private void connect(SDRFNode source, SDRFNode destination, ProtocolTargetType type, HasProtocolAssignment protocolAssignment) {
        Collection<Protocol> protocols = type == null ? Collections.<Protocol>emptyList() : exp.getProtocols(type);
        if (protocols.isEmpty()) {
            connect(source, destination);
            return;
        }

        SDRFNode prev = source;
        for (Protocol protocol : protocols) {
            if (protocol.isAssigned2All() || protocolAssignment.hasProtocol(protocol)) {
                // protocol node name must be unique
                String nodeName = prev.getNodeName() + ":" + protocol.getId();
                ProtocolApplicationNode protocolNode = getNode(ProtocolApplicationNode.class, nodeName);
                if (protocolNode == null) {
                    protocolNode = createNode(ProtocolApplicationNode.class, nodeName);
                    protocolNode.setNodeName(prev.getNodeName() + ":" + protocol.getId());
                    protocolNode.protocol = protocol.getName();
                }
                connect(prev, protocolNode);
                prev = protocolNode;
            }
        }
        connect(prev, destination);
    }

    private void connect(SDRFNode source, SDRFNode destination) {
        source.addChildNode(destination);
        destination.addParentNode(source);
    }

    private SourceNode createSourceNode(Sample sample) {
        SourceNode sourceNode = getNode(SourceNode.class, sample.getName());
        if (sourceNode != null) {
            return sourceNode;
        }
        sourceNode = createNode(SourceNode.class, sample.getName());
        sourceNode.characteristics.addAll(extractCharacteristicsAttributes(sample));
        sourceNode.materialType = extractMaterialTypeAttribute(sample);
        sourceNode.provider = extractProviderAttribute(sample);
        sourceNode.description = extractDescriptionAttribute(sample);
        addComments(sourceNode, sample);
        return sourceNode;
    }

    private ExtractNode createExtractNode(Extract extract, SDRFNode parentNode) {
        ExtractNode extractNode;
        if (extract == null) {
            extractNode = createFakeNode(ExtractNode.class);
        } else {
            extractNode = getNode(ExtractNode.class, extract.getName());
            if (extractNode != null) {
                return extractNode;
            }
            extractNode = createNode(ExtractNode.class, extract.getName());
            for (ExtractAttribute attr : ExtractAttribute.values()) {
                String value = extract.getAttributeValue(attr);
                if (!isNullOrEmpty(value)) {
                    extractNode.comments.put(getSdrfFriendlyName(attr), value);
                }
            }
        }

        connect(parentNode, extractNode, EXTRACTS, extract);
        return extractNode;
    }

    private LabeledExtractNode createLabeledExtractNode(LabeledExtract labeledExtract, SDRFNode extractNode) {
        LabeledExtractNode labeledExtractNode;
        if (labeledExtract == null) {
            labeledExtractNode = createFakeNode(LabeledExtractNode.class);
        } else {
            labeledExtractNode = getNode(LabeledExtractNode.class, labeledExtract.getName());
            if (labeledExtractNode != null) {
                return labeledExtractNode;
            }
            labeledExtractNode = createNode(LabeledExtractNode.class, labeledExtract.getName());
            LabelAttribute label = new LabelAttribute();
            label.setAttributeValue(labeledExtract.getLabel());
            labeledExtractNode.label = label;
        }

        connect(extractNode, labeledExtractNode, LABELED_EXTRACTS, labeledExtract);
        return labeledExtractNode;
    }

    private AssayNode createAssayNode(Assay assay, SDRFNode prevNode) {
        AssayNode assayNode;
        if (assay == null) {
            assayNode = createFakeNode(AssayNode.class);
        } else {
            assayNode = getNode(AssayNode.class, assay.getName());
            if (assayNode != null) {
                return assayNode;
            }
            assayNode = createNode(AssayNode.class, assay.getName());
            TechnologyTypeAttribute technologyType = new TechnologyTypeAttribute();
            technologyType.setAttributeValue(
                    exp.getType().isMicroarray() ? "array assay" : "sequencing assay");
            assayNode.technologyType = technologyType;

            String arrayDesign = exp.getArrayDesign();
            if (!isNullOrEmpty(arrayDesign)) {
                ArrayDesignAttribute arrayDesignAttribute = new ArrayDesignAttribute();
                arrayDesignAttribute.setAttributeValue(arrayDesign);
                arrayDesignAttribute.termAccessionNumber = arrayDesign;
                arrayDesignAttribute.termSourceREF = ensureTermSource(TermSource.ARRAY_EXPRESS_TERM_SOURCE).getName();
                assayNode.arrayDesigns.add(arrayDesignAttribute);
            }

            Sample sample = findSample(assay);
            for (SampleAttribute attribute : exp.getSampleAttributes()) {
                if (!attribute.getType().isFactorValue()) {
                    continue;
                }
                FactorValueAttribute attr = new FactorValueAttribute();
                attr.type = attribute.getName();
                attribute.getValueType().visit(AttributeValueTypeVisitor.visitFactorValue(attr));
                attr.setAttributeValue(sample.getValue(attribute));
                assayNode.factorValues.add(attr);
            }
        }

        connect(prevNode, assayNode, ASSAYS, assay);
        return assayNode;
    }

    private Sample findSample(Assay assay) {
        Collection<Sample> samples = exp.getSamples(assay.getExtract());
        if (samples.size() != 1) {
            throw new IllegalStateException("Too many samples per assya found: " + samples.size());
        }
        return samples.iterator().next();
    }

    private ScanNode createScanNode(Assay assay, SDRFNode assayNode) {
        ScanNode scanNode;
        if (assay == null) {
            scanNode = createFakeNode(ScanNode.class);
        } else {
            scanNode = getNode(ScanNode.class, assay.getName());
            if (scanNode != null) {
                return scanNode;
            }
            scanNode = createNode(ScanNode.class, assay.getName());
        }

        connect(assayNode, scanNode, null, null);
        return scanNode;
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

    private Assay getAssay(Extract extract) {
        return getAssay(extract, null);
    }

    private Assay getAssay(LabeledExtract labeledExtract) {
        return getAssay(labeledExtract.getExtract(), labeledExtract.getLabel());
    }

    private Assay getAssay(Extract extract, String label) {
        return exp.getAssay(new Assay(extract, label).getId());
    }

    private void createFileNodes(Assay assay, SDRFNode assayNode) {
        Collection<FileColumn> fileColumns = getSortedFileColumns();

        List<SDRFNode> prev = new ArrayList<SDRFNode>();
        List<SDRFNode> next = new ArrayList<SDRFNode>();
        for (FileColumn fileColumn : fileColumns) {
            FileType type = fileColumn.getType();
            String fileName = fileColumn.getFileName(assay);
            SDRFNode current;
            switch (type) {
                case RAW_FILE:
                    current = createDataFileNode(fileName, ArrayDataNode.class);
                    break;
                case RAW_MATRIX_FILE:
                    current = createDataFileNode(fileName, ArrayDataMatrixNode.class);
                    break;
                case PROCESSED_FILE:
                    current = createDataFileNode(fileName, DerivedArrayDataNode.class);
                    break;
                case PROCESSED_MATRIX_FILE:
                    current = createDataFileNode(fileName, DerivedArrayDataMatrixNode.class);
                    break;
                default:
                    throw new IllegalStateException("Unsupported file type: " + type);
            }
            if (type.isRaw()) {
                // always connect raw data files to assays
                connect(assayNode, current, RAW_FILES, fileColumn.getFileRef(fileName));
                prev.add(current);
            } else {
                if (prev.isEmpty()) {
                    // no raw data files are defined
                    prev.add(assayNode);
                }
                for (SDRFNode prevNode : prev) {
                    connect(prevNode, current, PROCESSED_AND_MATRIX_FILES, fileColumn.getFileRef(fileName));
                }
                next.add(current);
                prev = next;
            }
        }
    }

    private <T extends SDRFNode> SDRFNode createDataFileNode(String fileName, Class<T> clazz) {
        if (fileName == null) {
            return createFakeNode(clazz);
        }
        T node = getNode(clazz, fileName);
        if (node != null) {
            return node;
        }
        return createNode(clazz, fileName);
    }

    private TermSource ensureTermSource(TermSource termSource) {
        usedTermSources.add(termSource);
        return termSource;
    }

    private Collection<FileColumn> getSortedFileColumns() {
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
            if (attribute.getType().isCharacteristicOrFactorValue()) {
                CharacteristicsAttribute attr = new CharacteristicsAttribute();
                attr.type = attribute.getName();
                attribute.getValueType().visit(AttributeValueTypeVisitor.visitCharacteristic(attr));
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
        return str == null || str.trim().isEmpty() ? "" : escape(str);
    }

    private static String notNull(Collection<String> collection) {
        return escape(on(",").join(collection));
    }

    private static String escape(String str) {
        return "\"" + str.replaceAll("\"", "\\\\\"") + "\"";
    }

    private static class AttributeValueTypeVisitor implements AttributeValueType.Visitor {

        private final FactorValueOrCharacteristicAttribute attribute;

        private AttributeValueTypeVisitor(FactorValueOrCharacteristicAttribute attribute) {
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
            this.attribute.setUnit(unitAttribute);
        }

        @Override
        public void visitTextValueType(TextAttributeValueType valueType) {
        }

        @Override
        public void visitTermValueType(TermAttributeValueType valueType) {
            attribute.setType(valueType.getBranch().getLabel());
        }

        public static AttributeValueType.Visitor visitCharacteristic(final CharacteristicsAttribute attribute) {
            return new AttributeValueTypeVisitor(new FactorValueOrCharacteristicAttribute() {
                @Override
                public void setUnit(UnitAttribute unitAttribute) {
                    attribute.unit = unitAttribute;
                }

                @Override
                public void setType(String label) {
                    attribute.type = label;
                }
            });
        }

        public static AttributeValueType.Visitor visitFactorValue(final FactorValueAttribute attribute) {
            return new AttributeValueTypeVisitor(new FactorValueOrCharacteristicAttribute() {
                @Override
                public void setUnit(UnitAttribute unitAttribute) {
                    attribute.unit = unitAttribute;
                }

                @Override
                public void setType(String label) {
                    attribute.type = label;
                }
            });
        }
    }

    private abstract static class FactorValueOrCharacteristicAttribute {

        public abstract void setUnit(UnitAttribute unitAttribute);

        public abstract void setType(String label);
    }
}
