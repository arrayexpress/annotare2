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

package uk.ac.ebi.fg.annotare2.submission.transform;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.submission.model.*;

import java.util.Date;
import java.util.HashMap;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertNotNull;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.ONE_COLOR_MICROARRAY;

/**
 * @author Olga Melnichuk
 */
public class SubmissionSerializationTest {

    private static final Logger log = LoggerFactory.getLogger(SubmissionSerializationTest.class);

    @Test
    public void experimentProfileSerializationTest() throws Exception {
        ExperimentProfile profileIn = new ExperimentProfile(ONE_COLOR_MICROARRAY);
        profileIn.setTitle(null);
        profileIn.setDescription("description");
        profileIn.setArrayDesign("AD-1234");
        profileIn.setAeExperimentType("AEExperimentType-123");
        profileIn.setExperimentDate(new Date());
        profileIn.setPublicReleaseDate(new Date());
        profileIn.setExperimentalDesigns(asList(new OntologyTerm("term1", "term1"), new OntologyTerm("term2", "term2")));

        Contact c1 = profileIn.createContact();
        c1.setFirstName("contact1");
        c1.setRoles(asList("role1", "role2"));

        Contact c2 = profileIn.createContact();
        c2.setFirstName("contact2");
        c2.setRoles(asList("role3", "role4"));

        Publication p1 = profileIn.createPublication();
        p1.setTitle("pub1");
        p1.setStatus(new OntologyTerm("ready", "ready"));

        Publication p2 = profileIn.createPublication();
        p2.setTitle("pub2");
        p2.setStatus(new OntologyTerm("in-review", "in-review"));

        Protocol prot1 = profileIn.createProtocol(new OntologyTerm("protocol1", "protocol1"), ProtocolTargetType.EXTRACTS);
        prot1.setName("Name of Protocol1");
        prot1.setDescription("Description of Protocol1");
        prot1.setParameters(asList("param1", "param2"));

        Protocol prot2 = profileIn.createProtocol(new OntologyTerm("protocol2", "protocol2"), ProtocolTargetType.LABELED_EXTRACTS);
        prot2.setName("Name of Protocol2");
        prot2.setDescription("Description of Protocol2");

        Label label1 = profileIn.addLabel("label1");
        Label label2 = profileIn.addLabel("label2");

        final SampleAttribute sa1 = profileIn.createSampleAttribute("template");
        sa1.setName("Sample Attribute 1");
        sa1.setTerm(new OntologyTerm("sa1", "sa1"));
        sa1.setType(SampleAttributeType.CHARACTERISTIC);

        Sample s1 = profileIn.createSample();
        s1.setName("Sample 1");
        s1.setValues(new HashMap<Integer, String>() {{
            put(sa1.getId(), "value1");
        }});

        Sample s2 = profileIn.createSample();
        s2.setName("Sample 2");
        s2.setValues(new HashMap<Integer, String>() {{
            put(sa1.getId(), "value2");
        }});

        Extract ex1 = profileIn.createExtract(false, s1);
        Extract ex2 = profileIn.createExtract(false, s2);

        LabeledExtract le1= profileIn.createLabeledExtract(ex1, label1.getName());
        LabeledExtract le2 = profileIn.createLabeledExtract(ex1, label2.getName());
        LabeledExtract le3 = profileIn.createLabeledExtract(ex2, label1.getName());
        LabeledExtract le4 = profileIn.createLabeledExtract(ex2, label2.getName());

        FileColumn fileColumn = profileIn.createFileColumn(FileType.RAW_FILE);
        fileColumn.setFileName(profileIn.getAssay(le1), "file1");
        fileColumn.setFileName(profileIn.getAssay(le2), "file1");
        fileColumn.setFileName(profileIn.getAssay(le3), "file1");
        fileColumn.setFileName(profileIn.getAssay(le4), "file1");

        String jsonString = JsonCodec.writeExperiment(profileIn);
        log.debug("experimentProfile=" + jsonString);
        assertNotNull(jsonString);

        ExperimentProfile profileOut = JsonCodec.readExperiment(jsonString);
        assertReflectionEquals(profileIn, profileOut);
    }

    @Test
    public void arrayDesignHeaderSerializationTest() throws DataSerializationException {
        ArrayDesignHeader adHeaderIn = new ArrayDesignHeader();
        adHeaderIn.setName("Array Design Name");
        adHeaderIn.setDescription("Array Design Description");
        adHeaderIn.setOrganism(new OntologyTerm("mus", "mus"));
        adHeaderIn.setPublicReleaseDate(new Date());
        adHeaderIn.setVersion("123");
        adHeaderIn.setPrintingProtocolBackup(new PrintingProtocol(0, "name", "description"));

        String jsonString = JsonCodec.writeArrayDesign(adHeaderIn);
        log.debug("arrayDesignHeader=", jsonString);
        assertNotNull(jsonString);

        ArrayDesignHeader adHeaderOut = JsonCodec.readArrayDesign(jsonString);
        assertReflectionEquals(adHeaderIn, adHeaderOut);
    }
}
