/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.gwt.regexp.shared.RegExp;
import com.google.inject.Inject;
import org.mged.magetab.error.ErrorItem;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.listener.ErrorItemListener;
import uk.ac.ebi.arrayexpress2.magetab.parser.IDFParser;
import uk.ac.ebi.arrayexpress2.magetab.parser.SDRFParser;
import uk.ac.ebi.fg.annotare2.core.components.EfoSearch;
import uk.ac.ebi.fg.annotare2.core.files.DataFileHandle;
import uk.ac.ebi.fg.annotare2.core.files.RemoteFileHandle;
import uk.ac.ebi.fg.annotare2.core.magetab.MageTabGenerator;
import uk.ac.ebi.fg.annotare2.core.magetab.MageTabGenerator.GenerateOption;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.ImportedExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.magetabcheck.MageTabChecker;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.*;
import uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.LimpopoBasedExperiment;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.FileColumn;
import uk.ac.ebi.fg.annotare2.submission.model.FileRef;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;
import uk.ac.ebi.fg.annotare2.web.server.services.files.DataFileConnector;
import uk.ac.ebi.fg.annotare2.web.server.services.files.FileAvailabilityChecker;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Ordering.natural;

public class SubmissionValidator {

    private final MageTabChecker checker;
    private final DataFileManagerImpl dataFileManager;
    private final DataFileConnector dataFileConnector;
    private final EfoSearch efoSearch;

    @Inject
    public SubmissionValidator(MageTabChecker checker,
                               DataFileManagerImpl dataFileManager,
                               DataFileConnector dataFileConnector,
                               EfoSearch efoSearch) {
        this.checker = checker;
        this.dataFileManager = dataFileManager;
        this.dataFileConnector = dataFileConnector;
        this.efoSearch = efoSearch;
    }

    public Collection<CheckResult> validate(Submission submission) throws IOException,
            ParseException, UnknownExperimentTypeException, DataSerializationException {

        if (submission instanceof ExperimentSubmission) {
            return validateExperimentSubmission((ExperimentSubmission)submission);
        } else if (submission instanceof ImportedExperimentSubmission) {
            return validateImportedExperimentSubmission((ImportedExperimentSubmission)submission);
        } else {
            throw new IllegalArgumentException("Unable to validate a submission of " + submission.getClass().getName() + " type");
        }
    }

    private boolean validateRelatedAccessionNumber(String relatedAccessionNumber) {
        RegExp regex = RegExp.compile("^\\t*\\s*(?:(?:[E]-[A-Z]{4}-\\d+,\\t*\\s*)|(?:[A-Z]{3}\\d{6},\\t*\\s*))*(?:(?:[E]-[A-Z]{4}-\\d+)|(?:[A-Z]{3}\\d{6}))$");
        return regex.test(relatedAccessionNumber);
    }

    private Collection<CheckResult> validateExperimentSubmission(ExperimentSubmission submission) throws IOException,
            ParseException, UnknownExperimentTypeException, DataSerializationException {

        Collection<CheckResult> results;

        Long userId = submission.getCreatedBy().getId();
        Long submissionId = submission.getId();
        ExperimentProfile exp = submission.getExperimentProfile();

        ExperimentType type;

        if (exp.getType().isMethylationMicroarray()){
            type = ExperimentType.METHYLATION_MICROARRAY;
        } else if (exp.getType().isMicroarray()){
            type = ExperimentType.MICRO_ARRAY;
        } else if (exp.getType().isSingleCell()){
            type = ExperimentType.SINGLE_CELL;
        } else{
            type = ExperimentType.HTS;
        }

        MAGETABInvestigation mageTab = (new MageTabGenerator(exp, efoSearch, GenerateOption.REPLACE_NEWLINES_WITH_SPACES)).generate();
        mageTab.IDF.setLocation(dataFileConnector.getFileUrl(userId, submissionId, "idf.txt"));
        mageTab.IDF.sdrfFile.add("sdrf.txt");
        mageTab.IDF.getLayout().calculateLocations(mageTab.IDF);
        if (mageTab.SDRFs.size() > 1) {
            throw new IllegalArgumentException("Unable to validate submission with no or multiple SDRFs");
        }
        SDRF sdrf = mageTab.SDRFs.values().iterator().next();
        sdrf.setLocation(dataFileConnector.getFileUrl(userId, submissionId, "sdrf.txt"));
        sdrf.getLayout().calculateLocations(sdrf);

        results = checker.check(new LimpopoBasedExperiment(mageTab.IDF, mageTab.SDRFs), type);

        Set<DataFile> allFiles = submission.getFiles();
        Set<DataFile> assignedFiles = dataFileManager.getAssignedFiles(submission);

        Collection<FileColumn> rawDataFileColumns = exp.getFileColumns(FileType.RAW_FILE);
        Collection<FileColumn> rawMatrixDataFileColumns = exp.getFileColumns(FileType.RAW_MATRIX_FILE);

        if(exp.getType().isMicroarray()) {
            if(rawMatrixDataFileColumns.isEmpty() && rawDataFileColumns.isEmpty()) {
                addError(results, "[<a href=\"#DESIGN:FILES\">Assign Files</a>] At least one 'Raw Matrix Data File' OR one 'Raw Data File' column must be added.");
            }
        } else if(rawDataFileColumns.isEmpty()) {
            addError(results, "[<a href=\"#DESIGN:FILES\">Assign Files</a>] At least one 'Raw Data File' column must be added.");
        }

        if(exp.getType().isSequencing() || exp.getType().isSingleCell() || exp.getType().isMethylationMicroarray()) {
            Collection<DataFile> rawAssignedFiles = dataFileManager.getAssignedFiles(submission, FileType.RAW_FILE);
            Collection<FileRef> columnFiles = dataFileManager.getColumnFiles(submission, FileType.RAW_FILE);

            if(exp.getType().isSequencing() || exp.getType().isSingleCell()) {
                if (rawAssignedFiles.size() != 0 && columnFiles.size() == exp.getSamples().size()) {
                    addDuplicateFilesError(columnFiles, results);
                }
            } else if(exp.getType().isMethylationMicroarray()) {
                if(rawAssignedFiles.size() != 0 && columnFiles.size() == exp.getLabeledExtracts().size()){
                    addDuplicateFilesError(columnFiles, results);                }
            }
        }

        if(!isNullOrEmpty(exp.getRelatedAccessionNumber())) {
            if (!validateRelatedAccessionNumber(exp.getRelatedAccessionNumber())) {
                addError(results, "[<a href=\"#INFO:GENERAL_INFO\">General Info</a>] Enter the accession number of related experiments in ArrayExpress or PRIDE e.g. E-MTAB-4688, PXD123456");
            }
        }
        if (null == allFiles || 0 == allFiles.size()) {
            addError(results, "[<a href=\"#DESIGN:FILES\">Assign Files</a>] At least one data file must be uploaded and assigned.");
        } else if (null == assignedFiles || 0 == assignedFiles.size()) {
            addError(results, "[<a href=\"#DESIGN:FILES\">Assign Files</a>] At least one uploaded data file must be assigned.");
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
                    addError(results, "[<a href=\"#DESIGN:FILES\">Assign Files</a>] File " + dataFile.getName() + " uploaded with an error" + cause);
                } else if (!assignedFiles.contains(dataFile)) {
                    results.add(
                            CheckResult.checkFailed(
                                    "[<a href=\"#DESIGN:FILES\">Assign Files</a>] File " + dataFile.getName() + " should be assigned to at least one labeled extract."
                                    , CheckModality.WARNING
                                    , CheckPosition.undefinedPosition()
                                    , null
                                    , "DF01"
                            )
                    );
                }
            }
            FileAvailabilityChecker fileChecker = new FileAvailabilityChecker();
            for (DataFile dataFile : assignedFiles) {
                DataFileHandle source = dataFileManager.getFileHandle(dataFile);
                if (null == source || !fileChecker.isAvailable(source)) {
                    addError(results, "[<a href=\"#DESIGN:FILES\">Assign Files</a>] File " + dataFile.getName() + " is not accessible."
                            + ((source instanceof RemoteFileHandle) ? " on FTP" : ""));
                }
            }
        }

        return natural().sortedCopy(results);
    }

    private void addDuplicateFilesError(Collection<FileRef> columnFiles, Collection<CheckResult> results) {
        String duplicateFiles = getDuplicateAssignedFiles(columnFiles);

        addError(results, "[<a href=\"#DESIGN:FILES\">Assign Files</a>] (Duplicate File(s): " + duplicateFiles + ") A raw data file cannot be assigned to multiple samples.");
    }

    private String getDuplicateAssignedFiles(Collection<FileRef> rawAssignedFiles) {
        StringBuilder result = new StringBuilder();
        HashSet<FileRef> assignedFiles = new HashSet<>();
        HashSet<String> duplicateFiles = new HashSet<>();
        for (FileRef file: rawAssignedFiles) {
            if(!assignedFiles.add(file)) {
                duplicateFiles.add(file.getName());
            }
        }
        for(String fileName : duplicateFiles) {
            result.append(fileName);
            result.append(", ");
        }
        if(!result.toString().isEmpty()) {
            result.setLength(result.length() - 2);  // added to remove ending comma
        }
        return result.toString();
    }

    private Collection<CheckResult> validateImportedExperimentSubmission(ImportedExperimentSubmission submission)
            throws IOException, ParseException, UnknownExperimentTypeException, DataSerializationException {

        final List<ErrorItem> parserErrors = new ArrayList<ErrorItem>();
        ErrorItemListener parserListener = new ErrorItemListener() {

            @Override
            public void errorOccurred(ErrorItem item) {
                parserErrors.add(item);
            }
        };

        Collection<CheckResult> results = new ArrayList<CheckResult>();

        try {
            Collection<DataFile> idfFiles = submission.getIdfFiles();
            if (0 == idfFiles.size()) {
                addError(results, "[<a href=\"#DESIGN:FILES\">Assign Files</a>] IDF file has not been uploaded");
            } else if (idfFiles.size() > 1) {
                addError(results, "[<a href=\"#DESIGN:FILES\">Assign Files</a>] More than one IDF file has been uploaded (" + fileNames(idfFiles) + ")");
            } else {
                DataFile idfFile = idfFiles.iterator().next();
                MAGETABInvestigation mageTab = parseMageTab(submission, idfFile.getName(), parserListener);
                if (!parserErrors.isEmpty()) {
                    for (ErrorItem error : parserErrors) {
                        addError(results, error.reportString());
                    }
                } else {
                    results = checker.check(new LimpopoBasedExperiment(mageTab.IDF, mageTab.SDRFs));
                }
            }
        } catch (Exception x) {
            addError(results, x.getMessage());
        }

        return natural().sortedCopy(results);
    }

    private MAGETABInvestigation parseMageTab(Submission submission, String idfName, ErrorItemListener errorItemListener) throws IOException, ParseException {
        MAGETABInvestigation mageTab = new MAGETABInvestigation();

        Long userId = submission.getCreatedBy().getId();
        Long submissionId = submission.getId();

        if (dataFileConnector.containsFile(userId, submissionId, idfName)) {
            URL idfLocation = dataFileConnector.getFileUrl(userId, submissionId, idfName);
            mageTab.IDF.setLocation(idfLocation);
            IDFParser parser = new IDFParser();
            parser.addErrorItemListener(errorItemListener);
            parser.parse(idfLocation.openStream(), mageTab.IDF);
            for (String sdrfName : mageTab.IDF.sdrfFile) {
                if (dataFileConnector.containsFile(userId, submissionId, sdrfName)) {
                    URL sdrfLocation = dataFileConnector.getFileUrl(userId, submissionId, sdrfName);
                    SDRF sdrf = new SDRF();
                    sdrf.setLocation(sdrfLocation);
                    new SDRFParser().parse(sdrfLocation.openStream(), sdrf);
                    mageTab.SDRFs.put(sdrf.getLocation().getFile(), sdrf);
                }
            }
        }
        return mageTab;
    }

    private void addError(Collection<CheckResult> results, String errorMessage) {
        results.add(CheckResult.checkFailed(
                errorMessage
                , CheckModality.ERROR
                , CheckPosition.undefinedPosition()
                , null
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
