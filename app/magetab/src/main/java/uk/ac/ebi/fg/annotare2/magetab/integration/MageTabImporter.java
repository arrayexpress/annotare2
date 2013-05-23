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
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.*;
import uk.ac.ebi.fg.annotare2.configmodel.Contact;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.configmodel.Publication;
import uk.ac.ebi.fg.annotare2.configmodel.SampleProfile;
import uk.ac.ebi.fg.annotare2.configmodel.SampleProfile;
import uk.ac.ebi.fg.annotare2.configmodel.SampleProfile;
import uk.ac.ebi.fg.annotare2.configmodel.enums.ExperimentConfigType;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.IdfData;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Info;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Person;
import uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.idf.LimpopoIdfDataProxy;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static uk.ac.ebi.fg.annotare2.magetab.integration.MageTabUtils.parseDate;


/**
 * @author Olga Melnichuk
 */
public class MageTabImporter {

    private static final Logger log = LoggerFactory.getLogger(MageTabImporter.class);

    private final ExperimentProfile exp;

    public MageTabImporter(ExperimentConfigType configType) {
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
        addSampleConfigs(sdrf);
    }

    private void addSampleConfigs(SDRF sdrf) throws ImportExperimentException {
        Collection<SampleNode> samples = sdrf.getNodes(SampleNode.class);
        if (samples.isEmpty()) {
            Collection<ExtractNode> extracts = sdrf.getNodes(ExtractNode.class);
            if (extracts.isEmpty()) {
                Collection<SourceNode> sources = sdrf.getNodes(SourceNode.class);
                if (sources.isEmpty()) {
                    throw new ImportExperimentException("Invalid experiment: neither sources nor samples nor extracts found");
                } else {
                    addSampleConfigsFromSources(sources);
                }
            } else {
                addSampleConfigsFromExtracts(extracts);
            }
        } else {
            addSampleConfigsFromSamples(samples);
        }
    }

    private void addSampleConfigsFromSamples(Collection<SampleNode> samples) throws ImportExperimentException {
        for (SampleNode node : samples) {
            SampleProfile config = exp.createSampleConfig();
            config.setName(node.getNodeName());
            //TODO set material type and characteristics
            assignLabels(config, node);
        }
    }

    private void addSampleConfigsFromSources(Collection<SourceNode> sources) throws ImportExperimentException {
        for (SourceNode node : sources) {
            SampleProfile config = exp.createSampleConfig();
            config.setName(node.getNodeName());
            //TODO set material type and characteristics
            assignLabels(config, node);
        }
    }

    private void addSampleConfigsFromExtracts(Collection<ExtractNode> extracts) throws ImportExperimentException {
        for(ExtractNode node : extracts) {
            SampleProfile config = exp.createSampleConfig();
            config.setName(node.getNodeName());
            //TODO set material type and characteristics
            assignLabels(config, node);
        }
    }

    private void assignLabels(SampleProfile config, SDRFNode node) throws ImportExperimentException {
        if (!exp.getType().isMicroarray()) {
            return;
        }
        Collection<LabeledExtractNode> labeledExtracts = findLabeledExtracts(node);
        if (labeledExtracts.isEmpty()) {
            throw new ImportExperimentException("Invalid experiment: no labeled extracts found for '" + config.getName() + "' ");
        }
        for(LabeledExtractNode labeledExtract : labeledExtracts) {
            exp.assignLabel(config, labeledExtract.label.getAttributeValue());
        }
    }

    private List<LabeledExtractNode> findLabeledExtracts(Node node) {
        List<LabeledExtractNode> nodes = newArrayList();
        for (Node child : node.getChildNodes()) {
            if (child instanceof LabeledExtractNode) {
                nodes.add((LabeledExtractNode)child);
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
