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
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.IdfData;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Info;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Person;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.*;
import uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.idf.LimpopoIdfDataProxy;
import uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.sdrf.LimpopoBasedSdrfGraph;
import uk.ac.ebi.fg.annotare2.submissionmodel.*;

import javax.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Queues.newArrayDeque;
import static com.google.common.collect.Sets.newHashSet;

/**
 * @author Olga Melnichuk
 */
public class MageTab2ExperimentConverter {

    private static final Logger log = LoggerFactory.getLogger(MageTab2ExperimentConverter.class);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private Experiment exp;

    private Map<SdrfGraphNode, Object> cache;

    public static Experiment convert(MAGETABInvestigation inv) throws UnsupportedGraphLayoutException {
        return convert(inv.IDF, inv.SDRF);
    }

    public static Experiment convert(IDF idf, SDRF sdrf) throws UnsupportedGraphLayoutException {
        return (new MageTab2ExperimentConverter()).buildExperiment(idf, sdrf);
    }

    private Experiment buildExperiment(IDF idf, SDRF sdrf) throws UnsupportedGraphLayoutException {
        cache = newHashMap();
        exp = new Experiment(Maps.<String, String>newHashMap());
        IdfData idfData = new LimpopoIdfDataProxy(idf);
        fillInIdfData(idfData);
        fillInSdrfData(new LimpopoBasedSdrfGraph(sdrf, idfData));
        return exp;
    }

    private void fillInIdfData(IdfData idfData) {
        Info expInfo = idfData.getInfo();
        exp.setAccession(expInfo.getAccession().getValue());
        exp.setTitle(expInfo.getTitle().getValue());
        exp.setDescription(expInfo.getExperimentDescription().getValue());
        exp.setExperimentDate(parseDate(expInfo.getDateOfExperiment().getValue()));
        exp.setPublicReleaseDate(parseDate(expInfo.getPublicReleaseDate().getValue()));

        for (Person p : idfData.getContacts()) {
            Contact contact = exp.createContact();
            contact.setFirstName(p.getFirstName().getValue());
            contact.setLastName(p.getLastName().getValue());
            contact.setMidInitials(p.getMidInitials().getValue());
            contact.setEmail(p.getEmail().getValue());
            contact.setFax(p.getFax().getValue());
            contact.setPhone(p.getPhone().getValue());
            contact.setAffiliation(p.getAffiliation().getValue());
            contact.setAddress(p.getAddress().getValue());
            //TODO add roles to contact
        }

        for (uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Publication p : idfData.getPublications()) {
            Publication publ = exp.createPublication(new Publication());
            publ.setTitle(p.getTitle().getValue());
            publ.setPubMedId(p.getPubMedId().getValue());
            publ.setAuthors(p.getAuthorList().getValue());
            // TODO publ.setStaus(p.getStatus())
        }
    }

    private void fillInSdrfData(SdrfGraph sdrfGraph) throws UnsupportedGraphLayoutException {
        Set<NodeToVisit> visited = newHashSet();
        Queue<NodeToVisit> queue = newArrayDeque();
        queue.addAll(
                Collections2.transform(sdrfGraph.getRootNodes(), new Function<SdrfGraphNode, NodeToVisit>() {
                    @Nullable
                    @Override
                    public NodeToVisit apply(@Nullable SdrfGraphNode input) {
                        return new NodeToVisit(input, 0);
                    }
                }));

        while (!queue.isEmpty()) {
            NodeToVisit node = queue.poll();
            if (visited.contains(node)) {
                continue;
            }
            visited.add(node);
            int id = visit(node.getParentId(), node.getNode());
            for (SdrfGraphNode n : node.getNode().getChildNodes()) {
                queue.add(new NodeToVisit(n, id));
            }
        }
    }

    private int visit(int parentId, SdrfGraphNode node) throws UnsupportedGraphLayoutException {
        //TODO use visitor ?
        if (node instanceof SdrfSourceNode) {
            return visitNode(parentId, (SdrfSourceNode) node);
        } else if (node instanceof SdrfSampleNode) {
            return visitNode(parentId, (SdrfSampleNode) node);
        } else if (node instanceof SdrfExtractNode) {
            return visitNode(parentId, (SdrfExtractNode) node);
        } else if (node instanceof SdrfLabeledExtractNode) {
            return visitNode(parentId, (SdrfLabeledExtractNode) node);
        } else if (node instanceof SdrfAssayNode) {
            return visitNode(parentId, (SdrfAssayNode) node);
        } else if (node instanceof SdrfArrayDataNode) {
            return visitNode(parentId, (SdrfArrayDataNode) node);
        } else if (node instanceof SdrfScanNode) {
            return visitNode(parentId, (SdrfScanNode) node);
        } else if (node instanceof SdrfProtocolNode) {
            //TODO add protocols to experiment
            return parentId;
        }
        throw new UnsupportedGraphLayoutException("Unsupported node type: " + node.getClass());
    }

    private int visitNode(int parentId, SdrfScanNode scanNode) throws UnsupportedGraphLayoutException {
        Object obj = cache.get(scanNode);
        Scan scan;
        if (obj == null) {
            scan = exp.createScan();
            scan.setName(scanNode.getName());
            //TODO set other attributes
            cache.put(scanNode, scan);
        } else {
            scan = (Scan) obj;
        }
        if (parentId > 0) {
            Assay assay = exp.getAssay(parentId);
            if (assay == null) {
                throw new UnsupportedGraphLayoutException("scan attached to non assay node");
            }
            assay.addScan(scan);
        }
        return 0;
    }

    private int visitNode(int parentId, SdrfArrayDataNode arrayDataNode) throws UnsupportedGraphLayoutException {
        Object obj = cache.get(arrayDataNode);
        ArrayDataFile dataFile;
        if (obj == null) {
            dataFile = exp.createArrayDataFile();
            dataFile.setName(arrayDataNode.getName());
            //TODO set other attributes
            cache.put(arrayDataNode, dataFile);
        } else {
            dataFile = (ArrayDataFile) obj;
        }
        if (parentId > 0) {
            Assay assay = exp.getAssay(parentId);
            if (assay == null) {
                throw new UnsupportedGraphLayoutException("array data file attached to a non assay node");
            }
            assay.addArrayDataFile(dataFile);
        }
        return dataFile.getId();
    }

    private int visitNode(int parentId, SdrfAssayNode assayNode) throws UnsupportedGraphLayoutException {
        Object obj = cache.get(assayNode);
        Assay assay;
        if (obj == null) {
            assay = exp.createAssay();
            assay.setName(assayNode.getName());
            //TODO set other attributes
            cache.put(assayNode, assay);
        } else {
            assay = (Assay) obj;
        }
        if (parentId > 0) {
            LabeledExtract labeledExtract = exp.getLabeledExtract(parentId);
            if (labeledExtract == null) {
                Extract extract = exp.getExtract(parentId);
                if (extract == null) {
                    throw new UnsupportedGraphLayoutException("assay attached neither to labeled extract nor extract");
                } else {
                    extract.addAssay(assay);
                }

            } else {
                labeledExtract.addAssay(assay);
            }
        }
        return assay.getId();
    }

    private int visitNode(int parentId, SdrfLabeledExtractNode labeledExtractNode) throws UnsupportedGraphLayoutException {
        Object obj = cache.get(labeledExtractNode);
        LabeledExtract labeledExtract;
        if (obj == null) {
            labeledExtract = exp.createLabeledExtract();
            labeledExtract.setName(labeledExtractNode.getName());
            labeledExtract.setLabel(labeledExtractNode.getLabel().getValue());
            //TODO set other attributes
            cache.put(labeledExtractNode, labeledExtract);
        } else {
            labeledExtract = (LabeledExtract) obj;
        }
        if (parentId > 0) {
            Extract extract = exp.getExtract(parentId);
            if (extract == null) {
                throw new UnsupportedGraphLayoutException("labeled extract attached to a non extract node");
            }
            extract.addLabeledExtract(labeledExtract);
        }
        return labeledExtract.getId();
    }

    private int visitNode(int parentId, SdrfExtractNode extractNode) throws UnsupportedGraphLayoutException {
        Object obj = cache.get(extractNode);
        Extract extract;
        if (obj == null) {
            extract = exp.createExtract();
            extract.setName(extractNode.getName());
            //TODO set other attributes
            cache.put(extractNode, extract);
        } else {
            extract = (Extract) obj;
        }
        if (parentId > 0) {
            Sample sample = exp.getSample(parentId);
            if (sample == null) {
                Source source = exp.getSource(parentId);
                if (source == null) {
                    throw new UnsupportedGraphLayoutException("extract attached neither to sample nor source");
                } else {
                    source.addExtract(extract);
                }
            } else {
                sample.addExtract(extract);
            }
        }
        return extract.getId();
    }

    private int visitNode(int parentId, SdrfSampleNode sampleNode) throws UnsupportedGraphLayoutException {
        Object obj = cache.get(sampleNode);
        Sample sample;
        if (obj == null) {
            sample = exp.createSample();
            sample.setName(sampleNode.getName());
            //TODO set other attributes
            cache.put(sampleNode, obj);
        } else {
            sample = (Sample) obj;
        }
        if (parentId > 0) {
            Source source = exp.getSource(parentId);
            if (source == null) {
                throw new UnsupportedGraphLayoutException("Sample attached not to a source");
            }
            source.addSample(sample);
        }
        return sample.getId();
    }

    private int visitNode(int parentId, SdrfSourceNode sourceNode) throws UnsupportedGraphLayoutException {
        if (parentId > 0) {
            throw new UnsupportedGraphLayoutException("Source must be a root node");
        }
        Source source = exp.createSource();
        source.setName(sourceNode.getName());
        //TODO set other attributes
        return source.getId();
    }

    private static Date parseDate(String value) {
        try {
            return isNullOrEmpty(value) ? null : DATE_FORMAT.parse(value);
        } catch (ParseException e) {
            log.error("can't parse date string: " + value);
            return null;
        }
    }

    private static class NodeToVisit {
        private final int parentId;
        private final SdrfGraphNode node;

        private NodeToVisit(SdrfGraphNode node, int parentId) {
            this.node = node;
            this.parentId = parentId;
        }

        private SdrfGraphNode getNode() {
            return node;
        }

        private int getParentId() {
            return parentId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NodeToVisit that = (NodeToVisit) o;

            if (parentId != that.parentId) return false;
            if (node != null ? !node.equals(that.node) : that.node != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = parentId;
            result = 31 * result + (node != null ? node.hashCode() : 0);
            return result;
        }
    }

}
