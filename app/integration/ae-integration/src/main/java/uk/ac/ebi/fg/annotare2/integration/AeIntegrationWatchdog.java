/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package uk.ac.ebi.fg.annotare2.integration;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.inject.Inject;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.ae.AEConnection;
import uk.ac.ebi.fg.annotare2.ae.AEConnectionException;
import uk.ac.ebi.fg.annotare2.autosubs.SubsTracking;
import uk.ac.ebi.fg.annotare2.autosubs.SubsTrackingException;
import uk.ac.ebi.fg.annotare2.core.components.*;
import uk.ac.ebi.fg.annotare2.core.files.DataFileHandle;
import uk.ac.ebi.fg.annotare2.core.files.LocalFileHandle;
import uk.ac.ebi.fg.annotare2.core.magetab.MageTabFiles;
import uk.ac.ebi.fg.annotare2.core.transaction.Transactional;
import uk.ac.ebi.fg.annotare2.core.utils.LinuxShellCommandExecutor;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionFeedbackDao;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.ImportedExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.enums.DataFileStatus;
import uk.ac.ebi.fg.annotare2.db.model.enums.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.concurrent.TimeUnit.MINUTES;
import static uk.ac.ebi.fg.annotare2.ae.AEConnection.SubmissionState.*;

public class AeIntegrationWatchdog {

    private static final Logger logger = LoggerFactory.getLogger(AeIntegrationWatchdog.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final HibernateSessionFactory sessionFactory;
    private final SubsTracking subsTracking;
    private final AEConnection aeConnection;
    private final SubmissionDao submissionDao;
    private final SubmissionFeedbackDao submissionFeedbackDao;
    private final SubmissionManager submissionManager;
    private final DataFileManager dataFileManager;
    private final FtpManager ftpManager;
    private final EfoSearch efoSearch;
    private final ExtendedAnnotareProperties properties;
    private final EmailSender emailer;
    private final OtrsEmailSender otrsEmailer;

    private enum SubmissionOutcome {
        INITIAL_SUBMISSION_OK,
        REPEAT_SUBMISSION_OK,
        SUBMISSION_FAILED
    }

    @Inject
    public AeIntegrationWatchdog(HibernateSessionFactory sessionFactory,
                                 ExtendedAnnotareProperties properties,
                                 SubsTracking subsTracking,
                                 AEConnection aeConnection,
                                 SubmissionDao submissionDao,
                                 SubmissionFeedbackDao submissionFeedbackDao,
                                 SubmissionManager submissionManager,
                                 DataFileManager dataFileManager,
                                 FtpManager ftpManager,
                                 EfoSearch efoSearch,
                                 EmailSender emailer,
                                 OtrsEmailSender otrsEmailer) {
        this.sessionFactory = sessionFactory;
        this.subsTracking = subsTracking;
        this.aeConnection = aeConnection;
        this.submissionDao = submissionDao;
        this.submissionFeedbackDao = submissionFeedbackDao;
        this.submissionManager = submissionManager;
        this.dataFileManager = dataFileManager;
        this.ftpManager = ftpManager;
        this.efoSearch = efoSearch;
        this.properties = properties;
        this.emailer = emailer;
        this.otrsEmailer = otrsEmailer;
    }

    @PostConstruct
    public void startUp() throws Exception {
        final Runnable periodicProcess = new Runnable() {
            @Override
            public void run() {
                Session session = sessionFactory.openSession();
                try {
                    periodicRun();
                } catch (Throwable x) {
                    logger.error("Submission watchdog process caught an exception:", x);
                    emailer.sendException("Error in submission watchdog process:", x);
                } finally {
                    session.close();
                }
            }

        };

        if (properties.isSubsTrackingEnabled()) {
            scheduler.scheduleAtFixedRate(periodicProcess, 0, 1, MINUTES);
        }
    }

    @PreDestroy
    public void shutDown() throws Exception {
        scheduler.shutdown();
        if (!scheduler.awaitTermination(1, MINUTES)) {
            logger.warn("Submission watchdog process failed to stop cleanly, possibly busy processing submission");
        }
    }

    private void periodicRun() throws Exception {
        Collection<Submission> submissions = submissionDao.getSubmissionsByStatus(
                SubmissionStatus.SUBMITTED
                , SubmissionStatus.RESUBMITTED
                , SubmissionStatus.IN_CURATION
                , SubmissionStatus.PRIVATE_IN_AE
                , SubmissionStatus.PUBLIC_IN_AE
        );

        // funny hack to submit only one submission per run (to make gaps between submits)
        // this only applies to processSubmitted() case
        boolean hasProcessedOneSubmission = false;

        for (Submission submission : submissions) {
            switch (submission.getStatus()) {
                case SUBMITTED:
                case RESUBMITTED:
                    if (!hasProcessedOneSubmission) {
                        processSubmitted(submission);
                        hasProcessedOneSubmission = true;
                    }
                    break;

                case IN_CURATION:
                    processInCuration(submission);
                    break;

                case PRIVATE_IN_AE:
                    processPrivateInAE(submission);
                    break;

                case PUBLIC_IN_AE:
                    processPublicInAE(submission);
                    break;
            }
        }
    }

    @Transactional(rollbackOn = {SubsTrackingException.class})
    public void processSubmitted(Submission submission) throws SubsTrackingException {
        SubmissionOutcome outcome = submitSubmission(submission);
        if (SubmissionOutcome.SUBMISSION_FAILED != outcome) {
            boolean hasResubmitted = SubmissionStatus.RESUBMITTED == submission.getStatus();
            submission.setStatus(SubmissionStatus.IN_CURATION);
            submissionManager.save(submission);
            if (!hasResubmitted) {
                sendEmail(
                        EmailTemplates.INITIAL_SUBMISSION_TEMPLATE,
                        ImmutableMap.of(
                                "to.name", submission.getCreatedBy().getName(),
                                "to.email", submission.getCreatedBy().getEmail(),
                                "submission.title", submission.getTitle(),
                                "submission.date", submission.getUpdated().toString()
                        )
                );
            }

            if (properties.isSubsTrackingEnabled()) {
                String otrsTemplate = (SubmissionOutcome.INITIAL_SUBMISSION_OK == outcome) ?
                        EmailTemplates.INITIAL_SUBMISSION_OTRS_TEMPLATE : EmailTemplates.REPEAT_SUBMISSION_OTRS_TEMPLATE;

                String submissionType = "";
                if (submission instanceof ExperimentSubmission) {
                    try {
                        ExperimentProfile exp = ((ExperimentSubmission) submission).getExperimentProfile();
                        if (exp.getType().isSequencing()) {
                            submissionType = "HTS";
                        } else {
                            submissionType = "microarray";
                        }
                    } catch (DataSerializationException x) {}

                }
                //SubmissionFeedback feedback = submissionFeedbackDao.getLastFeedbackFor(submission);

                sendEmail(
                        otrsTemplate,
                        new ImmutableMap.Builder<String, String>().
                                put("to.name", submission.getCreatedBy().getName()).
                                put("to.email", submission.getCreatedBy().getEmail()).
                                put("submission.title", submission.getTitle()).
                                put("submission.date", submission.getUpdated().toString()).
                                put("submission.type", submissionType).
                                put("subsTracking.user", properties.getSubsTrackingUser()).
                                put("subsTracking.experiment.type", properties.getSubsTrackingExperimentType()).
                                put("subsTracking.experiment.id", String.valueOf(submission.getSubsTrackingId())).
                                build()
                );
            }
        }
    }

    @Transactional(rollbackOn = {SubsTrackingException.class, AEConnectionException.class})
    public void processInCuration(Submission submission) throws SubsTrackingException, AEConnectionException {
        Integer subsTrackingId = submission.getSubsTrackingId();
        if (null != subsTrackingId) {

            // check if the accession has been assigned or updated in subs tracking
            String subsTrackingAccession = getSubsTrackingAccession(subsTrackingId);
            if (!Objects.equal(submission.getAccession(), subsTrackingAccession)) {
                String oldFtpSubDirectory = submission.getFtpSubDirectory();
                submission.setAccession(subsTrackingAccession);
                if (ftpManager.doesExist(oldFtpSubDirectory)) {
                    String newFtpSubDirectory = submissionManager.generateUniqueFtpSubDirectory(submission);
                    submission.setFtpSubDirectory(newFtpSubDirectory);
                    try {
                        DataFileHandle dirToRename = DataFileHandle.createFromUri(
                                new URI(ftpManager.getRoot() + oldFtpSubDirectory)
                        );
                        dirToRename.rename(newFtpSubDirectory);
                    } catch (URISyntaxException | IOException e) {
                        throw new SubsTrackingException(e);
                    }
                }

                submissionManager.save(submission);

                sendEmail(
                        EmailTemplates.ACCESSION_UPDATE_TEMPLATE,
                        ImmutableMap.of(
                                "to.name", submission.getCreatedBy().getName(),
                                "to.email", submission.getCreatedBy().getEmail(),
                                "submission.title", submission.getTitle(),
                                "submission.accession", submission.getAccession(),
                                "submission.date", submission.getUpdated().toString()
                        )
                );
            }

            // check if the submission has been rejected
            if (!isInCuration(submission.getSubsTrackingId())) {
                submission.setStatus(SubmissionStatus.IN_PROGRESS);
                submission.setSubmitted(null);
                submission.setOwnedBy(submission.getCreatedBy());
                submissionManager.save(submission);
                sendEmail(
                        EmailTemplates.REJECTED_SUBMISSION_TEMPLATE,
                        ImmutableMap.of(
                                "to.name", submission.getCreatedBy().getName(),
                                "to.email", submission.getCreatedBy().getEmail(),
                                "submission.title", submission.getTitle(),
                                "submission.date", submission.getUpdated().toString()
                        )
                );
            } else if (properties.isAeConnectionEnabled()) {
                String accession = submission.getAccession();
                if (!isNullOrEmpty(accession)) {
                    AEConnection.SubmissionState state = aeConnection.getSubmissionState(accession);
                    if (PRIVATE == state) {
                        submission.setStatus(SubmissionStatus.PRIVATE_IN_AE);
                        submissionManager.save(submission);
                    } else if (PUBLIC == state) {
                        submission.setStatus(SubmissionStatus.PUBLIC_IN_AE);
                        submissionManager.save(submission);
                    }
                }
            }
        }
    }

    @Transactional(rollbackOn = {AEConnectionException.class})
    public void processPrivateInAE(Submission submission) throws AEConnectionException {
       if (properties.isAeConnectionEnabled()) {
           String accession = submission.getAccession();
           if (!isNullOrEmpty(accession)) {
               AEConnection.SubmissionState state = aeConnection.getSubmissionState(accession);
               if (NOT_LOADED == state) {
                   submission.setStatus(SubmissionStatus.IN_CURATION);
                   submissionManager.save(submission);
               } else if (PUBLIC == state) {
                   submission.setStatus(SubmissionStatus.PUBLIC_IN_AE);
                   submissionManager.save(submission);
               }
           } else {
               submission.setStatus(SubmissionStatus.IN_CURATION);
               submissionManager.save(submission);
           }
       }
    }

    @Transactional(rollbackOn = {AEConnectionException.class})
    public void processPublicInAE(Submission submission) throws AEConnectionException {
        if (properties.isAeConnectionEnabled()) {
            String accession = submission.getAccession();
            if (!isNullOrEmpty(accession)) {
                AEConnection.SubmissionState state = aeConnection.getSubmissionState(accession);
                if (NOT_LOADED == state) {
                    submission.setStatus(SubmissionStatus.IN_CURATION);
                    submissionManager.save(submission);
                } else if (PRIVATE == state) {
                    submission.setStatus(SubmissionStatus.PRIVATE_IN_AE);
                    submissionManager.save(submission);
                }
            } else {
                submission.setStatus(SubmissionStatus.IN_CURATION);
                submissionManager.save(submission);
            }
        }
    }

    private SubmissionOutcome submitSubmission(Submission submission) throws SubsTrackingException {

        SubmissionOutcome result = SubmissionOutcome.SUBMISSION_FAILED;
        Connection subsTrackingConnection = null;

        File exportDir;
        try {
            // check all files are in a good shape first; if not - skip
            Collection<DataFile> files = dataFileManager.getAssignedFiles(submission);
            for (DataFile file : files) {
                if (!file.getStatus().isFinal()) {
                    return result;
                }
            }

            if (properties.isSubsTrackingEnabled()) {
                subsTrackingConnection = subsTracking.getConnection();
                if (null == subsTrackingConnection) {
                    throw new SubsTrackingException(SubsTrackingException.UNABLE_TO_OBTAIN_CONNECTION);
                }

                subsTrackingConnection.setAutoCommit(false);
                Integer subsTrackingId = submission.getSubsTrackingId();
                if (null == subsTrackingId) {
                    subsTrackingId = subsTracking.addSubmission(subsTrackingConnection, submission);
                    submission.setSubsTrackingId(subsTrackingId);
                    result = SubmissionOutcome.INITIAL_SUBMISSION_OK;
                } else {
                    subsTracking.updateSubmission(subsTrackingConnection, submission);
                    result = SubmissionOutcome.REPEAT_SUBMISSION_OK;
                }

                exportDir = new File(properties.getSubsTrackingExportDir(), properties.getSubsTrackingUser());
                if (!exportDir.exists()) {
                    exportDir.mkdir();
                    exportDir.setWritable(true, false);
                }
                exportDir = new File(exportDir, properties.getSubsTrackingExperimentType() + "_" + String.valueOf(subsTrackingId));
                if (!exportDir.exists()) {
                    exportDir.mkdir();
                    exportDir.setWritable(true, false);
                }
            } else {
                exportDir = properties.getExportDir();
            }

            if (submission instanceof ExperimentSubmission) {
                exportExperimentSubmissionFiles(subsTrackingConnection, (ExperimentSubmission) submission, exportDir);
            } else if (submission instanceof ImportedExperimentSubmission) {
                exportImportedExperimentSubmissionFiles(subsTrackingConnection, (ImportedExperimentSubmission) submission, exportDir);
            }

            if (properties.isSubsTrackingEnabled()) {
                subsTracking.sendSubmission(subsTrackingConnection, submission.getSubsTrackingId());
                subsTrackingConnection.commit();
            }

            return result;
        } catch (Throwable e) {
            try {
                if (null != subsTrackingConnection) {
                    subsTrackingConnection.rollback();
                }
            } catch (SQLException ee) {
                logger.error("SQLException:", ee);
            }
            throw new SubsTrackingException(e);
        } finally {
            if (properties.isSubsTrackingEnabled()) {
                subsTracking.releaseConnection(subsTrackingConnection);
            }
        }
    }

    private void exportExperimentSubmissionFiles(Connection connection, ExperimentSubmission submission, File exportDirectory)
            throws SubsTrackingException {
        try {
            ExperimentProfile exp = submission.getExperimentProfile();
            Integer subsTrackingId = submission.getSubsTrackingId();
            String fileName = submission.getAccession();
            if (null == fileName) {
                fileName = "submission" + submission.getId() + "_annotare";
            }
            if (properties.isSubsTrackingEnabled()) {
                int version = 1;
                while (subsTracking.hasMageTabFileAdded(
                        connection,
                        subsTrackingId,
                        fileName + "_v" + version + ".idf.txt")) {
                    version++;
                }
                fileName = fileName + "_v" + version;
            }
            MageTabFiles mageTab = MageTabFiles.createMageTabFiles(exp, efoSearch, exportDirectory, fileName + ".idf.txt", fileName + ".sdrf.txt");

            if (!mageTab.getIdfFile().exists() || !mageTab.getSdrfFile().exists()) {
                throw new Exception("Unable to locate generated MAGE-TAB files");
            }
            mageTab.getIdfFile().setWritable(true, false);

            String dataFilesPostProcessingScript = null;
            if (properties.isSubsTrackingEnabled()) {
                subsTracking.deleteFiles(connection, subsTrackingId);
                subsTracking.addMageTabFile(connection, subsTrackingId, mageTab.getIdfFile().getName());
                dataFilesPostProcessingScript = properties.getSubsTrackingDataFilesPostProcessingScript();
            }

            exportDirectory = new File(exportDirectory, "unpacked");
            if (!exportDirectory.exists()) {
                exportDirectory.mkdir();
                exportDirectory.setWritable(true, false);
            }

            // move sdrf file
            File exportedSdrfFile = new File(exportDirectory, mageTab.getSdrfFile().getName());
            Files.move(mageTab.getSdrfFile(), exportedSdrfFile);
            exportedSdrfFile.setWritable(true, false);

            // copy data files
            Set<DataFile> dataFiles = dataFileManager.getAssignedFiles(submission);
            Set<DataFile> rawDataFiles = dataFileManager.getAssignedFiles(submission, FileType.RAW_FILE);
            boolean isSequencing = exp.getType().isSequencing();
            String ftpSubDirectory = submission.getFtpSubDirectory();

            if (isSequencing && rawDataFiles.size() > 0) {
                if (!ftpManager.doesExist(ftpSubDirectory)) {
                    ftpManager.createDirectory(ftpSubDirectory);
                }
            }

            if (dataFiles.size() > 0) {
                for (DataFile dataFile : dataFiles) {
                    if (DataFileStatus.STORED == dataFile.getStatus()) {
                        URI destinationURI = (isSequencing && rawDataFiles.contains(dataFile))
                                ? new URI(ftpManager.getDirectory(ftpSubDirectory) + dataFile.getName())
                                : new File(exportDirectory, dataFile.getName()).toURI();
                        DataFileHandle source = dataFileManager.getFileHandle(dataFile);
                        DataFileHandle destination = source.copyTo(destinationURI);
                        if (!isNullOrEmpty(dataFilesPostProcessingScript) && destination instanceof LocalFileHandle) {
                            LinuxShellCommandExecutor executor = new LinuxShellCommandExecutor();
                            if (executor.execute(dataFilesPostProcessingScript + " " + destination.getUri().getPath())) {
                                logger.info(isNullOrEmpty(
                                        executor.getOutput()) ?
                                                "Ran post-processing script on " + destination.getName() : executor.getOutput()
                                );
                            } else {
                                logger.error("Data file post-processing script returned an error: {}", executor.getErrors());
                            }
                        }
                    } else if (!dataFile.getStatus().isOk()) {
                        throw new IOException("Unable to process data file " + dataFile.getName() + ": " + dataFile.getStatus().getTitle());
                    }
                    if (properties.isSubsTrackingEnabled()) {
                        subsTracking.addDataFile(connection, subsTrackingId, dataFile.getName());
                    }
                }
            }
        } catch (Exception e) {
            throw new SubsTrackingException(e);
        }
    }

    private void exportImportedExperimentSubmissionFiles(Connection connection, ImportedExperimentSubmission submission, File exportDirectory)
            throws SubsTrackingException {
//        try {
//            Integer subsTrackingId = submission.getSubsTrackingId();
//            String fileName = submission.getAccession();
//            if (null == fileName) {
//                fileName = "submission" + submission.getId() + "_annotare";
//            }
//            if (properties.isSubsTrackingEnabled()) {
//                int version = 1;
//                while (subsTracking.hasMageTabFileAdded(
//                        connection,
//                        subsTrackingId,
//                        fileName + "_v" + version + ".idf.txt")) {
//                    version++;
//                }
//                fileName = fileName + "_v" + version;
//            }
//            String idfName = fileName + "idf.txt";
//            String sdrfName = fileName + "sdrf.txt";
//
//            Collection<DataFile> idfFiles = submission.getIdfFiles();
//            Collection<DataFile> sdrfFiles = submission.getSdrfFiles();
//            if (1 != idfFiles.size() && 1 != sdrfFiles.size()) {
//                throw new IOException("Imported submission must have one IDF and one SDRF file uploaded");
//            }
//
//            File exportedIdfFile = new File(exportDirectory, idfName);
//            DataFileHandle idfSource = dataFileManager.getFileHandle(idfFiles.iterator().next());
//            idfSource.copyTo(exportedIdfFile);
//            exportedIdfFile.setWritable(true, false);
//
//            String dataFilesPostProcessingScript = null;
//            if (properties.isSubsTrackingEnabled()) {
//                subsTracking.deleteFiles(connection, subsTrackingId);
//                subsTracking.addMageTabFile(connection, subsTrackingId, idfName);
//                dataFilesPostProcessingScript = properties.getSubsTrackingDataFilesPostProcessingScript();
//            }
//
//            exportDirectory = new File(exportDirectory, "unpacked");
//            if (!exportDirectory.exists()) {
//                exportDirectory.mkdir();
//                exportDirectory.setWritable(true, false);
//            }
//
//            // move sdrf file
//            File exportedSdrfFile = new File(exportDirectory, sdrfName);
//            DataFileHandle sdrfSource = dataFileManager.getFileHandle(sdrfFiles.iterator().next());
//            sdrfSource.copyTo(exportedSdrfFile);
//            exportedSdrfFile.setWritable(true, false);
//
//            // copy data files
//            Set<DataFile> dataFiles = dataFileManager.getAssignedFiles(submission);
//            if (dataFiles.size() > 0) {
//                for (DataFile dataFile : dataFiles) {
//                    if (DataFileStatus.STORED == dataFile.getStatus()) {
//                        File f = new File(exportDirectory, dataFile.getName());
//                        DataFileHandle source = dataFileManager.getFileHandle(dataFile);
//                        source.copyTo(f);
//                        f.setWritable(true, false);
//                        if (!isNullOrEmpty(dataFilesPostProcessingScript)) {
//                            LinuxShellCommandExecutor executor = new LinuxShellCommandExecutor();
//                            if (executor.execute(dataFilesPostProcessingScript + " " + f.getAbsolutePath())) {
//                                logger.info(isNullOrEmpty(
//                                                executor.getOutput()) ?
//                                                "Ran post-processing script on " + f.getName() : executor.getOutput()
//                                );
//                            } else {
//                                logger.error("Data file post-processing script returned an error: {}", executor.getErrors());
//                            }
//                        }
//                    } else if (!dataFile.getStatus().isOk()) {
//                        throw new IOException("Unable to process data file " + dataFile.getName() + ": " + dataFile.getStatus().getTitle());
//                    }
//                    if (properties.isSubsTrackingEnabled()) {
//                        subsTracking.addDataFile(connection, subsTrackingId, dataFile.getName());
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new SubsTrackingException(e);
//        }
    }

    private boolean isInCuration(Integer subsTrackingId) throws SubsTrackingException {
        Connection dbConnection = subsTracking.getConnection();
        try {
            return subsTracking.isInCuration(dbConnection, subsTrackingId);
        } finally {
            subsTracking.releaseConnection(dbConnection);
        }
    }

    private String getSubsTrackingAccession(Integer subsTrackingId) throws SubsTrackingException {
        Connection dbConnection = subsTracking.getConnection();
        try {
            return subsTracking.getAccession(dbConnection, subsTrackingId);
        } finally {
            subsTracking.releaseConnection(dbConnection);
        }
    }

    private void sendEmail(String template, Map<String,String> params) {
        try {
            emailer.sendFromTemplate(template, params);
        } catch (RuntimeException e) {
            logger.error("Unable to send email", e);
        }
    }

    private void sendOtrsEmail(String template, Map<String,String> params) {
        try {
            emailer.sendFromTemplate(template, params);
        } catch (RuntimeException e) {
            logger.error("Unable to send email", e);
        }

    }
}
