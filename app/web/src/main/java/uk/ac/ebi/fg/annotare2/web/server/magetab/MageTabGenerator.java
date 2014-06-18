/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.server.magetab;

import com.google.common.base.Function;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.graph.Node;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.*;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.*;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.fg.annotare2.submission.model.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Ordering.natural;
import static java.util.Collections.emptyMap;
import static uk.ac.ebi.fg.annotare2.submission.model.TermSource.EFO_TERM_SOURCE;
import static uk.ac.ebi.fg.annotare2.web.server.magetab.MageTabUtils.formatDate;

/**
 * @author Olga Melnichuk
 */
public class MageTabGenerator {

    public static String replaceAllAssayNameValues(String str) {
        return AssayNameValue.replaceAll(str);
    }

    public static String replaceAllUnassignedValues(String str) {
        return UnassignedValue.replaceAll(str);
    }

    public static class UnassignedValue {
        private static final String TEMPLATE = "__UNASSIGNED__@[ID]";
        private static final String PATTERN = TEMPLATE.replace("[ID]", "\\d+");

        private int id = 1;

        public String next() {
            return TEMPLATE.replace("[ID]", Integer.toString(id++));
        }

        public static String replaceAll(String string) {
            return string.replaceAll(PATTERN, "");
        }
    }

    public static class AssayNameValue {
        private static final String TEMPLATE = "__ASSAY_NAME__([FILE_NAME])__@[ID]";
        private static final Pattern PATTERN = Pattern.compile(
                TEMPLATE.replace("([FILE_NAME])", "\\((.*)\\)")
                        .replace("[ID]", "\\d+")
        );

        private int id = 1;

        public String next(String fileName) {
            return TEMPLATE.replace("[ID]", Integer.toString(id++))
                    .replace("[FILE_NAME]", fileName);
        }

        public static String replaceAll(String string) {
            Matcher matcher = PATTERN.matcher(string);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, matcher.group(1));
            }
            matcher.appendTail(sb);
            return sb.toString();
        }
    }

    private final ExperimentProfile exp;

    private final Map<String, SDRFNode> nodeCache = new HashMap<String, SDRFNode>();
    private final Set<TermSource> usedTermSources = new HashSet<TermSource>();

    private UnassignedValue unassignedValue;
    private AssayNameValue assayNameValue;

    public MageTabGenerator(ExperimentProfile exp) {
        this.exp = exp;
    }

    public MAGETABInvestigation generate() throws ParseException {
        nodeCache.clear();

        unassignedValue = new UnassignedValue();
        assayNameValue = new AssayNameValue();

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

        idf.investigationTitle = nullOrAllWhitespaceToEmpty(exp.getTitle());
        idf.experimentDescription = nullOrAllWhitespaceToEmpty(exp.getDescription());
        idf.publicReleaseDate = nullOrAllWhitespaceToEmpty(formatDate(exp.getPublicReleaseDate()));
        idf.dateOfExperiment = nullOrAllWhitespaceToEmpty(formatDate(exp.getExperimentDate()));
        idf.accession = nullOrAllWhitespaceToEmpty(exp.getAccession());

        for (OntologyTerm term : exp.getExperimentalDesigns()) {
            idf.experimentalDesign.add(nullOrAllWhitespaceToEmpty(term.getLabel()));
            idf.experimentalDesignTermAccession.add(nullOrAllWhitespaceToEmpty(term.getAccession()));
            idf.experimentalDesignTermSourceREF.add(ensureTermSource(EFO_TERM_SOURCE).getName());
        }

        for (Contact contact : exp.getContacts()) {
            idf.personFirstName.add(nullOrAllWhitespaceToEmpty(contact.getFirstName()));
            idf.personLastName.add(nullOrAllWhitespaceToEmpty(contact.getLastName()));
            idf.personMidInitials.add(nullOrAllWhitespaceToEmpty(contact.getMidInitials()));
            idf.personEmail.add(nullOrAllWhitespaceToEmpty(contact.getEmail()));
            idf.personPhone.add(nullOrAllWhitespaceToEmpty(contact.getPhone()));
            idf.personFax.add(nullOrAllWhitespaceToEmpty(contact.getFax()));
            idf.personAddress.add(nullOrAllWhitespaceToEmpty(contact.getAddress()));
            idf.personAffiliation.add(nullOrAllWhitespaceToEmpty(contact.getAffiliation()));
            idf.personRoles.add(joinCollection(contact.getRoles(), ";"));
        }

        for (Publication publication : exp.getPublications()) {
            idf.publicationTitle.add(nullOrAllWhitespaceToEmpty(publication.getTitle()));
            idf.publicationAuthorList.add(nullOrAllWhitespaceToEmpty(publication.getAuthors()));
            idf.pubMedId.add(nullOrAllWhitespaceToEmpty(publication.getPubMedId()));
            idf.publicationDOI.add(nullOrAllWhitespaceToEmpty(publication.getDoi()));
            OntologyTerm status = publication.getStatus();
            idf.publicationStatus.add(nullOrAllWhitespaceToEmpty(status == null ? null : status.getLabel()));
            idf.publicationStatusTermAccession.add(nullOrAllWhitespaceToEmpty(status == null ? null : status.getAccession()));
            idf.publicationStatusTermSourceREF.add(nullOrAllWhitespaceToEmpty(status == null ? null : ensureTermSource(EFO_TERM_SOURCE).getName()));
        }

        for (Protocol protocol : exp.getProtocols()) {
            if (protocol.isAssigned()) {
                idf.protocolName.add(nullOrAllWhitespaceToEmpty(protocol.getName()));
                idf.protocolDescription.add(nullOrAllWhitespaceToEmpty(protocol.getDescription()));
                idf.protocolType.add(nullOrAllWhitespaceToEmpty(protocol.getType().getLabel()));
                idf.protocolTermAccession.add(nullOrAllWhitespaceToEmpty(protocol.getType().getAccession()));
                idf.protocolTermSourceREF.add(ensureTermSource(EFO_TERM_SOURCE).getName());
                idf.protocolHardware.add(nullOrAllWhitespaceToEmpty(protocol.getHardware()));
                idf.protocolSoftware.add(nullOrAllWhitespaceToEmpty(protocol.getSoftware()));
            }
        }

        for (SampleAttribute attribute : exp.getSampleAttributes()) {
            if (!attribute.getType().isFactorValue()) {
                continue;
            }
            idf.experimentalFactorName.add(nullOrAllWhitespaceToEmpty(getName(attribute)));
            OntologyTerm term = attribute.getTerm();
            if (term != null) {
                idf.experimentalFactorType.add(nullOrAllWhitespaceToEmpty(term.getLabel()));
                idf.experimentalFactorTermAccession.add(nullOrAllWhitespaceToEmpty(term.getAccession()));
                idf.experimentalFactorTermSourceREF.add(nullOrAllWhitespaceToEmpty(ensureTermSource(EFO_TERM_SOURCE).getName()));
            } else {
                idf.experimentalFactorType.add(nullOrAllWhitespaceToEmpty(getName(attribute)));
                idf.experimentalFactorTermAccession.add("");
                idf.experimentalFactorTermSourceREF.add("");
            }
        }
    }

    private void addTermSources(IDF idf) {
        for (TermSource termSource : usedTermSources) {
            idf.termSourceName.add(nullOrAllWhitespaceToEmpty(termSource.getName()));
            idf.termSourceVersion.add(nullOrAllWhitespaceToEmpty(termSource.getVersion()));
            idf.termSourceFile.add(nullOrAllWhitespaceToEmpty(termSource.getUrl()));
        }
    }

    private String nodeId(Class<?> clazz, String name) {
        return clazz + "@" + name;
    }

    private <T extends SDRFNode> T createFakeNode(Class<T> clazz) {
        return createNode(clazz, unassignedValue.next());
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

    @SuppressWarnings("unchecked")
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
            assayLayer = generateMicroarrayAssayNodes(labeledExtractLayer);
        } else {
            assayLayer = generateSeqAssayNodes(extractLayer);
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
            Collection<Protocol> protocols = exp.getProtocols(sample);
            if (extracts.isEmpty()) {
                layer.put(fakeId--, createExtractNode(null, sampleNode, protocols));
            }
            for (Extract extract : extracts) {
                layer.put(extract.getId(), createExtractNode(extract, sampleNode, protocols));
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
            Collection<Protocol> protocols = exp.getProtocols(extract);
            if (labeledExtracts.isEmpty()) {
                layer.put("" + (fakeId--), createLabeledExtractNode(null, extractNode, protocols));
            }
            for (LabeledExtract labeledExtract : labeledExtracts) {
                layer.put(labeledExtract.getId(), createLabeledExtractNode(labeledExtract, extractNode, protocols));
            }
        }
        return layer;
    }

    private Map<String, SDRFNode> generateMicroarrayAssayNodes(Map<String, SDRFNode> labeledExtractLayer) {
        // no labeled extracts supplied? no assays can be generated
        if (labeledExtractLayer.isEmpty()) {
            return emptyMap();
        }

        // no files uploaded? no assays can be generated
        if (exp.getFileColumns().isEmpty()) {
            return emptyMap();
        }

        Collection<FileColumn> fileColumns = exp.getFileColumns(FileType.RAW_FILE);
        if (fileColumns.isEmpty()) {
            fileColumns = exp.getFileColumns(FileType.PROCESSED_FILE);
        }
        //if (fileColumns.isEmpty()) {
        //    fileColumns =
        //}


        Map<String, SDRFNode> layer = new LinkedHashMap<String, SDRFNode>();
        int fakeId = -1;
        for (String labeledExtractId : labeledExtractLayer.keySet()) {
            LabeledExtract labeledExtract = exp.getLabeledExtract(labeledExtractId);

            SDRFNode labeledExtractNode = labeledExtractLayer.get(labeledExtractId);
            Collection<Protocol> protocols = exp.getProtocols(labeledExtract);

            FileRef file = (labeledExtract == null) ? null : fileColumns.iterator().next().getFileRef(labeledExtract.getId());
            if (null != file) {
                layer.put(labeledExtract.getId(),
                        createAssayNode(labeledExtract,
                                assayNameValue.next(removeExtension(file.getName())),
                                labeledExtractNode,
                                protocols
                        )
                );
            } else {
                layer.put("" + (fakeId--), createAssayNode(null, "", labeledExtractNode, protocols));
            }
        }
        return layer;
    }

    //private FileColumn getFirstFileColumn() {
    //    Collection<FileColumn> columns = sortFileColumns(
    //            exp.getFileColumns()
    //    );
    //    return columns.isEmpty() ? null : columns.iterator().next();
    //}

    private String removeExtension(String fileName) {
        return (null != fileName ? fileName.replaceAll("^(.+)[.][^.]*$", "$1") : null);
    }

    private Map<String, SDRFNode> generateSeqAssayNodes(Map<Integer, SDRFNode> extractLayer) {
        if (extractLayer.isEmpty()) {
            return emptyMap();
        }

        Map<String, SDRFNode> layer = new LinkedHashMap<String, SDRFNode>();
        int fakeId = -1;
        for (Integer extractId : extractLayer.keySet()) {
            Extract extract = exp.getExtract(extractId);
            SDRFNode extractNode = extractLayer.get(extractId);
            Collection<Protocol> protocols = exp.getProtocols(extract);
            if (null == extract) {
                layer.put("" + (fakeId--), createAssayNode(null, "", extractNode, protocols));
            } else {

                layer.put(
                        "" + extract.getId(),
                        createAssayNode(new LabeledExtract(extract), extract.getName(), extractNode, protocols)
                );
            }
        }
        return layer;
    }

    private void generateDataFileNodes(Map<String, SDRFNode> assayLayer) {
        if (assayLayer.isEmpty() || exp.getFileColumns().isEmpty()) {
            return;
        }
        for (String labeledExtractId : assayLayer.keySet()) {
            LabeledExtract labeledExtract = exp.getLabeledExtract(labeledExtractId);
            SDRFNode assayNode = assayLayer.get(labeledExtractId);
            createFileNodes(labeledExtract, assayNode);
        }
    }

    private void connect(SDRFNode source, SDRFNode destination, Collection<Protocol> protocols) {
        SDRFNode prev = source;
        for (Protocol protocol : protocols) {
            // protocol node name must be unique
            String nodeName = prev.getNodeName() + ":" + protocol.getId() + (protocol.isAssigned() ? "" : "F");
            ProtocolApplicationNode protocolNode = getNode(ProtocolApplicationNode.class, nodeName);
            if (protocolNode == null) {
                if (protocol.isAssigned()) {
                    protocolNode = createNode(ProtocolApplicationNode.class, nodeName);
                    protocolNode.protocol = protocol.getName();
                    if (protocol.hasPerformer()) {
                        PerformerAttribute attr = new PerformerAttribute();
                        attr.setAttributeValue(protocol.getPerformer());
                        protocolNode.performer = attr;
                    }
                } else {
                    protocolNode = createFakeNode(ProtocolApplicationNode.class);
                    protocolNode.protocol = "";
                }
            }
            connect(prev, protocolNode);
            prev = protocolNode;
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
        return sourceNode;
    }

    private ExtractNode createExtractNode(Extract extract, SDRFNode sampleNode, Collection<Protocol> protocols) {
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
                    extractNode.comments.put(getSdrfFriendlyName(attr), Arrays.asList(value));
                }
            }
        }
        connect(sampleNode, extractNode, protocols);
        return extractNode;
    }

    private LabeledExtractNode createLabeledExtractNode(LabeledExtract labeledExtract, SDRFNode extractNode, Collection<Protocol> protocols) {
        LabeledExtractNode labeledExtractNode;
        String label;
        if (labeledExtract == null) {
            labeledExtractNode = createFakeNode(LabeledExtractNode.class);
            label = labeledExtractNode.getNodeName();
        } else {
            labeledExtractNode = getNode(LabeledExtractNode.class, labeledExtract.getName());
            if (labeledExtractNode != null) {
                return labeledExtractNode;
            }
            labeledExtractNode = createNode(LabeledExtractNode.class, labeledExtract.getName());
            label = labeledExtract.getLabel().getName();
        }

        LabelAttribute labelAttribute = new LabelAttribute();
        labelAttribute.setAttributeValue(label);
        labeledExtractNode.label = labelAttribute;

        connect(extractNode, labeledExtractNode, protocols);
        return labeledExtractNode;
    }

    private AssayNode createAssayNode(LabeledExtract labeledExtract, String assayName, SDRFNode prevNode, Collection<Protocol> protocols) {
        AssayNode assayNode;
        if (labeledExtract == null) {
            assayNode = createFakeNode(AssayNode.class);
        } else {
            assayNode = getNode(AssayNode.class, assayName);
            if (assayNode != null) {
                return assayNode;
            }
            assayNode = createNode(AssayNode.class, assayName);
        }

        assayNode.technologyType = createTechnologyTypeAttribute();

        ArrayDesignAttribute arrayDesignAttribute = createArrayDesignAttribute();
        if (arrayDesignAttribute != null) {
            assayNode.arrayDesigns.add(arrayDesignAttribute);
        }

        addFactorValues(assayNode, prevNode);
        connect(prevNode, assayNode, protocols);
        return assayNode;
    }

    private void addFactorValues(AssayNode assayNode, SDRFNode prevNode) {
        Collection<Sample> samples = findSamples(prevNode);
        if (samples.size() > 1) {
            throw new IllegalStateException("Too many samples");
        }
        for (SampleAttribute attribute : exp.getSampleAttributes()) {
            if (!attribute.getType().isFactorValue()) {
                continue;
            }
            for (Sample sample : samples) {
                FactorValueAttribute attr = new FactorValueAttribute();
                attr.type = getName(attribute);
                attr.unit = createUnitAttribute(attribute.getUnits());
                attr.setAttributeValue(sample.getValue(attribute));
                //TODO if attr value is an EFO Term then fill in accession and source REF
                //attr.termAccessionNumber = term.getAccession();
                //attr.termSourceREF = ensureTermSource(TermSource.EFO_TERM_SOURCE).getName();
                assayNode.factorValues.add(attr);
            }
        }
    }

    private TechnologyTypeAttribute createTechnologyTypeAttribute() {
        TechnologyTypeAttribute technologyType = new TechnologyTypeAttribute();
        technologyType.setAttributeValue(
                exp.getType().isMicroarray() ? "array assay" : "sequencing assay");
        return technologyType;
    }

    private ArrayDesignAttribute createArrayDesignAttribute() {
        String arrayDesign = exp.getArrayDesign();
        if (isNullOrEmpty(arrayDesign)) {
            return null;
        }
        ArrayDesignAttribute arrayDesignAttribute = new ArrayDesignAttribute();
        arrayDesignAttribute.setAttributeValue(arrayDesign);
        arrayDesignAttribute.termSourceREF = ensureTermSource(TermSource.ARRAY_EXPRESS_TERM_SOURCE).getName();
        return arrayDesignAttribute;
    }

    private Collection<Sample> findSamples(SDRFNode node) {
        List<SourceNode> sourceNodes = new ArrayList<SourceNode>();
        Queue<Node> nodes = new ArrayDeque<Node>();
        nodes.add(node);
        while (!nodes.isEmpty()) {
            Node next = nodes.poll();
            for (Node parent : next.getParentNodes()) {
                if (parent instanceof SourceNode) {
                    sourceNodes.add((SourceNode) parent);
                } else {
                    nodes.addAll(next.getParentNodes());
                }
            }
        }

        List<Sample> samples = new ArrayList<Sample>();
        for (SourceNode sourceNode : sourceNodes) {
            Sample sample = exp.getSampleByName(sourceNode.getNodeName());
            if (sample != null) {
                samples.add(sample);
            }
        }
        return samples;
    }

    private ScanNode createScanNode(Extract extract, SDRFNode assayNode) {
        ScanNode scanNode;
        if (extract == null) {
            scanNode = createFakeNode(ScanNode.class);
        } else {
            scanNode = getNode(ScanNode.class, extract.getName());
            if (scanNode != null) {
                return scanNode;
            }
            scanNode = createNode(ScanNode.class, extract.getName());
        }

        connect(assayNode, scanNode, Collections.<Protocol>emptyList());
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

    private void createFileNodes(LabeledExtract labeledExtract, SDRFNode assayNode) {
        Collection<FileColumn> fileColumns = sortFileColumns(exp.getFileColumns());

        List<SDRFNode> prev = new ArrayList<SDRFNode>();
        List<SDRFNode> next = new ArrayList<SDRFNode>();
        FileType prevType = null;
        for (FileColumn fileColumn : fileColumns) {
            FileType type = fileColumn.getType();
            FileRef file = (null != labeledExtract) ? fileColumn.getFileRef(labeledExtract.getId()) : null;
            String fileName = (null != file) ? file.getName() : null;
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
            if (FileType.RAW_FILE == type && exp.getType().isSequencing()) {
                ((ArrayDataNode)current).comments.put("MD5", Arrays.asList((null != file) ? file.getHash() : null));
            }
            if (type.isRaw() && (prevType == null || prevType == type)) {
                // always connect raw data files to assays
                connect(assayNode, current, exp.getProtocolsByType(ProtocolSubjectType.ASSAY));
                prev.add(current);
            } else {
                if (prev.isEmpty()) {
                    // no raw data files are defined
                    prev.add(assayNode);
                }
                for (SDRFNode prevNode : prev) {
                    connect(prevNode, current, exp.getProtocolsByType(ProtocolSubjectType.RAW_FILE));
                }
                next.add(current);
                prev = next;
            }
            prevType = type;
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

    private Collection<FileColumn> sortFileColumns(Collection<FileColumn> columns) {
        return natural().onResultOf(new Function<FileColumn, Integer>() {
            @Nullable
            @Override
            public Integer apply(@Nullable FileColumn input) {
                return (null != input && null != input.getType()) ? input.getType().ordinal() : null;
            }
        }).immutableSortedCopy(columns);
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
                attr.type = getName(attribute);
                attr.unit = createUnitAttribute(attribute.getUnits());
                attr.setAttributeValue(sample.getValue(attribute));
                //TODO if value is an EFO term fill in accession and term source
                //attr.termAccessionNumber = term.getAccession();
                //attr.termSourceREF = ensureTermSource(TermSource.EFO_TERM_SOURCE).getName();
                attributes.add(attr);
            }
        }
        return attributes;
    }

    private UnitAttribute createUnitAttribute(OntologyTerm units) {
        if (units == null) {
            return null;
        }
        UnitAttribute attr = new UnitAttribute();
        attr.type = units.getLabel();
        attr.setAttributeValue(units.getLabel());
        attr.termSourceREF = ensureTermSource(TermSource.EFO_TERM_SOURCE).getName();
        attr.termAccessionNumber = units.getAccession();

        return attr;
    }

    private String getName(SampleAttribute attr) {
        OntologyTerm term = attr.getTerm();
        return term != null ? term.getLabel() :
                attr.getName() != null ? attr.getName().toLowerCase() :
                        null;
    }

    private static String nullOrAllWhitespaceToEmpty(String str) {
        return str == null || str.trim().isEmpty() ? "" : str;
    }

    private static String joinCollection(Collection<String> collection, String separator) {
        return on(separator).join(collection);
    }
}
