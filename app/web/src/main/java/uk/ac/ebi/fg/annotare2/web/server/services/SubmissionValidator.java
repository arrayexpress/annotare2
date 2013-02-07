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

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.inject.Inject;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.parser.MAGETABParser;
import uk.ac.ebi.fg.annotare2.magetab.idf.IdfParser;
import uk.ac.ebi.fg.annotare2.magetab.idf.Investigation;
import uk.ac.ebi.fg.annotare2.magetabcheck.MageTabChecker;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckResult;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.UknownExperimentTypeException;
import uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.LimpopoBasedExperiment;
import uk.ac.ebi.fg.annotare2.om.ExperimentSubmission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Ordering.natural;
import static com.google.common.io.Closeables.close;
import static com.google.common.io.Closeables.closeQuietly;

/**
 * @author Olga Melnichuk
 */
public class SubmissionValidator {

    //TODO move it
    private static final String IDF_FILE_NAME = "idf";

    private final MageTabChecker checker;

    @Inject
    public SubmissionValidator(MageTabChecker checker) {
        this.checker = checker;
    }

    public Collection<CheckResult> validate(ExperimentSubmission submission) throws IOException,
            ParseException, UknownExperimentTypeException {

        File tmp = copy(submission);

        MAGETABParser parser = new MAGETABParser();
        MAGETABInvestigation inv = parser.parse(new File(tmp, IDF_FILE_NAME));

        Collection<CheckResult> results = checker.check(new LimpopoBasedExperiment(inv));
        return natural().sortedCopy(results);
    }

    private File copy(ExperimentSubmission submission) throws IOException {
        Investigation inv = IdfParser.parse(submission.getInvestigation());
        String sdrfFileRef = inv.getSdrfFile().getValue();

        File tmp = Files.createTempDir();
        File idfFile = new File(tmp, IDF_FILE_NAME);

        if (0 == copyStream(submission.getInvestigation(), idfFile)) {
            throw new IOException("No IDF data");
        }

        if (!isNullOrEmpty(sdrfFileRef)) {
            if (0 == copyStream(submission.getSampleAndDataRelationship(), new File(tmp, sdrfFileRef))) {
                /* A workaround: limpopo parser hands when the content of a file is empty */
                throw new IOException("No SDRF data");
            }
        }

        return tmp;
    }

    private long copyStream(InputStream from, File file) throws IOException {
        if (from == null) {
            return 0;
        }
        FileOutputStream to = new FileOutputStream(file);
        try {
            return ByteStreams.copy(from, to);
        } finally {
            close(to, true);
            close(from, true);
        }
    }
}
