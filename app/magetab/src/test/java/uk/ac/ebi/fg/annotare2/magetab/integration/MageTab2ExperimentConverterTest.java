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

import org.junit.Test;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.parser.IDFParser;
import uk.ac.ebi.arrayexpress2.magetab.parser.SDRFParser;
import uk.ac.ebi.fg.annotare2.submissionmodel.DataSerializationException;
import uk.ac.ebi.fg.annotare2.submissionmodel.Experiment;
import uk.ac.ebi.fg.annotare2.submissionmodel.GraphNode;

import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Sets.newHashSet;
import static junit.framework.TestCase.*;


/**
 * @author Olga Melnichuk
 */
public class MageTab2ExperimentConverterTest {

    @Test
    public void mageTab2OneColourExperimentTest() throws ParseException, UnsupportedGraphLayoutException, DataSerializationException {
        Experiment exp = createExperiment("/E-MTAB-641.idf.txt", "/E-MTAB-641.sdrf.txt");
        assertNotNull(exp);
        assertEquals(2, exp.getSources().size());
        assertEquals(0, exp.getSamples().size());
        assertEquals(6, exp.getExtracts().size());
        assertEquals(18, exp.getLabeledExtracts().size());
        assertEquals(18, exp.getAssays().size());
        assertEquals(0, exp.getScans().size());
        assertEquals(18, exp.getArrayDataFiles().size());

        assertHasUniqueNames(exp);
        assertHasUniqueIds(exp);
    }

    @Test
    public void mageTab2TwoColourExperimentTest() throws ParseException, UnsupportedGraphLayoutException, DataSerializationException {
        Experiment exp = createExperiment("/E-MEXP-3237.idf.txt", "/E-MEXP-3237.sdrf.txt");
        assertNotNull(exp);
        assertEquals(6, exp.getSources().size());
        assertEquals(0, exp.getSamples().size());
        assertEquals(6, exp.getExtracts().size());
        assertEquals(12, exp.getLabeledExtracts().size());
        assertEquals(6, exp.getAssays().size());
        assertEquals(0, exp.getScans().size());
        assertEquals(6, exp.getArrayDataFiles().size());

        assertTrue(exp.getLabeledExtracts().size() == 2 * exp.getAssays().size());
        assertHasUniqueNames(exp);
        assertHasUniqueIds(exp);
    }

    @Test
    public void mageTab2SequencingExperimentTest() throws ParseException, UnsupportedGraphLayoutException, DataSerializationException {
        Experiment exp = createExperiment("/E-MTAB-582.idf.txt", "/E-MTAB-582.sdrf.txt");
        assertNotNull(exp);
        assertEquals(10, exp.getSources().size());
        assertEquals(0, exp.getSamples().size());
        assertEquals(10, exp.getExtracts().size());
        assertEquals(0, exp.getLabeledExtracts().size());
        assertEquals(10, exp.getAssays().size());
        assertEquals(10, exp.getScans().size());
        assertEquals(0, exp.getArrayDataFiles().size());

        assertHasUniqueNames(exp);
        assertHasUniqueIds(exp);
    }

    private static void assertHasUniqueNames(Experiment exp) {
        assertHasUniqueNames(exp.getSources());
        assertHasUniqueNames(exp.getSamples());
        assertHasUniqueNames(exp.getExtracts());
        assertHasUniqueNames(exp.getLabeledExtracts());
        assertHasUniqueNames(exp.getAssays());
        assertHasUniqueNames(exp.getScans());
        assertHasUniqueNames(exp.getArrayDataFiles());
    }

    private static void assertHasUniqueNames(Collection<? extends GraphNode> items) {
        Set<String> names = newHashSet();
        for (GraphNode item : items) {
            assertFalse(isNullOrEmpty(item.getName()));
            if (names.contains(item.getName())) {
                fail("None unique name: " + item.getName());
            }
            names.add(item.getName());
        }
    }

    private static void assertHasUniqueIds(Experiment exp) {
        Set<Integer> ids = newHashSet();
        assertHasUniqueIds(ids, exp.getSources());
        assertHasUniqueIds(ids, exp.getSamples());
        assertHasUniqueIds(ids, exp.getExtracts());
        assertHasUniqueIds(ids, exp.getLabeledExtracts());
        assertHasUniqueIds(ids, exp.getAssays());
        assertHasUniqueIds(ids, exp.getScans());
        assertHasUniqueIds(ids, exp.getArrayDataFiles());
    }

    private static void assertHasUniqueIds(Set<Integer> ids, Collection<? extends GraphNode> items) {
        for (GraphNode item : items) {
            assertTrue(item.getId() > 0);
            if (ids.contains(item.getId())) {
                fail("None unique id: " + item.getId());
            }
        }
    }

    private Experiment createExperiment(String idfFile, String sdrfFile)
            throws UnsupportedGraphLayoutException, DataSerializationException, ParseException {
        IDFParser idfParser = new IDFParser();
        IDF idf = idfParser.parse(getClass().getResourceAsStream(idfFile));

        SDRFParser sdrfParser = new SDRFParser();
        SDRF sdrf = sdrfParser.parse(getClass().getResourceAsStream(sdrfFile));

        Experiment exp = MageTab2ExperimentConverter.convert(idf, sdrf);
        System.out.println(exp.toJsonString());
        return exp;
    }
}
