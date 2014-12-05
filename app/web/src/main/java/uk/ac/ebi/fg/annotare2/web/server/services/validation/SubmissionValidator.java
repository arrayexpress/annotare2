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

package uk.ac.ebi.fg.annotare2.web.server.services.validation;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.inject.Inject;
import org.mged.magetab.error.ErrorItem;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.listener.ErrorItemListener;
import uk.ac.ebi.arrayexpress2.magetab.parser.IDFParser;
import uk.ac.ebi.arrayexpress2.magetab.parser.SDRFParser;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.ImportedExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.magetabcheck.MageTabChecker;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.*;
import uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.LimpopoBasedExperiment;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;
import uk.ac.ebi.fg.annotare2.web.server.magetab.MageTabGenerator;
import uk.ac.ebi.fg.annotare2.web.server.magetab.MageTabGenerator.GenerateOption;
import uk.ac.ebi.fg.annotare2.web.server.services.DataFileManager;
import uk.ac.ebi.fg.annotare2.web.server.services.EfoSearch;
import uk.ac.ebi.fg.annotare2.web.server.services.files.DataFileSource;
import uk.ac.ebi.fg.annotare2.web.server.services.files.FileAvailabilityChecker;
import uk.ac.ebi.fg.annotare2.web.server.services.files.RemoteFileSource;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Ordering.natural;

public class SubmissionValidator {

    private final MageTabChecker checker;
    private final DataFileManager dataFileManager;
    private final ValidatedDataFileConnector dataFileConnector;
    private final EfoSearch efoSearch;

    @Inject
    public SubmissionValidator(MageTabChecker checker,
                               DataFileManager dataFileManager,
                               //DataFileStore dataFileStore,
                               ValidatedDataFileConnector dataFileConnector,
                               EfoSearch efoSearch) {
        this.checker = checker;
        this.dataFileManager = dataFileManager;
        //this.dataFileStore = dataFileStore;
        this.dataFileConnector = dataFileConnector;
        this.efoSearch = efoSearch;
    }

    public Collection<CheckResult> validate(Submission submission) throws IOException,
            ParseException, UknownExperimentTypeException, DataSerializationException {

        if (submission instanceof ExperimentSubmission) {
            return validateExperimentSubmission((ExperimentSubmission)submission);
        } else if (submission instanceof ImportedExperimentSubmission) {
            return validateImportedExperimentSubmission((ImportedExperimentSubmission)submission);
        } else throw new IllegalArgumentException("Unable to validate a submission of " + submission.getClass().getName() + " type");
    }

    private Collection<CheckResult> validateExperimentSubmission(ExperimentSubmission submission) throws IOException,
            ParseException, UknownExperimentTypeException, DataSerializationException {

        Collection<CheckResult> results = newArrayList();

        try {
            ExperimentProfile exp = submission.getExperimentProfile();
            ExperimentType type = exp.getType().isMicroarray() ? ExperimentType.MICRO_ARRAY : ExperimentType.HTS;

            MAGETABInvestigation mageTab = (new MageTabGenerator(exp, efoSearch, GenerateOption.REPLACE_NEWLINES_WITH_SPACES)).generate();
            mageTab.IDF.setLocation(new URL("annotare:/" + submission.getId() + "/idf.txt"));
            mageTab.IDF.sdrfFile.add("sdrf.txt");
            mageTab.IDF.getLayout().calculateLocations(mageTab.IDF);
            mageTab.SDRF.setLocation(new URL("annotare:/" + submission.getId() + "/sdrf.txt"));
            mageTab.SDRF.getLayout().calculateLocations(mageTab.SDRF);

            Set<DataFile> allFiles = submission.getFiles();

            dataFileConnector.addDataFiles(submission.getId(), allFiles);
            dataFileConnector.addFile(submission.getId(), "idf.txt");
            dataFileConnector.addFile(submission.getId(), "sdrf.txt");

            results = checker.check(new LimpopoBasedExperiment(mageTab.IDF, mageTab.SDRF), type);

            Set<DataFile> assignedFiles = dataFileManager.getAssignedFiles(submission);

            if (null == allFiles || 0 == allFiles.size()) {
                addError(results, "At least one data file must be uploaded and assigned");
            } else if (null == assignedFiles || 0 == assignedFiles.size()) {
                addError(results, "At least one uploaded data file must be assigned");
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
                        addError(results, "File " + dataFile.getName() + " uploaded with an error" + cause);
                    } else if (!assignedFiles.contains(dataFile)) {
                        results.add(
                                CheckResult.checkFailed(
                                        "File " + dataFile.getName() + " should be assigned to at least one labeled extract"
                                        , CheckModality.WARNING
                                        , CheckPosition.undefinedPosition()
                                        , null
                                )
                        );
                    }
                }
                FileAvailabilityChecker fileChecker = new FileAvailabilityChecker();
                for (DataFile dataFile : assignedFiles) {
                    DataFileSource source = dataFileManager.getFileSource(dataFile);
                    if (null == source || !fileChecker.isAvailable(source)) {
                        addError(results, "File " + dataFile.getName() + " is not accessible"
                                + ((source instanceof RemoteFileSource) ? " on FTP" : ""));
                    }
                }
            }
        } finally {
            dataFileConnector.removeFiles(submission.getId());
        }

        return natural().sortedCopy(results);
    }

    private Collection<CheckResult> validateImportedExperimentSubmission(ImportedExperimentSubmission submission)
            throws IOException, ParseException, UknownExperimentTypeException, DataSerializationException {

        final List<ErrorItem> parserErrors = new ArrayList<ErrorItem>();
        ErrorItemListener parserListener = new ErrorItemListener() {

            @Override
            public void errorOccurred(ErrorItem item) {
                parserErrors.add(item);
            }
        };

        Collection<CheckResult> results = new ArrayList<CheckResult>();

        try {
            dataFileConnector.addDataFiles(submission.getId(), submission.getFiles());

            Collection<DataFile> idfFiles = submission.getIdfFiles();
            if (0 == idfFiles.size()) {
                addError(results, "IDF file has not been uploaded");
            } else if (idfFiles.size() > 1) {
                addError(results, "More than one IDF file has been uploaded (" + fileNames(idfFiles) + ")");
            } else {
                DataFile idfFile = idfFiles.iterator().next();
                MAGETABInvestigation mageTab = parseMageTab(submission.getId(), idfFile.getName(), parserListener);
                if (!parserErrors.isEmpty()) {
                    for (ErrorItem error : parserErrors) {
                        addError(results, error.reportString());
                    }
                } else {
                    results = checker.check(new LimpopoBasedExperiment(mageTab.IDF, mageTab.SDRF));
                }
            }
        } catch (Exception x) {
            addError(results, x.getMessage());
        } finally {
            dataFileConnector.removeFiles(submission.getId());
        }

        return natural().sortedCopy(results);
    }

    private MAGETABInvestigation parseMageTab(Long submissionId, String idfName, ErrorItemListener errorItemListener) throws IOException, ParseException {
        MAGETABInvestigation mageTab = new MAGETABInvestigation();

        if (dataFileConnector.containsFile(submissionId, idfName)) {
            URL idfLocation = getAnnotareURL(submissionId, idfName);
            mageTab.IDF.setLocation(idfLocation);
            IDFParser parser = new IDFParser();
            parser.addErrorItemListener(errorItemListener);
            parser.parse(idfLocation.openStream(), mageTab.IDF);
            for (String sdrfName : mageTab.IDF.sdrfFile) {
                if (dataFileConnector.containsFile(submissionId, sdrfName)) {
                    URL sdrfLocation = getAnnotareURL(submissionId, sdrfName);
                    mageTab.SDRF.setLocation(sdrfLocation);
                    new SDRFParser().parse(sdrfLocation.openStream(), mageTab.SDRF);
                }
            }
        }
        return mageTab;
    }

    private URL getAnnotareURL(Long submissionId, String fileName) throws MalformedURLException {
        return new URL("annotare:/" + submissionId + "/" + fileName);
    }

    private void addError(Collection<CheckResult> results, String errorMessage) {
        results.add(CheckResult.checkFailed(
                errorMessage
                , CheckModality.ERROR
                , CheckPosition.undefinedPosition()
                , null
        ));
    }

    private String fileNames(Collection<DataFile> files) {
        return Joiner.on(", ").join(
                transform(files, new Function<DataFile, String>() {
                        @Nullable
                        @Override
                        public String apply(@Nullable DataFile input) {
                            return null == input ? null : input.getName();
                        }
                })
        );
    }
}
