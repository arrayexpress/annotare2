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
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentConfig;
import uk.ac.ebi.fg.annotare2.configmodel.enums.ExperimentConfigType;
import uk.ac.ebi.fg.annotare2.submissionmodel.DataSerializationException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static uk.ac.ebi.fg.annotare2.configmodel.enums.ExperimentConfigType.*;

/**
 * @author Olga Melnichuk
 */
public class MageTabImporterTest {

    @Test
    public void mageTab2OneColourExperimentTest() throws ParseException, UnsupportedGraphLayoutException,
            DataSerializationException, ImportExperimentException {
        ExperimentConfig exp = createExperiment(ONE_COLOR_MICROARRAY, "/E-MTAB-641.idf.txt", "/E-MTAB-641.sdrf.txt");
        assertNotNull(exp);
        assertEquals(6, exp.getSamples().size());
        assertEquals(18, exp.getLabeledExtracts().size());
    }

    @Test
    public void mageTab2TwoColourExperimentTest() throws ParseException, UnsupportedGraphLayoutException,
            DataSerializationException, ImportExperimentException {
        ExperimentConfig exp = createExperiment(TWO_COLOR_MICROARRAY, "/E-MEXP-3237.idf.txt", "/E-MEXP-3237.sdrf.txt");
        assertNotNull(exp);
        assertEquals(6, exp.getSamples().size());
        assertEquals(12, exp.getLabeledExtracts().size());
    }

    @Test
    public void mageTab2SequencingExperimentTest() throws ParseException, UnsupportedGraphLayoutException,
            DataSerializationException, ImportExperimentException {
        ExperimentConfig exp = createExperiment(SEQUENCING, "/E-MTAB-582.idf.txt", "/E-MTAB-582.sdrf.txt");
        assertNotNull(exp);
        assertEquals(10, exp.getSamples().size());
        assertEquals(0, exp.getLabeledExtracts().size());
    }

    private ExperimentConfig createExperiment(ExperimentConfigType type, String idfFile, String sdrfFile)
            throws UnsupportedGraphLayoutException, DataSerializationException, ParseException, ImportExperimentException {
        IDFParser idfParser = new IDFParser();
        IDF idf = idfParser.parse(getClass().getResourceAsStream(idfFile));

        SDRFParser sdrfParser = new SDRFParser();
        SDRF sdrf = sdrfParser.parse(getClass().getResourceAsStream(sdrfFile));

        ExperimentConfig exp = new MageTabImporter(type).importFrom(idf, sdrf);
        System.out.println(exp.toJsonString());
        return exp;
    }

}
