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
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.magetabcheck.MageTabChecker;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.*;
import uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.LimpopoBasedExperiment;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;
import uk.ac.ebi.fg.annotare2.web.server.magetab.MageTabGenerator;
import uk.ac.ebi.fg.annotare2.web.server.services.files.DataFileSource;
import uk.ac.ebi.fg.annotare2.web.server.services.files.FileAvailabilityChecker;
import uk.ac.ebi.fg.annotare2.web.server.services.files.RemoteFileSource;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import static com.google.common.collect.Ordering.natural;

/**
 * @author Olga Melnichuk
 */
public class SubmissionValidator {

    private final MageTabChecker checker;
    private final DataFileManager dataFileManager;

    @Inject
    public SubmissionValidator(MageTabChecker checker, DataFileManager dataFileManager) {
        this.checker = checker;
        this.dataFileManager = dataFileManager;
    }

    public Collection<CheckResult> validate(ExperimentSubmission submission) throws IOException,
            ParseException, UknownExperimentTypeException, DataSerializationException {

        ExperimentProfile exp = submission.getExperimentProfile();
        ExperimentType type = exp.getType().isMicroarray() ? ExperimentType.MICRO_ARRAY : ExperimentType.HTS;

        MAGETABInvestigation mageTab = (new MageTabGenerator(exp)).generate();
        //MageTabFiles mageTab = MageTabFiles.createMageTabFiles(exp, true);

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
            FileAvailabilityChecker checker = new FileAvailabilityChecker();
            for (DataFile dataFile : assignedFiles) {
                DataFileSource source = dataFileManager.getFileSource(dataFile);
                if (null == source || !checker.isAvailable(source)) {
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
}
