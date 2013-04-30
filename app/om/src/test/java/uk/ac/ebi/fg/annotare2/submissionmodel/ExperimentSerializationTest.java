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

package uk.ac.ebi.fg.annotare2.submissionmodel;

import com.google.common.collect.Maps;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Olga Melnichuk
 */
public class ExperimentSerializationTest {

    @Test
    public void emptyExperimentTest() throws DataSerializationException {
        Experiment exp1 = newExperiment();
        Experiment exp2 = Experiment.fromJsonString(exp1.toJsonString());
        assertTrue(exp2.getSources().isEmpty());
        assertTrue(exp2.getSamples().isEmpty());
        assertTrue(exp2.getExtracts().isEmpty());
        assertTrue(exp2.getLabeledExtracts().isEmpty());
        assertTrue(exp2.getAssays().isEmpty());
        assertTrue(exp2.getScans().isEmpty());
        assertTrue(exp2.getArrayDataFiles().isEmpty());
    }

    @Test
    public void oneSourceNodeTest() throws DataSerializationException {
        Experiment exp1 = newExperiment();
        Sample sample1 = exp1.createSample();
        sample1.setName("Sample");

        Extract extract1 = exp1.createExtract();
        extract1.setName("Extract");

        Source source1 = exp1.createSource();
        source1.setName("Source");
        source1.addSample(sample1);
        source1.addExtract(extract1);

        assertEquals(1, exp1.getSources().size());
        assertEquals(1, exp1.getSamples().size());
        assertEquals(1, exp1.getExtracts().size());

        Experiment exp2 = Experiment.fromJsonString(exp1.toJsonString());
        assertEquals(exp1.getSources().size(), exp2.getSources().size());
        assertEquals(exp1.getSamples().size(), exp2.getSamples().size());
        assertEquals(exp1.getExtracts().size(), exp2.getExtracts().size());

        Source source2 = exp2.getSources().get(0);
        assertEquals(source1.getSamples().size(), source2.getSamples().size());
        assertEquals(source1.getExtracts().size(), source2.getExtracts().size());
        Sample sample2 = source2.getSamples().get(0);
        Extract extract2 = source2.getExtracts().get(0);

        assertNodeEquals(source1, source2);
        assertNodeEquals(sample1, sample2);
        assertNodeEquals(extract1, extract2);
    }

    @Test
    public void oneSampleNodeTest() throws DataSerializationException {
        Experiment exp1 = newExperiment();
        Extract extract1 = exp1.createExtract();
        extract1.setName("Extract");

        Sample sample1 = exp1.createSample();
        sample1.setName("Sample");
        sample1.addExtract(extract1);
        assertEquals(1, exp1.getSamples().size());
        assertEquals(1, exp1.getExtracts().size());

        Experiment exp2 = Experiment.fromJsonString(exp1.toJsonString());
        assertEquals(exp1.getSamples().size(), exp2.getSamples().size());
        assertEquals(exp1.getExtracts().size(), exp2.getExtracts().size());

        Sample sample2 = exp2.getSamples().get(0);
        assertEquals(sample1.getExtracts().size(), sample2.getExtracts().size());
        Extract extract2 = sample2.getExtracts().get(0);

        assertNodeEquals(sample1, sample2);

        assertEquals(extract1.getId(), extract2.getId());
        assertEquals(extract1.getName(), extract2.getName());
    }

    @Test
    public void oneExtractNodeTest() throws DataSerializationException {
        Experiment exp1 = newExperiment();
        Assay assay1 = exp1.createAssay();
        assay1.setName("Assay");

        LabeledExtract labeledExtract1 = exp1.createLabeledExtract();
        labeledExtract1.setName("LabeledExtract");

        Extract extract1 = exp1.createExtract();
        extract1.setName("Extract");
        extract1.addAssay(assay1);
        extract1.addLabeledExtract(labeledExtract1);

        assertEquals(1, exp1.getAssays().size());
        assertEquals(1, exp1.getLabeledExtracts().size());
        assertEquals(1, exp1.getExtracts().size());

        Experiment exp2 = Experiment.fromJsonString(exp1.toJsonString());
        assertEquals(exp1.getAssays().size(), exp2.getAssays().size());
        assertEquals(exp1.getLabeledExtracts().size(), exp2.getLabeledExtracts().size());
        assertEquals(exp1.getExtracts().size(), exp2.getExtracts().size());

        Extract extract2 = exp2.getExtracts().get(0);
        assertNodeEquals(extract1, extract2);

        assertEquals(extract1.getAssays().size(), extract2.getAssays().size());
        assertEquals(extract1.getLabeledExtracts().size(), extract2.getLabeledExtracts().size());

        Assay assay2 = extract2.getAssays().get(0);
        LabeledExtract labeledExtract2 = extract2.getLabeledExtracts().get(0);
        assertNodeEquals(assay1, assay2);
        assertNodeEquals(labeledExtract1, labeledExtract2);
    }

    @Test
    public void oneLabeledExtractNodeTest() throws DataSerializationException {
        Experiment exp1 = newExperiment();
        Assay assay1 = exp1.createAssay();
        assay1.setName("Assay");

        LabeledExtract labeledExtract1 = exp1.createLabeledExtract();
        labeledExtract1.setName("LabeledExtract");
        labeledExtract1.addAssay(assay1);
        assertEquals(1, exp1.getAssays().size());
        assertEquals(1, exp1.getLabeledExtracts().size());

        Experiment exp2 = Experiment.fromJsonString(exp1.toJsonString());
        assertEquals(exp1.getAssays().size(), exp2.getAssays().size());
        assertEquals(exp1.getLabeledExtracts().size(), exp2.getLabeledExtracts().size());

        LabeledExtract labeledExtract2 = exp2.getLabeledExtracts().get(0);
        assertNodeEquals(labeledExtract1, labeledExtract2);

        assertEquals(labeledExtract1.getAssays().size(), labeledExtract2.getAssays().size());

        Assay assay2 = labeledExtract2.getAssays().get(0);
        assertNodeEquals(assay1, assay2);
    }

    @Test
    public void oneAssayNodeTest() throws DataSerializationException {
        Experiment exp1 = newExperiment();
        ArrayDataFile arrayDataFile1 = exp1.createArrayDataFile();
        arrayDataFile1.setName("ArrayDataFile");

        Scan scan1 = exp1.createScan();
        scan1.setName("Scan");

        Assay assay1 = exp1.createAssay();
        assay1.setName("Assay");
        assay1.addArrayDataFile(arrayDataFile1);
        assay1.addScan(scan1);

        assertEquals(1, exp1.getAssays().size());
        assertEquals(1, exp1.getArrayDataFiles().size());
        assertEquals(1, exp1.getScans().size());

        Experiment exp2 = Experiment.fromJsonString(exp1.toJsonString());
        assertEquals(exp1.getAssays().size(), exp2.getAssays().size());
        assertEquals(exp1.getArrayDataFiles().size(), exp2.getArrayDataFiles().size());
        assertEquals(exp1.getScans().size(), exp2.getScans().size());

        Assay assay2 = exp2.getAssays().get(0);
        assertNodeEquals(assay1, assay2);

        assertEquals(assay1.getArrayDataFiles().size(), assay2.getArrayDataFiles().size());
        assertEquals(assay1.getScans().size(), assay2.getScans().size());

        ArrayDataFile arrayDataFile2 = assay2.getArrayDataFiles().get(0);
        Scan scan2 = assay2.getScans().get(0);
        assertNodeEquals(arrayDataFile1, arrayDataFile2);
        assertNodeEquals(scan1, scan2);
    }

    private static void assertNodeEquals(GraphNode node1, GraphNode node2) {
        assertEquals(node1.getId(), node2.getId());
        assertEquals(node1.getName(), node2.getName());
    }

    private static Experiment newExperiment() {
        return new Experiment(Maps.<String, String>newHashMap());
    }
}

