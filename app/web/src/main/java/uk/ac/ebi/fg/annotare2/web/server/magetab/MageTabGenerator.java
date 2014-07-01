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
import com.google.common.collect.Ordering;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.graph.Node;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.*;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.*;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.fg.annotare2.submission.model.*;
import uk.ac.ebi.fg.annotare2.web.server.ProtocolTypes;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Collections.emptyMap;
import static uk.ac.ebi.fg.annotare2.submission.model.ProtocolSubjectType.*;
import static uk.ac.ebi.fg.annotare2.submission.model.TermSource.EFO_TERM_SOURCE;
import static uk.ac.ebi.fg.annotare2.web.server.magetab.MageTabUtils.formatDate;

/**
 * @author Olga Melnichuk
 */
public class MageTabGenerator {

    private static class UnassignedValue {
        private static final String TEMPLATE = "__UNASSIGNED__@[SEQ]";
        private static final String PATTERN = TEMPLATE.replace("[SEQ]", "\\d+");

        private int seq = 1;

        public String next() {
            return TEMPLATE.replace("[SEQ]", Integer.toString(seq++));
        }

        public static String replaceAll(String string) {
            return string.replaceAll(PATTERN, "");
        }
    }

    public static String restoreAllUnassignedValues(String str) {
        return UnassignedValue.replaceAll(str);
    }

    private static class UniqueNameValue {
        private static final String TEMPLATE = "__UNIQUE_NAME__([ORIGINAL_NAME])__@[SEQ]";
        private static final Pattern PATTERN = Pattern.compile(
                TEMPLATE.replace("([ORIGINAL_NAME])", "\\((.*)\\)")
                        .replace("[SEQ]", "\\d+")
        );

        private int seq = 1;

        public String next(String name) {
            return TEMPLATE.replace("[SEQ]", Integer.toString(seq++))
                    .replace("[ORIGINAL_NAME]", name);
        }

        public static String replaceAll(String string) {
            Matcher matcher = PATTERN.matcher(string);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, matcher.group(1).replaceAll("([\\\\$])", "\\\\$1"));
            }
            matcher.appendTail(sb);
            return sb.toString();
        }
    }

    public static String restoreOriginalNameValues(String str) {
        return UniqueNameValue.replaceAll(str);
    }

    private final Map<String, SDRFNode> nodeCache = new HashMap<String, SDRFNode>();
    private final Set<TermSource> usedTermSources = new HashSet<TermSource>();

    private static ProtocolTypes protocolTypes = null;

    private final UnassignedValue unassignedValue;
    private final UniqueNameValue uniqueNameValue;

    private final ExperimentProfile exp;

    public MageTabGenerator(ExperimentProfile exp) {
        if (null == protocolTypes) {
            protocolTypes = ProtocolTypes.create();
        }

        this.exp = exp;
        this.unassignedValue = new UnassignedValue();
        this.uniqueNameValue = new UniqueNameValue();
    }

    public MAGETABInvestigation generate() throws ParseException {
        this.nodeCache.clear();
        this.usedTermSources.clear();

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

    private <T extends SDRFNode> T getOrCreateNode(Class<T> clazz, String nodeName) {
        if (isNullOrEmpty(nodeName)) {
            return createFakeNode(clazz);
        }
        T node = getNode(clazz, nodeName);
        if (null == node) {
            node = createNode(clazz, nodeName);
        }
        return node;
    }

    private void generateSdrf(SDRF sdrf) throws ParseException {
        Map<Integer, SDRFNode> sourceLayer = generateSourceNodes();
        for (SDRFNode node : sourceLayer.values()) {
            sdrf.addNode(node);
        }

        Map<Integer, SDRFNode> extractLayer = generateExtractNodes(sourceLayer);
        if (exp.getType().isMicroarray()) {
            Map<String, SDRFNode> labeledExtractLayer = generateLabeledExtractNodes(extractLayer);
            generateMicroarrayAssayScanAndDataFileNodes(sdrf, labeledExtractLayer);
        } else {
            generateSeqAssayNodesAndDataFileNodes(extractLayer);
        }
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

    private void generateMicroarrayAssayScanAndDataFileNodes(SDRF sdrf, Map<String, SDRFNode> labeledExtractLayer) {
        // no labeled extracts supplied? no assays can be generated
        if (labeledExtractLayer.isEmpty()) {
            return;
        }

        // no files uploaded? no assays can be generated
        Collection<FileColumn> fileColumns = exp.getFileColumns();
        if (fileColumns.isEmpty()) {
            return;
        }

        // create assay name per labeledExtractId map
        Map<String, String> leIds2assayName = new HashMap<String, String>();
        for (FileColumn fileColumn : fileColumns) {
            if (!fileColumn.getType().isFGEM()) {
                for (String leId : labeledExtractLayer.keySet()) {
                    FileRef fileRef = fileColumn.getFileRef(leId);
                    leIds2assayName.put(leId, null != fileRef ? removeExtension(fileRef.getName()) : null);
                }
                break;
            }
        }

        Map<String, SDRFNode> nextLayer = new LinkedHashMap<String, SDRFNode>();

        for (String leId : labeledExtractLayer.keySet()) {
            LabeledExtract labeledExtract = exp.getLabeledExtract(leId);

            SDRFNode leNode = labeledExtractLayer.get(leId);
            Collection<Protocol> leProtocols = exp.getProtocols(labeledExtract);

            String assayName = leNode.getNodeName();
            if (!leIds2assayName.isEmpty()) {
                assayName = leIds2assayName.containsKey(leId) ? leIds2assayName.get(leId) : null;
            }
            SDRFNode assayNode = createAssayNode(assayName, leNode, leProtocols, sdrf);
            nextLayer.put(leId, assayNode);

            for (FileColumn fileColumn : fileColumns) {
                FileRef fileRef = fileColumn.getFileRef(leId);
                boolean isFirstColumn = fileColumn.equals(fileColumns.iterator().next());

                List<ProtocolSubjectType> protocolSubjectTypes = new ArrayList<ProtocolSubjectType>();
                if (isFirstColumn) {
                    protocolSubjectTypes.add(FILE);
                }
                protocolSubjectTypes.add(fileColumn.getType().isRaw() ? RAW_FILE : PROCESSED_FILE);

                Collection<Protocol> protocols = null != fileRef ?
                        exp.getProtocols(fileRef, protocolSubjectTypes.toArray(new ProtocolSubjectType[protocolSubjectTypes.size()])) :
                        Collections.<Protocol>emptyList();

                // for the first column check if there are array scanning protocol(s) defined
                // add scan object if necessary
                //if (isFirstColumn) {
                //    boolean isScanProtocolDefined = Iterables.any(protocols, new Predicate<Protocol>() {
                //        @Override
                //        public boolean apply(@Nullable Protocol protocol) {
                //            return null != protocol && "EFO_0003814".equals(protocol.getType().getAccession());
                //        }
                //    });
                //
                //    if (isScanProtocolDefined) {
                //        nextLayer.put(leId, createScanNode(nextLayer.get(leId), Collections.<Protocol>emptyList()));
                //    }
                //}
                SDRFNode fileNode = createFileNode(nextLayer.get(leId), fileColumn.getType(), fileRef, protocols);
                nextLayer.put(leId, fileNode);
            }
        }
    }

    private String removeExtension(String fileName) {
        return (null != fileName ? fileName.replaceAll("^(.+)[.][^.]*$", "$1") : null);
    }

    private void generateSeqAssayNodesAndDataFileNodes(Map<Integer, SDRFNode> extractLayer) {
        // no extracts supplied? no assays can be generated
        if (extractLayer.isEmpty()) {
            return;
        }

        FileType[] fileTypesInOrder = new FileType[] {FileType.RAW_FILE, FileType.PROCESSED_FILE, FileType.PROCESSED_MATRIX_FILE};
        MultiSets<Integer, SDRFNode> nextLayer = new MultiSets<Integer, SDRFNode>();

        for (Integer extractId : extractLayer.keySet()) {
            Extract extract = exp.getExtract(extractId);

            SDRFNode extractNode = extractLayer.get(extractId);
            Collection<Protocol> eProtocols = exp.getProtocols(extract);

            String assayName = extractNode.getNodeName();
            SDRFNode assayNode = createAssayNode(assayName, extractNode, eProtocols, null);
            nextLayer.put(extractId, assayNode);

            for (FileType fileType : fileTypesInOrder) {
                Set<SDRFNode> sourceNodes = nextLayer.get(extractId);
                nextLayer.remove(extractId);
                for (FileColumn fileColumn : exp.getFileColumns(fileType)) {
                    FileRef fileRef = fileColumn.getFileRef(String.valueOf(extractId));

                    Collection<Protocol> protocols = null != fileRef ?
                            exp.getProtocols(fileRef, fileColumn.getType().isRaw() ? RAW_FILE : PROCESSED_FILE) :
                            Collections.<Protocol>emptyList();

                    SDRFNode fileNode = createFileNode(sourceNodes, fileColumn.getType(), fileRef, protocols);
                    nextLayer.put(extractId, fileNode);
                    if (!fileType.isRaw()) {
                        sourceNodes = nextLayer.get(extractId);
                        nextLayer.remove(extractId);
                    }
                }
            }
        }
    }

    private void connect(SDRFNode source, SDRFNode destination, Collection<Protocol> protocols) {
        SDRFNode prev = source;
        Collection<Protocol> orderedProtocols =
                Ordering.natural().onResultOf(new Function<Protocol, Integer>() {
                    @Override
                    public Integer apply(Protocol protocol) {
                        return protocolTypes.getPrecedence(protocol.getType().getAccession());
                    }
                }).sortedCopy(protocols);

        for (Protocol protocol : orderedProtocols) {
            // protocol node name must be unique
            String nodeName = prev.getNodeName() + ":" + protocol.getId() + (protocol.isAssigned() ? "" : "F");
            ProtocolApplicationNode protocolNode = getNode(ProtocolApplicationNode.class, nodeName);
            if (null == protocolNode) {
                if (protocol.isAssigned()) {
                    protocolNode = createNode(ProtocolApplicationNode.class, nodeName);
                    protocolNode.protocol = protocol.getName();
                    if (protocol.hasPerformer()) {
                        PerformerAttribute attr = new PerformerAttribute();
                        attr.setAttributeValue(protocol.getPerformer());
                        protocolNode.performer = attr;
                    }
                }
            }
            if (null != protocolNode) {
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

    private AssayNode createAssayNode(String assayName, SDRFNode prevNode, Collection<Protocol> protocols, SDRF sdrf) {
        AssayNode assayNode;
        if (!isNullOrEmpty(assayName)) {
            assayNode = getNode(AssayNode.class, assayName);
            if (assayNode != null) {
                addFactorValues(assayNode, prevNode, sdrf);
                connect(prevNode, assayNode, protocols);
                return assayNode;
            }
            assayNode = createNode(AssayNode.class, assayName);
        } else {
            assayNode = createFakeNode(AssayNode.class);
        }

        assayNode.technologyType = createTechnologyTypeAttribute();

        ArrayDesignAttribute arrayDesignAttribute = createArrayDesignAttribute();
        if (arrayDesignAttribute != null) {
            assayNode.arrayDesigns.add(arrayDesignAttribute);
        }

        addFactorValues(assayNode, prevNode, sdrf);
        connect(prevNode, assayNode, protocols);
        return assayNode;
    }

    private void addFactorValues(AssayNode assayNode, SDRFNode prevNode, SDRF sdrf) {
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
                if (prevNode instanceof LabeledExtractNode && null != sdrf) {
                    String label = ((LabeledExtractNode)prevNode).label.getAttributeValue();
                    attr.scannerChannel = sdrf.getChannelNumber(label);
                }
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

    private ScanNode createScanNode(SDRFNode assayNode, Collection<Protocol> protocols) {
        ScanNode scanNode = getOrCreateNode(ScanNode.class, assayNode.getNodeName());
        if (null == scanNode) {
            scanNode = createNode(ScanNode.class, assayNode.getNodeName());
        }

        connect(assayNode, scanNode, protocols);
        return scanNode;
    }

    private SDRFNode createFileNode(SDRFNode prevNode, FileType fileType, FileRef fileRef, Collection<Protocol> protocols) {
        Set<SDRFNode> prevNodes = new HashSet<SDRFNode>();
        prevNodes.add(prevNode);

        return createFileNode(prevNodes, fileType, fileRef, protocols);
    }

    private SDRFNode createFileNode(Set<SDRFNode> prevNodes, FileType fileType, FileRef fileRef, Collection<Protocol> protocols) {
        SDRFNode fileNode;
        String fileName = null != fileRef ? fileRef.getName() : null;

        switch (fileType) {
            case RAW_FILE:
                ArrayDataNode rawFileNode = getOrCreateNode(ArrayDataNode.class, fileName);

                if (exp.getType().isSequencing() && rawFileNode.comments.isEmpty()) {
                    rawFileNode.comments.put("MD5", Arrays.asList(null != fileRef ? fileRef.getHash() : null));
                }
                fileNode = rawFileNode;
                break;
            case RAW_MATRIX_FILE:
                fileNode = getOrCreateNode(ArrayDataMatrixNode.class, uniqueNameValue.next(fileName));
                break;
            case PROCESSED_FILE:
                fileNode = getOrCreateNode(DerivedArrayDataNode.class, fileName);
                break;
            case PROCESSED_MATRIX_FILE:
                fileNode = getOrCreateNode(DerivedArrayDataMatrixNode.class, fileName);
                break;
            default:
                throw new IllegalStateException("Unsupported file type: " + fileType);
        }
        for (SDRFNode prevNode : prevNodes) {
            connect(prevNode, fileNode, protocols);
        }
        return fileNode;
    }

    private TermSource ensureTermSource(TermSource termSource) {
        usedTermSources.add(termSource);
        return termSource;
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
