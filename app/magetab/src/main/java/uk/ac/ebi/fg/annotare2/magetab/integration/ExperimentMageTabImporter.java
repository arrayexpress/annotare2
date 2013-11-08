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
import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.graph.Node;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.ExtractNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.LabeledExtractNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.SampleNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.SourceNode;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.IdfData;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Info;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Person;
import uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.idf.LimpopoIdfDataProxy;
import uk.ac.ebi.fg.annotare2.submission.model.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static uk.ac.ebi.fg.annotare2.magetab.integration.MageTabUtils.parseDate;


/**
 * @author Olga Melnichuk
 */
public class ExperimentMageTabImporter {

    private static final Logger log = LoggerFactory.getLogger(ExperimentMageTabImporter.class);

    private final ExperimentProfile exp;
    private final Map<ExtractNode, Extract> extracts = new HashMap<ExtractNode, Extract>();

    public ExperimentMageTabImporter(ExperimentProfileType configType) {
        exp = new ExperimentProfile(configType);
    }

    public ExperimentProfile importFrom(IDF idf, SDRF sdrf) throws ImportExperimentException {
        IdfData idfData = new LimpopoIdfDataProxy(idf);
        importIdfData(idfData);
        importSdrfData(sdrf);
        return exp;
    }

    private void importIdfData(IdfData idfData) {
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
            Publication publ = exp.createPublication();
            publ.setTitle(p.getTitle().getValue());
            publ.setPubMedId(p.getPubMedId().getValue());
            publ.setAuthors(p.getAuthorList().getValue());
            // TODO publ.setStaus(p.getStatus())
        }
    }

    private void importSdrfData(SDRF sdrf) throws ImportExperimentException {
        addSamples(sdrf);
    }

    private void addSamples(SDRF sdrf) throws ImportExperimentException {
        Collection<SourceNode> sources = sdrf.getNodes(SourceNode.class);
        if (sources.isEmpty()) {
            Collection<SampleNode> samples = sdrf.getNodes(SampleNode.class);
            if (samples.isEmpty()) {
                Collection<ExtractNode> extracts = sdrf.getNodes(ExtractNode.class);
                if (extracts.isEmpty()) {
                    throw new ImportExperimentException("Invalid experiment: neither sources nor samples nor extracts found");
                } else {
                    addSampleFromExtracts(extracts);
                }
            } else {
                addSamplesFromSamples(samples);
            }
        } else {
            addSamplesFromSources(sources);
        }
    }

    private void addSamplesFromSamples(Collection<SampleNode> samples) throws ImportExperimentException {
        for (SampleNode node : samples) {
            Sample sample = exp.createSample();
            sample.setName(node.getNodeName());
            //TODO set material type and characteristics
            // assignLabels(config, node);
            addExtracts(sample, findExtractNodes(node));
        }
    }

    private void addSamplesFromSources(Collection<SourceNode> sources) throws ImportExperimentException {
        for (SourceNode node : sources) {
            Sample sample = exp.createSample();
            sample.setName(node.getNodeName());
            //TODO set material type and characteristics
            // assignLabels(config, node);
            addExtracts(sample, findExtractNodes(node));
        }
    }

    private void addSampleFromExtracts(Collection<ExtractNode> extracts) throws ImportExperimentException {
        for (ExtractNode node : extracts) {
            Sample sample = exp.createSample();
            sample.setName(node.getNodeName());
            //TODO set material type and characteristics
            /// assignLabels(config, node);
            addExtracts(sample, findExtractNodes(node));
        }
    }

    private void addExtracts(Sample sample, Collection<ExtractNode> extractNodes) {
        for(ExtractNode node : extractNodes) {
            List<LabeledExtractNode> labeledExtracts = findLabeledExtracts(node);
            Extract extract = extracts.get(node);
            if (extract == null) {
                extract = exp.createExtract(labeledExtracts.isEmpty(), sample);
                extract.setName(node.getNodeName());
                extracts.put(node, extract);
            } else {
                exp.link(sample, extract);
            }
            addLabeledExtracts(extract, labeledExtracts);
        }
    }

    private void addLabeledExtracts(Extract extract, List<LabeledExtractNode> labeledExtractNodes) {
        for(LabeledExtractNode node : labeledExtractNodes) {
            exp.createLabeledExtract(extract, node.label.getAttributeValue());
        }
    }

    private List<ExtractNode> findExtractNodes(Node node) {
        List<ExtractNode> nodes = newArrayList();
        for(Node child : node.getChildNodes()) {
            if (child instanceof ExtractNode) {
                nodes.add((ExtractNode)child);
            } else {
                nodes.addAll(findExtractNodes(child));
            }
        }
        return nodes;
    }

    private List<LabeledExtractNode> findLabeledExtracts(Node node) {
        List<LabeledExtractNode> nodes = newArrayList();
        for (Node child : node.getChildNodes()) {
            if (child instanceof LabeledExtractNode) {
                nodes.add((LabeledExtractNode) child);
            }
        }
        if (!nodes.isEmpty()) {
            return nodes;
        }
        for (Node child : node.getChildNodes()) {
            nodes = findLabeledExtracts(child);
            if (!nodes.isEmpty()) {
                return nodes;
            }
        }
        return emptyList();
    }
}
