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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.*;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.fg.annotare2.configmodel.Contact;
import uk.ac.ebi.fg.annotare2.configmodel.Publication;
import uk.ac.ebi.fg.annotare2.submissionmodel.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Joiner.on;
import static uk.ac.ebi.fg.annotare2.magetab.integration.MageTabUtils.formatDate;

/**
 * @author Olga Melnichuk
 */
public class Experiment2MageTabConverter {

    private static final Logger log = LoggerFactory.getLogger(Experiment2MageTabConverter.class);

    public static MAGETABInvestigation convert(Experiment exp) throws ParseException {
        MAGETABInvestigation inv = new MAGETABInvestigation();
        fillInIdfData(exp, inv.IDF);
        fillInSdrfData(exp, inv.SDRF);
        return inv;
    }

    private static void fillInIdfData(Experiment exp, IDF idf) {
        idf.investigationTitle = fix(exp.getTitle());
        idf.experimentDescription = fix(exp.getDescription());
        idf.publicReleaseDate = fix(formatDate(exp.getPublicReleaseDate()));
        idf.dateOfExperiment = fix(formatDate(exp.getExperimentDate()));
        idf.accession = fix(exp.getAccession());

        for (Contact contact : exp.getContacts()) {
            idf.personFirstName.add(fix(contact.getFirstName()));
            idf.personLastName.add(fix(contact.getLastName()));
            idf.personMidInitials.add(fix(contact.getMidInitials()));
            idf.personEmail.add(fix(contact.getEmail()));
            idf.personPhone.add(fix(contact.getPhone()));
            idf.personFax.add(fix(contact.getFax()));
            idf.personAddress.add(fix(contact.getAddress()));
            idf.personAffiliation.add(fix(contact.getAffiliation()));
            idf.personRoles.add(fix(contact.getRoles()));
        }

        for (Publication publication : exp.getPublications()) {
            idf.publicationTitle.add(fix(publication.getTitle()));
            idf.publicationAuthorList.add(fix(publication.getAuthors()));
            idf.pubMedId.add(fix(publication.getPubMedId()));
            //idf.publicationDOI.add(fix(publication.getPubMedId()));
            //idf.publicationStatus.add(fix(publication.getStatus()));
        }
    }

    private static void fillInSdrfData(Experiment exp, SDRF sdrf) throws ParseException {
        Map<Integer, SDRFNode> map = new HashMap<Integer, SDRFNode>();

        boolean root = true;

        for (Source source : exp.getSources()) {
            SourceNode sourceNode = new SourceNode();
            sourceNode.setNodeName(source.getName());
            map.put(source.getId(), sourceNode);
            sdrf.addNode(sourceNode);
        }

        root = root && exp.getSources().isEmpty();

        for (Sample sample : exp.getSamples()) {
            SampleNode sampleNode = new SampleNode();
            sampleNode.setNodeName(sample.getName());
            map.put(sample.getId(), sampleNode);
            if (root) {
                sdrf.addNode(sampleNode);
            }
        }

        root = root && exp.getSamples().isEmpty();

        for (Extract extract : exp.getExtracts()) {
            ExtractNode extractNode = new ExtractNode();
            extractNode.setNodeName(extract.getName());
            map.put(extract.getId(), extractNode);
            if (root) {
                sdrf.addNode(extractNode);
            }
        }

        root = root && exp.getExtracts().isEmpty();

        for (LabeledExtract labeledExtract : exp.getLabeledExtracts()) {
            LabeledExtractNode labeledExtractNode = new LabeledExtractNode();
            labeledExtractNode.setNodeName(labeledExtract.getName());
            labeledExtract.setLabel(labeledExtract.getLabel());
            map.put(labeledExtract.getId(), labeledExtractNode);
            if (root) {
                sdrf.addNode(labeledExtractNode);
            }
        }

        root = root && exp.getLabeledExtracts().isEmpty();

        for (Assay assay : exp.getAssays()) {
            AssayNode assayNode = new AssayNode();
            assayNode.setNodeName(assay.getName());
            map.put(assay.getId(), assayNode);
            if (root) {
                sdrf.addNode(assayNode);
            }
        }

        root = root && exp.getAssays().isEmpty();

        for (Scan scan : exp.getScans()) {
            ScanNode scanNode = new ScanNode();
            scanNode.setNodeName(scan.getName());
            map.put(scan.getId(), scanNode);
            if (root) {
                sdrf.addNode(scanNode);
            }
        }

        root = root && exp.getScans().isEmpty();

        for (ArrayDataFile arrayDataFile : exp.getArrayDataFiles()) {
            ArrayDataNode arrayDataNode = new ArrayDataNode();
            arrayDataNode.setNodeName(arrayDataFile.getName());
            map.put(arrayDataFile.getId(), arrayDataNode);
            if (root) {
                sdrf.addNode(arrayDataNode);
            }
        }

        for (Source source : exp.getSources()) {
            SDRFNode node = map.get(source.getId());
            for (Sample sample : source.getSamples()) {
                addChildNode(node, map.get(sample.getId()));
            }
            for (Extract extract : source.getExtracts()) {
                addChildNode(node, map.get(extract.getId()));
            }
        }

        for (Sample sample : exp.getSamples()) {
            SDRFNode node = map.get(sample.getId());
            for (Extract extract : sample.getExtracts()) {
                addChildNode(node, map.get(extract.getId()));
            }
        }

        for (Extract extract : exp.getExtracts()) {
            SDRFNode node = map.get(extract.getId());
            for (LabeledExtract labeledExtract : extract.getLabeledExtracts()) {
                addChildNode(node, map.get(labeledExtract.getId()));
            }
            for (Assay assay : extract.getAssays()) {
                addChildNode(node, map.get(assay.getId()));
            }
        }

        for (LabeledExtract labeledExtract : exp.getLabeledExtracts()) {
            SDRFNode node = map.get(labeledExtract.getId());
            for (Assay assay : labeledExtract.getAssays()) {
                addChildNode(node, map.get(assay.getId()));
            }
        }

        for (Assay assay : exp.getAssays()) {
            SDRFNode node = map.get(assay.getId());
            for (Scan scan : assay.getScans()) {
                addChildNode(node, map.get(scan.getId()));
            }
            for (ArrayDataFile arrayDataFile : assay.getArrayDataFiles()) {
                addChildNode(node, map.get(arrayDataFile.getId()));
            }
        }
        //TODO
    }

    private static void addChildNode(SDRFNode node, SDRFNode child) {
        node.addChildNode(child);
        child.addParentNode(node);
    }

    private static String fix(String str) {
        return str == null || str.trim().isEmpty() ? "" : str;
    }

    private static String fix(Collection<String> collection) {
        return on(",").join(collection);
    }

}
