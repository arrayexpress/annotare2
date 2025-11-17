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

package uk.ac.ebi.fg.annotare2.core.magetab;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.graph.Node;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.ArrayDataMatrixNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.ArrayDataNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.AssayNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.DerivedArrayDataMatrixNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.DerivedArrayDataNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.ExtractNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.LabeledExtractNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.ProtocolApplicationNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.SDRFNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.ScanNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.SourceNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.ArrayDesignAttribute;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.CharacteristicsAttribute;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.FactorValueAttribute;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.LabelAttribute;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.MaterialTypeAttribute;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.PerformerAttribute;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.ProviderAttribute;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.TechnologyTypeAttribute;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.UnitAttribute;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.fg.annotare2.core.components.EfoSearch;
import uk.ac.ebi.fg.annotare2.core.data.ProtocolTypes;
import uk.ac.ebi.fg.annotare2.magetabcheck.efo.EfoTerm;
import uk.ac.ebi.fg.annotare2.submission.model.Contact;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.Extract;
import uk.ac.ebi.fg.annotare2.submission.model.ExtractAttribute;
import uk.ac.ebi.fg.annotare2.submission.model.FileColumn;
import uk.ac.ebi.fg.annotare2.submission.model.FileRef;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;
import uk.ac.ebi.fg.annotare2.submission.model.LabeledExtract;
import uk.ac.ebi.fg.annotare2.submission.model.MultiSets;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.submission.model.Protocol;
import uk.ac.ebi.fg.annotare2.submission.model.ProtocolSubjectType;
import uk.ac.ebi.fg.annotare2.submission.model.Publication;
import uk.ac.ebi.fg.annotare2.submission.model.Sample;
import uk.ac.ebi.fg.annotare2.submission.model.SampleAttribute;
import uk.ac.ebi.fg.annotare2.submission.model.SingleCellExtractAttribute;
import uk.ac.ebi.fg.annotare2.submission.model.TermSource;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Collections.emptyMap;
import static uk.ac.ebi.fg.annotare2.core.magetab.MageTabUtils.fixDate;
import static uk.ac.ebi.fg.annotare2.core.magetab.MageTabUtils.formatDate;

public class MageTabGenerator {

    private static class UnassignedValue {
        private static final String TEMPLATE = "____UNASSIGNED____[SEQ]";
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
        private static final String TEMPLATE = "____[ORIGINAL_NAME]____[SEQ]____";
        private static final Pattern PATTERN = Pattern.compile(
                TEMPLATE.replace("____[ORIGINAL_NAME]____", "____(.+?)____")
                        .replace("[SEQ]", "\\d+")
        );

        private int seq = 1;

        public String next(String name) {
            if (null == name) {
                return null;
            }
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

    private final Set<GenerateOption> options;
    private final String newLine;

    private final EfoSearch efoSearch;

    private List<SampleAttribute> emptyAttributeColumns;

    public enum GenerateOption {
        REPLACE_NEWLINES_WITH_SPACES
    }

    public MageTabGenerator(ExperimentProfile exp, EfoSearch efoSearch, GenerateOption... options) {
        this.efoSearch = efoSearch;

        if (null == protocolTypes) {
            protocolTypes = ProtocolTypes.create();
        }

        this.options = new HashSet<>(Arrays.asList(options));
        if (this.options.contains(GenerateOption.REPLACE_NEWLINES_WITH_SPACES)) {
            newLine = " ";
        } else {
            newLine = System.getProperty("line.separator");
        }
        
        this.exp = exp;
        this.unassignedValue = new UnassignedValue();
        this.uniqueNameValue = new UniqueNameValue();

        emptyAttributeColumns = new ArrayList<>();
        emptyAttributeColumns = findEmptyAttributeColumns();
    }

    public MAGETABInvestigation generate() throws ParseException {
        this.nodeCache.clear();
        this.usedTermSources.clear();

        MAGETABInvestigation inv = new MAGETABInvestigation();

        generateIdf(inv.IDF);
        SDRF sdrf = new SDRF();
        generateSdrf(sdrf);
        inv.SDRFs.put("sdrf", sdrf);
        addTermSources(inv.IDF);

        return inv;
    }

    private String[] relatedAccessionNumberList(String relatedAccessionNumber)
    {
        return relatedAccessionNumber.split(",");
    }

    private void generateIdf(IDF idf) {
        if (!isNullOrEmpty(exp.getAeExperimentType())) {
            idf.addComment("AEExperimentType", exp.getAeExperimentType());
        }

        idf.addComment("TemplateType", exp.getType().getTitle());

        if (exp.getAnonymousReview()) {
            idf.addComment("AEAnonymousReview", "yes");
        }

        if(!isNullOrEmpty(exp.getRelatedAccessionNumber())) {
            List<String> accessionNumbers = Arrays.asList(relatedAccessionNumberList(exp.getRelatedAccessionNumber()));
            for (String accessionNumber:accessionNumbers
                 ) {
                idf.addComment("RelatedExperiment",accessionNumber);
            }
        }

        idf.investigationTitle = convertToIdfFriendly(exp.getTitle(), newLine);
        idf.experimentDescription = convertToIdfFriendly(exp.getDescription(), newLine);
        idf.publicReleaseDate = convertToIdfFriendly(formatDate(fixDate(exp.getPublicReleaseDate())), newLine);
        idf.dateOfExperiment = convertToIdfFriendly(formatDate(fixDate(exp.getExperimentDate())), newLine);
        //TODO: idf.accession = convertToIdfFriendly(exp.getAccession(), newLine);

        for (OntologyTerm term : exp.getExperimentalDesigns()) {
            idf.experimentalDesign.add(convertToIdfFriendly(term.getLabel(), newLine));
            idf.experimentalDesignTermAccession.add(convertToIdfFriendly(term.getAccession(), newLine));
            idf.experimentalDesignTermSourceREF.add(ensureTermSource(TermSource.EFO_TERM_SOURCE).getName());
        }

        for (Contact contact : exp.getContacts()) {
            idf.personFirstName.add(convertToIdfFriendly(contact.getFirstName(), newLine));
            idf.personLastName.add(convertToIdfFriendly(contact.getLastName(), newLine));
            idf.personMidInitials.add(convertToIdfFriendly(contact.getMidInitials(), newLine));
            idf.personEmail.add(convertToIdfFriendly(contact.getEmail(), newLine));
            idf.personPhone.add(convertToIdfFriendly(contact.getPhone(), newLine));
            idf.personFax.add(convertToIdfFriendly(contact.getFax(), newLine));
            idf.personAddress.add(convertToIdfFriendly(contact.getAddress(), newLine));
            idf.personAffiliation.add(convertToIdfFriendly(contact.getAffiliation(), newLine));
            idf.personRoles.add(joinCollection(contact.getRoles(), ";"));
        }

        for (Publication publication : exp.getPublications()) {
            idf.publicationTitle.add(convertToIdfFriendly(publication.getTitle(), newLine));
            idf.publicationAuthorList.add(convertToIdfFriendly(publication.getAuthors(), newLine));
            idf.pubMedId.add(convertToIdfFriendly(publication.getPubMedId(), newLine));
            idf.publicationDOI.add(convertToIdfFriendly(publication.getDoi(), newLine));
            OntologyTerm status = publication.getStatus();
            idf.publicationStatus.add(convertToIdfFriendly(status == null ? null : status.getLabel(), newLine));
            idf.publicationStatusTermAccession.add(convertToIdfFriendly(status == null ? null : status.getAccession(), newLine));
            idf.publicationStatusTermSourceREF.add(convertToIdfFriendly(status == null ? null : ensureTermSource(TermSource.EFO_TERM_SOURCE).getName(), newLine));
        }

        for (Protocol protocol : exp.getProtocols()) {
            if (protocol.isAssigned()) {
                idf.protocolName.add(convertToIdfFriendly(protocol.getName(), newLine));
                idf.protocolDescription.add(convertToIdfFriendly(protocol.getDescription(), newLine));
                idf.protocolType.add(convertToIdfFriendly(protocol.getType().getLabel(), newLine));
                idf.protocolTermAccession.add(convertToIdfFriendly(protocol.getType().getAccession(), newLine));
                idf.protocolTermSourceREF.add(ensureTermSource(TermSource.EFO_TERM_SOURCE).getName());
                idf.protocolHardware.add(convertToIdfFriendly(protocol.getHardware(), newLine));
                idf.protocolSoftware.add(convertToIdfFriendly(protocol.getSoftware(), newLine));
            }
        }

        for (SampleAttribute attribute : exp.getSampleAttributes()) {
            if (!attribute.getType().isFactorValue()) {
                continue;
            }
            idf.experimentalFactorName.add(convertToIdfFriendly(getName(attribute), newLine));
            OntologyTerm term = attribute.getTerm();
            if (term != null) {
                idf.experimentalFactorType.add(convertToIdfFriendly(term.getLabel(), newLine));
                idf.experimentalFactorTermAccession.add(convertToIdfFriendly(term.getAccession(), newLine));
                idf.experimentalFactorTermSourceREF.add(convertToIdfFriendly(ensureTermSource(TermSource.EFO_TERM_SOURCE).getName(), newLine));
            } else {
                idf.experimentalFactorType.add(convertToIdfFriendly(getName(attribute), newLine));
                idf.experimentalFactorTermAccession.add("");
                idf.experimentalFactorTermSourceREF.add("");
            }
        }
    }

    private void addTermSources(IDF idf) {
        for (TermSource termSource : usedTermSources) {
            idf.termSourceName.add(convertToIdfFriendly(termSource.getName(), newLine));
            idf.termSourceVersion.add(convertToIdfFriendly(termSource.getVersion(), newLine));
            idf.termSourceFile.add(convertToIdfFriendly(termSource.getUrl(), newLine));
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
        T node = createNode(clazz, nodeName);
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

    private List<SampleAttribute> findEmptyAttributeColumns() {
        List<SampleAttribute> emptyColumns = new ArrayList<>();

        for (SampleAttribute attribute: exp.getSampleAttributes()){
            boolean flag = true;
            for (Sample sample : exp.getSamples()){
                if(!isNullOrEmpty(sample.getValue(attribute))){
                    flag = false;
                }
            }
            if(flag){
                emptyColumns.add(attribute);
            }
        }

        return emptyColumns;
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
            Integer extractId = labeledExtract != null ? labeledExtract.getExtract().getId() : null;
            SDRFNode leNode = labeledExtractLayer.get(leId);
            Collection<Protocol> leProtocols = exp.getProtocols(labeledExtract);

            String assayName = leNode.getNodeName();
            if (!leIds2assayName.isEmpty()) {
                assayName = leIds2assayName.containsKey(leId) ? leIds2assayName.get(leId) : null;
            }
            SDRFNode assayNode = createAssayNode(assayName, leNode, leProtocols, sdrf, extractId);
            nextLayer.put(leId, assayNode);

            boolean isRawDataPresent = false;
            for (FileColumn fileColumn : fileColumns) {
                FileRef fileRef = fileColumn.getFileRef(leId);
                FileType colType = fileColumn.getType();
                if (!isRawDataPresent && colType.isRaw()) {
                    isRawDataPresent = true;
                }
                //boolean isFirstColumn = fileColumn.equals(fileColumns.iterator().next());

                //List<ProtocolSubjectType> protocolSubjectTypes = new ArrayList<ProtocolSubjectType>();
                //if (isFirstColumn) {
                //    protocolSubjectTypes.add(FILE);
                //}
                //protocolSubjectTypes.add(fileColumn.getType().isRaw() ? RAW_FILE : PROCESSED_FILE);

                Collection<Protocol> protocols = Collections.emptyList();
                if (null != fileRef) {
                    if ((isRawDataPresent && colType.isRaw()) || (!isRawDataPresent && (colType.isProcessed() || colType.isProcessedMatrix()))) {
                        protocols = exp.getProtocols(fileRef, fileColumn.getType().isRaw() ? ProtocolSubjectType.RAW_FILE : ProtocolSubjectType.PROCESSED_FILE, ProtocolSubjectType.FILE);
                    } else {
                        protocols = exp.getProtocols(fileRef, fileColumn.getType().isRaw() ? ProtocolSubjectType.RAW_FILE : ProtocolSubjectType.PROCESSED_FILE);
                    }
                }

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
                SDRFNode fileNode = createFileNode(nextLayer.get(leId), fileColumn.getType(), fileRef, protocols, extractId);
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
            SDRFNode assayNode = createAssayNode(assayName, extractNode, eProtocols, null, extractId);
            nextLayer.put(extractId, assayNode);

            for (FileType fileType : fileTypesInOrder) {
                Set<SDRFNode> sourceNodes = nextLayer.get(extractId);
                Collection<FileColumn> fileColumns = exp.getFileColumns(fileType);
                if (!fileColumns.isEmpty()) {
                    nextLayer.remove(extractId);
                    for (Iterator<FileColumn> it = fileColumns.iterator(); it.hasNext();) {
                        FileColumn fileColumn = it.next();
                        FileRef fileRef = fileColumn.getFileRef(String.valueOf(extractId));

                        if (!fileType.isRaw() || null != fileRef || nextLayer.get(extractId).isEmpty()) {
                            Collection<Protocol> protocols = null != fileRef ?
                                    exp.getProtocols(fileRef, fileColumn.getType().isRaw() ? ProtocolSubjectType.RAW_FILE : ProtocolSubjectType.PROCESSED_FILE) :
                                    Collections.<Protocol>emptyList();

                            SDRFNode fileNode = createFileNode(sourceNodes, fileColumn.getType(), fileRef, protocols, extractId);
                            nextLayer.put(extractId, fileNode);
                            if (!fileType.isRaw() && it.hasNext()) {
                                sourceNodes = nextLayer.get(extractId);
                                nextLayer.remove(extractId);
                            }
                        }
                    }
                }
            }
        }
    }

    private void connect(SDRFNode source, SDRFNode destination, Collection<Protocol> protocols, Integer extractId) {
        SDRFNode prev = source;
        if(protocols.isEmpty()){
            ProtocolApplicationNode protocolNode = getOrCreateNode(ProtocolApplicationNode.class, null);
            connect(prev, protocolNode);
            prev = protocolNode;
        } else {
            Collection<Protocol> orderedProtocols =
                    Ordering.natural().onResultOf(new Function<Protocol, Integer>() {
                        @Override
                        public Integer apply(Protocol protocol) {
                            return protocolTypes.getPrecedence(protocol.getType().getAccession());
                        }
                    }).sortedCopy(protocols);

            for (Protocol protocol : orderedProtocols) {
                // protocol node name must be unique
                String nodeName = prev.getClass().getSimpleName() + ":" +
                        (destination instanceof LabeledExtractNode ? destination.getNodeName() : prev.getNodeName()) +
                        ":" + protocol.getId() +
                        (protocol.isAssigned() ? "" : "F");
                if (protocol.isAssigned()) {
                    ProtocolApplicationNode protocolNode = createNode(ProtocolApplicationNode.class, nodeName);
                    protocolNode.protocol = protocol.getName();
                    if (protocol.hasPerformer()) {
                        PerformerAttribute attr = new PerformerAttribute();
                        attr.setAttributeValue(protocol.getPerformer());
                        protocolNode.performer = attr;
                    }
                    protocolNode.setExtractId(extractId);
                    connect(prev, protocolNode);
                    prev = protocolNode;
                }
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
                    extractNode.comments.put(attr.getName(), Arrays.asList(value));
                }
            }

            for (SingleCellExtractAttribute attr : SingleCellExtractAttribute.values()) {
                String value = extract.getSingleCellAttributeValue(attr);
                if (!isNullOrEmpty(value)) {
                    extractNode.comments.put(attr.getName().replace("_"," ").toLowerCase(), Arrays.asList(value));
                }
            }
        }
        connect(sampleNode, extractNode, protocols, extract.getId());
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
        Integer extractId = labeledExtract != null ? labeledExtract.getExtract().getId() : null;
        connect(extractNode, labeledExtractNode, protocols, extractId);
        return labeledExtractNode;
    }

    private AssayNode createAssayNode(String assayName, SDRFNode prevNode, Collection<Protocol> protocols, SDRF sdrf, Integer extractId) {
        AssayNode assayNode;
        if (!isNullOrEmpty(assayName)) {
            assayNode = getNode(AssayNode.class, assayName, extractId);
            if (assayNode != null) {
                addFactorValues(assayNode, prevNode, sdrf);
                connect(prevNode, assayNode, protocols, extractId);
                return assayNode;
            }
            assayNode = createNode(AssayNode.class, assayName);
        } else {
            assayNode = createFakeNode(AssayNode.class);
        }
        assayNode.setExtractId(extractId);
        assayNode.technologyType = createTechnologyTypeAttribute();

        ArrayDesignAttribute arrayDesignAttribute = createArrayDesignAttribute();
        if (arrayDesignAttribute != null) {
            assayNode.arrayDesigns.add(arrayDesignAttribute);
        }

        addFactorValues(assayNode, prevNode, sdrf);
        connect(prevNode, assayNode, protocols, extractId);
        return assayNode;
    }

    private <T extends SDRFNode> T getNode(Class<T> clazz, String name, Integer extractId) {
        T node = (T) nodeCache.get(nodeId(clazz, name));
        if (node == null) {
            return null;
        }
        return Objects.equals(node.getExtractId(), extractId) ? node : null;
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

        connect(assayNode, scanNode, protocols, null);
        return scanNode;
    }

    private SDRFNode createFileNode(SDRFNode prevNode, FileType fileType, FileRef fileRef, Collection<Protocol> protocols, Integer extractId) {
        Set<SDRFNode> prevNodes = new HashSet<SDRFNode>();
        prevNodes.add(prevNode);

        return createFileNode(prevNodes, fileType, fileRef, protocols, extractId);
    }

    private SDRFNode createFileNode(Set<SDRFNode> prevNodes, FileType fileType, FileRef fileRef, Collection<Protocol> protocols, Integer extractId) {
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
                if(exp.getType().isMicroarray() || exp.getType().isMethylationMicroarray()){
                    fileName = uniqueNameValue.next(fileName);
                }
                fileNode = getOrCreateNode(DerivedArrayDataNode.class, fileName);
                break;
            case PROCESSED_MATRIX_FILE:
                if(exp.getType().isMicroarray() || exp.getType().isMethylationMicroarray()){
                    fileName = uniqueNameValue.next(fileName);
                }
                fileNode = getOrCreateNode(DerivedArrayDataMatrixNode.class, fileName);
                break;
            default:
                throw new IllegalStateException("Unsupported file type: " + fileType);
        }
        fileNode.setExtractId(extractId);
        for (SDRFNode prevNode : prevNodes) {
            connect(prevNode, fileNode, protocols, extractId);
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

        for (SampleAttribute attribute : getNonEmptyAttributesColumns()) {
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

    private List<SampleAttribute> getNonEmptyAttributesColumns(){
        List<SampleAttribute> nonEmptyAttributeColumns = new ArrayList<>();

        for (SampleAttribute attribute : exp.getSampleAttributes()){
            if(!emptyAttributeColumns.contains(attribute)){
                nonEmptyAttributeColumns.add(attribute);
            }
        }
        return nonEmptyAttributeColumns;
    }

    private static Set<String> ROOT_UNIT_TYPES = Sets.newHashSet("uo_0000000", "uo_0000045", "uo_0000046");

    private String getUnitType(String unitAccession) {

        EfoTerm unitTerm = efoSearch.searchByAccession(unitAccession);
        if (null != unitTerm) {
            if (!Sets.intersection(new HashSet<String>(unitTerm.getParents()), ROOT_UNIT_TYPES).isEmpty()) {
                return unitTerm.getLabel();
            }

            for (String parentAccession : unitTerm.getParents()) {
                String unitType = getUnitType(parentAccession);
                if (null != unitType) {
                    return unitType;
                }
            }
        }
        return null;
    }

    private UnitAttribute createUnitAttribute(OntologyTerm units) {
        if (null == units) {
            return null;
        }

        String unitType = getUnitType(units.getAccession());
        if (null == unitType) {
            return null;
        }

        UnitAttribute attr = new UnitAttribute();
        attr.type = unitType.replaceFirst("\\s*derived\\s*", "");
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

    private static String convertToIdfFriendly(String str, String newLine) {
        return str == null || str.trim().isEmpty() ? "" : str.replaceAll("(\\r\\n|[\\r\\n])", newLine);
    }

    private static String joinCollection(Collection<String> collection, String separator) {
        return on(separator).join(collection);
    }
}
