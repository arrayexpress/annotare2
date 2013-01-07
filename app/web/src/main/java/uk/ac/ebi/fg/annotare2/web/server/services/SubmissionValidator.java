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
import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.parser.MAGETABParser;
import uk.ac.ebi.fg.annotare2.magetab.CheckerModule;
import uk.ac.ebi.fg.annotare2.magetab.MageTabChecker;
import uk.ac.ebi.fg.annotare2.magetab.UndefinedInvestigationTypeException;
import uk.ac.ebi.fg.annotare2.magetab.checker.CheckResult;
import uk.ac.ebi.fg.annotare2.magetab.idf.IdfParser;
import uk.ac.ebi.fg.annotare2.magetab.idf.Investigation;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.IdfData;
import uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo.idf.LimpopoIdfDataProxy;
import uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo.sdrf.LimpopoBasedSdrfGraph;
import uk.ac.ebi.fg.annotare2.om.Submission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import static com.google.common.collect.Ordering.natural;
import static com.google.common.io.Closeables.closeQuietly;

/**
 * @author Olga Melnichuk
 */
public class SubmissionValidator {

    public Collection<CheckResult> validate(Submission submission) throws IOException, ParseException, UndefinedInvestigationTypeException {
        File tmp = copy(submission);
        MAGETABParser parser = new MAGETABParser();
        MAGETABInvestigation inv = parser.parse(new File(tmp, "idf"));
        IdfData idf = new LimpopoIdfDataProxy(inv.IDF);

        Injector injector = Guice.createInjector(new CheckerModule());
        MageTabChecker checker = new MageTabChecker(injector);
        Collection<CheckResult> results = checker.check(idf, new LimpopoBasedSdrfGraph(inv.SDRF, idf));
        return natural().sortedCopy(results);
    }

    private File copy(Submission submission) throws IOException {
        Investigation inv = IdfParser.parse(submission.getInvestigation());
        String sdrfFileRef = inv.getSdrfFile().getValue();

        File tmp = Files.createTempDir();

        copyStream(submission.getInvestigation(), new File(tmp, "idf"));
        copyStream(submission.getSampleAndDataRelationship(), new File(tmp, sdrfFileRef));
        return tmp;
    }

    private void copyStream(InputStream from, File file) throws IOException {
        FileOutputStream to = new FileOutputStream(file);
        try {
            ByteStreams.copy(from, to);
        } finally {
            closeQuietly(to);
            closeQuietly(from);
        }
    }
}
