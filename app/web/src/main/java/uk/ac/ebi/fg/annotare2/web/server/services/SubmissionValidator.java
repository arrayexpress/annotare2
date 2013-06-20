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

package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.common.io.Files;
import com.google.inject.Inject;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.parser.MAGETABParser;
import uk.ac.ebi.arrayexpress2.magetab.renderer.IDFWriter;
import uk.ac.ebi.arrayexpress2.magetab.renderer.SDRFWriter;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.magetab.integration.MageTabGenerator;
import uk.ac.ebi.fg.annotare2.magetabcheck.MageTabChecker;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckResult;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.ExperimentType;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.UknownExperimentTypeException;
import uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.LimpopoBasedExperiment;
import uk.ac.ebi.fg.annotare2.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.submissionmodel.DataSerializationException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import static com.google.common.collect.Ordering.natural;
import static com.google.common.io.Closeables.close;

/**
 * @author Olga Melnichuk
 */
public class SubmissionValidator {

    //TODO move it
    private static final String IDF_FILE_NAME = "idf";

    private static final String SDRF_FILE_NAME = "sdrf";

    private final MageTabChecker checker;

    @Inject
    public SubmissionValidator(MageTabChecker checker) {
        this.checker = checker;
    }

    public Collection<CheckResult> validate(ExperimentSubmission submission) throws IOException,
            ParseException, UknownExperimentTypeException, DataSerializationException {

        ExperimentProfile exp = submission.getExperimentProfile();
        ExperimentType type = exp.getType().isMicroarray() ? ExperimentType.MICRO_ARRAY : ExperimentType.HTS;

        File tmp = writeToFile(exp);

        MAGETABParser parser = new MAGETABParser();
        MAGETABInvestigation inv = parser.parse(new File(tmp, IDF_FILE_NAME));

        Collection<CheckResult> results = checker.check(new LimpopoBasedExperiment(inv), type);
        return natural().sortedCopy(results);
    }

    private File writeToFile(ExperimentProfile exp) throws IOException, DataSerializationException, ParseException {
        MAGETABInvestigation inv = (new MageTabGenerator(exp)).generate();

        File tmp = Files.createTempDir();
        File idfFile = new File(tmp, IDF_FILE_NAME);
        File sdrfFile = new File(tmp, SDRF_FILE_NAME);

        inv.IDF.sdrfFile.add(sdrfFile.getName());

        IDFWriter idfWriter = null;
        try {
            idfWriter = new IDFWriter(new FileWriter(idfFile));
            idfWriter.write(inv.IDF);
        } finally {
            close(idfWriter, true);
        }

        SDRFWriter sdrfWriter = null;
        try {
            sdrfWriter = new SDRFWriter(new FileWriter(sdrfFile));
            sdrfWriter.write(inv.SDRF);
        } finally {
            close(sdrfWriter, true);
        }
        return tmp;
    }

}
