/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

import com.google.inject.Inject;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.magetabcheck.MageTabChecker;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.*;
import uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.LimpopoBasedExperiment;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;
import uk.ac.ebi.fg.annotare2.web.server.magetab.MageTabGenerator;
import uk.ac.ebi.fg.annotare2.web.server.magetab.MageTabGenerator.GenerateOption;
import uk.ac.ebi.fg.annotare2.web.server.services.files.DataFileSource;
import uk.ac.ebi.fg.annotare2.web.server.services.files.FileAvailabilityChecker;
import uk.ac.ebi.fg.annotare2.web.server.services.files.RemoteFileSource;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Collection;
import java.util.Set;

import static com.google.common.collect.Ordering.natural;

/**
 * @author Olga Melnichuk
 */
public class SubmissionValidator {

    private final MageTabChecker checker;
    private final SubmissionDao submissionDao;
    private final DataFileManager dataFileManager;
    private final EfoSearch efoSearch;

    @Inject
    public SubmissionValidator(MageTabChecker checker,
                               SubmissionDao submissionDao,
                               DataFileManager dataFileManager,
                               EfoSearch efoSearch) {
        this.checker = checker;
        this.submissionDao = submissionDao;
        this.dataFileManager = dataFileManager;
        this.efoSearch = efoSearch;

        registerAnnotareURLScheme();
    }

    public Collection<CheckResult> validate(ExperimentSubmission submission) throws IOException,
            ParseException, UknownExperimentTypeException, DataSerializationException {

        ExperimentProfile exp = submission.getExperimentProfile();
        ExperimentType type = exp.getType().isMicroarray() ? ExperimentType.MICRO_ARRAY : ExperimentType.HTS;

        MAGETABInvestigation mageTab = (new MageTabGenerator(exp, efoSearch, GenerateOption.REPLACE_NEWLINES_WITH_SPACES)).generate();
        mageTab.IDF.setLocation(new URL("annotare:/" + submission.getId() + "/idf.txt"));
        mageTab.IDF.sdrfFile.add("sdrf.txt");
        mageTab.IDF.getLayout().calculateLocations(mageTab.IDF);
        mageTab.SDRF.setLocation(new URL("annotare:/" + submission.getId() + "/sdrf.txt"));
        mageTab.SDRF.getLayout().calculateLocations(mageTab.SDRF);

        Collection<CheckResult> results = checker.check(new LimpopoBasedExperiment(mageTab.IDF, mageTab.SDRF), type);

        Set<DataFile> allFiles = submission.getFiles();
        Set<DataFile> assignedFiles = dataFileManager.getAssignedFiles(submission);

        if (null == allFiles || 0 == allFiles.size()) {
            results.add(
                    CheckResult.checkFailed(
                            "At least one data file must be uploaded and assigned"
                            , CheckModality.ERROR
                            , CheckPosition.undefinedPosition()
                            ,null
                    )
            );
        } else if (null == assignedFiles || 0 == assignedFiles.size()) {
            results.add(
                    CheckResult.checkFailed(
                            "At least one uploaded data file must be assigned"
                            , CheckModality.ERROR
                            , CheckPosition.undefinedPosition()
                            ,null
                    )
            );
        } else {
            for (DataFile dataFile : allFiles) {
                if (!dataFile.getStatus().isOk()) {
                    String cause = "";
                    switch (dataFile.getStatus()) {
                        case MD5_ERROR:
                            cause = " (MD5 check failed)";
                            break;
                        case FILE_NOT_FOUND_ERROR:
                            cause = " (file not found)";
                            break;
                    }
                    results.add(
                            CheckResult.checkFailed(
                                    "File " + dataFile.getName()
                                            + " uploaded with an error" + cause
                                    , CheckModality.ERROR
                                    , CheckPosition.undefinedPosition()
                                    ,null
                            )
                    );
                } else if (!assignedFiles.contains(dataFile)) {
                    results.add(
                            CheckResult.checkFailed(
                                    "File " + dataFile.getName() + " should be assigned to at least one labeled extract"
                                    , CheckModality.WARNING
                                    , CheckPosition.undefinedPosition()
                                    ,null
                            )
                    );
                }
            }
            FileAvailabilityChecker fileChecker = new FileAvailabilityChecker();
            for (DataFile dataFile : assignedFiles) {
                DataFileSource source = dataFileManager.getFileSource(dataFile);
                if (null == source || !fileChecker.isAvailable(source)) {
                    results.add(
                            CheckResult.checkFailed(
                                    "File " + dataFile.getName() + " is not accessible"
                                            + ((source instanceof RemoteFileSource) ? " on FTP" : "")
                                    , CheckModality.ERROR
                                    , CheckPosition.undefinedPosition()
                                    ,null
                            )
                    );
                }
            }
        }

        return natural().sortedCopy(results);
    }

    private void registerAnnotareURLScheme() {
        try {
            for (final Field field : URL.class.getDeclaredFields()) {
                if ("factory".equalsIgnoreCase(field.getName()) ) {
                    field.setAccessible(true);
                    field.set(
                            null,
                            new AnnotareURLStreamHandlerFactory(
                                    (URLStreamHandlerFactory) field.get(null),
                                    submissionDao
                            )
                    );
                }
            }
        } catch (Throwable e) {
        }
    }


    private static class AnnotareURLConnection extends URLConnection {

        protected AnnotareURLConnection(URL url) {
            super(url);
        }

        public void connect() throws IOException {
        }
    }

    private class AnnotareURLStreamHandler extends URLStreamHandler {

        private final SubmissionDao submissionDao;

        protected AnnotareURLStreamHandler(SubmissionDao submissionDao) {
            this.submissionDao = submissionDao;
        }

        @Override
        protected URLConnection openConnection(URL url) throws IOException {
            if (null != url) {
                if (null != url.getFile()) {

                    String fileName = url.getFile().replaceAll("^/(\\d+)/(.+)$", "$2");
                    if ("sdrf.txt".equals(fileName) || "idf.txt".equals(fileName)) {
                        return new AnnotareURLConnection(url);
                    }

                    try {
                        Submission submission = submissionDao.get(
                                Long.valueOf(url.getFile().replaceAll("^/(\\d+)/(.+)$", "$1")),
                                false
                        );

                        Set<DataFile> files = submission.getFiles();
                        for (DataFile file : files) {
                            if (file.getName().equals(fileName)) {
                                return new AnnotareURLConnection(url);
                            }
                        }
                    } catch (RecordNotFoundException x) {

                    }
                }
            }
            return null;
        }
    }

    private class AnnotareURLStreamHandlerFactory implements URLStreamHandlerFactory {

        private final URLStreamHandlerFactory chainFactory;
        private final SubmissionDao submissionDao;

        protected AnnotareURLStreamHandlerFactory(URLStreamHandlerFactory chainFactory, SubmissionDao submissionDao) {
            this.chainFactory = chainFactory;
            this.submissionDao = submissionDao;
        }

        @Override
        public URLStreamHandler createURLStreamHandler(String protocol) {
            if ("annotare".equals(protocol)) {
                return new AnnotareURLStreamHandler(submissionDao);
            } else if (null != chainFactory) {
                return chainFactory.createURLStreamHandler(protocol);
            }

            return null;
        }
    }
}
