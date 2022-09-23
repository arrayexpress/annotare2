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
import com.google.gwt.thirdparty.json.JSONObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
import uk.ac.ebi.fg.annotare2.integration.FileValidationService.FileValidationStatus;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Queues.newArrayDeque;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static uk.ac.ebi.fg.annotare2.ae.AEConnection.SubmissionState.*;

public class AeIntegrationWatchdog {

    private static final Logger logger = LoggerFactory.getLogger(AeIntegrationWatchdog.class);

    private final HibernateSessionFactory sessionFactory;
    private final SubsTracking subsTracking;
    private final AEConnection aeConnection;
    private final SubmissionDao submissionDao;
    private final SubmissionFeedbackDao submissionFeedbackDao;
    private final SubmissionManager submissionManager;
    private final DataFileManager dataFileManager;
    private final FileValidationService fileValidationService;
    private final FtpManager ftpManager;
    private final EfoSearch efoSearch;
    private final ExtendedAnnotareProperties properties;
    private final Messenger messenger;
    private ScheduledExecutorService scheduler;
    private BlockingQueue<Long> submissionsBeingProcessed;
    private final SubmissionPostProcessor submissionPostProcessor;

    enum SubmissionOutcome {
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
                                 FileValidationService fileValidationService, FtpManager ftpManager,
                                 EfoSearch efoSearch,
                                 Messenger messenger,
                                 SubmissionPostProcessor submissionPostProcessor) {
        this.sessionFactory = sessionFactory;
        this.subsTracking = subsTracking;
        this.aeConnection = aeConnection;
        this.submissionDao = submissionDao;
        this.submissionFeedbackDao = submissionFeedbackDao;
        this.submissionManager = submissionManager;
        this.dataFileManager = dataFileManager;
        this.fileValidationService = fileValidationService;
        this.ftpManager = ftpManager;
        this.efoSearch = efoSearch;
        this.properties = properties;
        this.messenger = messenger;
        this.scheduler = Executors.newScheduledThreadPool(properties.getWatchdogThreadCount());
        this.submissionsBeingProcessed = new ArrayBlockingQueue<>(properties.getWatchdogThreadCount());
        this.submissionPostProcessor = submissionPostProcessor;
    }

    @PostConstruct
    public void startUp() throws Exception {
        final Runnable periodicProcess = new Runnable() {
            @Override
            public void run() {
                try {
                    processSubmissions();
                } catch (Throwable x) {
                    logger.error("Submission watchdog process caught an exception:", x);
                    messenger.send("Error in submission watchdog process:", x);
                }
            }

        };

        if (properties.isSubsTrackingEnabled()) {
            for (int i = 0; i < properties.getWatchdogThreadCount(); i++) {
                scheduler.scheduleAtFixedRate(periodicProcess, i * 20, 60, SECONDS);
            }
        }
    }

    @PreDestroy
    public void shutDown() throws Exception {
        scheduler.shutdown();
        if (!scheduler.awaitTermination(1, MINUTES)) {
            logger.warn("Submission watchdog process failed to stop cleanly, possibly busy processing submission");
        }
    }

    private void processSubmissions() throws Exception {
        sessionFactory.openSession();
        final Collection<Submission> submissions = submissionDao.getSubmissionsByStatus(
                SubmissionStatus.SUBMITTED
                , SubmissionStatus.RESUBMITTED
                , SubmissionStatus.AWAITING_FILE_VALIDATION
                , SubmissionStatus.VALIDATING_FILES
                , SubmissionStatus.IN_CURATION
                , SubmissionStatus.PRIVATE_IN_AE
                , SubmissionStatus.PUBLIC_IN_AE
        );

        for (Submission submission : submissions) {
            //Reopening session for each submission to avoid invalid session issue after long process of a submission
            Session session = sessionFactory.openSession();
            try{
                if (addSubmissionToSubmissionProcessingSet(submission)) {
                    logger.debug("Thread {} processing submission {}: {}", Thread.currentThread().getId(), submission.getId(), submission.getStatus());
                    try {
                        switch (submission.getStatus()) {
                            case SUBMITTED:
                            case RESUBMITTED:
                                if(!submissionPostProcessor.isPresent(submission)){
                                    processSubmitted(submission);
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

                            case AWAITING_FILE_VALIDATION:
                                processAwaitingFileValidation(submission);
                                break;

                            case VALIDATING_FILES:
                                processValidatingFiles(submission);
                                break;
                        }
                    } finally {
                        removeSubmissionFromSubmissionProcessingSet(submission);
                        if(submission.getStatus() == SubmissionStatus.SUBMITTED || submission.getStatus() == SubmissionStatus.RESUBMITTED) {
                            logger.debug("Thread {} removed submission {}: {} from current processing submission set.", Thread.currentThread().getId(), submission.getId(), submission.getStatus());
                        }
                    }

                }
            }
            finally {
                session.close();
            }
        }
    }

    private synchronized boolean addSubmissionToSubmissionProcessingSet(Submission submission) throws Exception {

        sessionFactory.getCurrentSession().refresh(submission);

        if (!submissionsBeingProcessed.contains(submission.getId()) && submissionsBeingProcessed.remainingCapacity() != 0) {
            if(submission.getStatus() == SubmissionStatus.SUBMITTED || submission.getStatus() == SubmissionStatus.RESUBMITTED){
                logger.debug("Thread {} is adding submission {}: {} to current submission processing set.", Thread.currentThread().getId(), submission.getId(), submission.getStatus());
            }
            submissionsBeingProcessed.add(submission.getId());
            if(submission.getStatus() == SubmissionStatus.SUBMITTED || submission.getStatus() == SubmissionStatus.RESUBMITTED){
                logger.debug("Thread {} added submission {}: {} to current submission set.", Thread.currentThread().getId(), submission.getId(), submission.getStatus());
            }
            return true;
        }
        return false;
    }

    private synchronized void removeSubmissionFromSubmissionProcessingSet(Submission submission){
        submissionsBeingProcessed.remove(submission.getId());
    }

    @Transactional(rollbackOn = {FileValidationException.class})
    public void processValidatingFiles(Submission submission) throws Exception {
        try {
            JSONObject json = fileValidationService.checkStatus(submission.getId());

            FileValidationStatus status = FileValidationStatus.valueOf(json.get("status").toString().replace(' ', '_').toUpperCase());
            logger.debug("File validation status for submission {} is {}", submission.getId(), status);
            if (status != FileValidationStatus.FINISHED) return;
            boolean hasErrors = fileValidationService.hasErrors(json);
            if (!hasErrors) {
                submission.setStatus(SubmissionStatus.SUBMITTED);
                submissionManager.save(submission);
            } else {
                sendEmail(
                        EmailTemplates.FILE_VALIDATION_ERROR_TEMPLATE,
                        new ImmutableMap.Builder<String, String>()
                                .put("to.name", submission.getCreatedBy().getName())
                                .put("to.email", submission.getCreatedBy().getEmail())
                                .put("submission.id", String.valueOf(submission.getId()))
                                .put("submission.title", submission.getTitle())
                                .put("submission.errors", fileValidationService.getErrorString(json))
                                .put("submission.date", submission.getUpdated().toString())
                                .build(),
                        submission
                );
                submission.setStatus(SubmissionStatus.IN_PROGRESS);
                submission.setOwnedBy(submission.getCreatedBy());
                submissionManager.save(submission);
            }
        } catch (Exception e) {
            throw new FileValidationException(e);
        }
    }

    @Transactional(rollbackOn = {FileValidationException.class})
    public void processAwaitingFileValidation(Submission submission) throws Exception {
        if (!(submission instanceof ExperimentSubmission)) {
            logger.error("Ignoring invalid submission type " + submission.getClass().getName() + " for " + submission.getId());
            return;
        }
        ExperimentProfile exp = ((ExperimentSubmission) submission).getExperimentProfile();
        String fileName = submission.getId().toString();

        // create temp sdrf
        File exportDir = Files.createTempDir();
        MageTabFiles mageTab = MageTabFiles.createMageTabFiles(exp, efoSearch, exportDir, fileName + ".idf.txt", fileName + ".sdrf.txt");
        if (!mageTab.getIdfFile().exists() || !mageTab.getSdrfFile().exists()) {
            throw new IOException("Unable to locate generated MAGE-TAB files");
        }

        // put sdrf in upload folder
        String ftpSubDirectory = submission.getFtpSubDirectory();
        if (!ftpManager.doesExist(ftpSubDirectory)) {
            ftpManager.createDirectory(ftpSubDirectory);
        }
        File sdrf = mageTab.getSdrfFile();
        new LocalFileHandle(sdrf).copyTo(new URI(ftpManager.getDirectory(ftpSubDirectory) + sdrf.getName()));

        // delete temp files
        mageTab.getSdrfFile().delete();
        mageTab.getIdfFile().delete();
        exportDir.delete();

        // put raw datafiles in upload folder
        Set<DataFile> rawDataFiles = dataFileManager.getAssignedFiles(submission, FileType.RAW_FILE);
        for (DataFile rawDataFile : rawDataFiles) {
            URI destinationURI = new URI(ftpManager.getDirectory(ftpSubDirectory) + rawDataFile.getName());
            DataFileHandle source = dataFileManager.getFileHandle(rawDataFile);
            source.copyTo(destinationURI);
        }

        // submit to file validator
        fileValidationService.submit(new URI(ftpManager.getDirectory(ftpSubDirectory)).getPath(), submission.getId());

        //update status
        submission.setStatus(SubmissionStatus.VALIDATING_FILES);
        submissionManager.save(submission);

        //TODO: send email

    }

    public void processSubmitted(Submission submission) throws SubsTrackingException, InterruptedException {
        Pair<SubmissionOutcome, Integer> submissionOutcomeIntegerPair = submitSubmission(submission);
        SubmissionOutcome outcome = submissionOutcomeIntegerPair.getLeft();
        Integer substrackingId = submissionOutcomeIntegerPair.getRight();
        if (SubmissionOutcome.SUBMISSION_FAILED != outcome) {
            File exportDir = copyDataFiles(submission, substrackingId);
            // Reopening session in case existing session closes after long file copy task(More than 8hrs).
            sessionFactory.openSession();
            addFilesToSubstracking(submission, substrackingId, exportDir);
            moveExportDirectory(exportDir);
            submissionPostProcessor.add(Pair.of(submission, outcome));
            logger.debug("Submission: {} added to post processing queue", submission.getId());
        }
    }

    private void moveExportDirectory(File exportDir) throws SubsTrackingException {
        LinuxShellCommandExecutor executor = new LinuxShellCommandExecutor();
        String lsfToDatamoverScript = properties.getLSFtoDataMoverScript() + " \"" + exportDir.getPath() + "\"";
        try {
            logger.info("Moving export directory {} to codon started.", exportDir.getPath());
            executor.execute("ssh codon-login \"bash -s\" < " + lsfToDatamoverScript);
            logger.info("Moving export directory {} to codon finished.", exportDir.getPath());
            executor.execute("rm -rf " + exportDir.getPath());
            logger.info("Export directory {} deleted.", exportDir.getPath());
        } catch (IOException e) {
            throw new SubsTrackingException(e);
        }
    }

    @Transactional(rollbackOn = {SubsTrackingException.class})
    public void addFilesToSubstracking(Submission submission, Integer substrackingId, File exportDir) throws SubsTrackingException {
        Connection subsTrackingConnection = null;

        try {
            if (properties.isSubsTrackingEnabled()) {
                subsTrackingConnection = subsTracking.getConnection();
                if (null == subsTrackingConnection) {
                    throw new SubsTrackingException(SubsTrackingException.UNABLE_TO_OBTAIN_CONNECTION);
                }
                subsTrackingConnection.setAutoCommit(false);
                if (submission instanceof ExperimentSubmission) {
                    exportExperimentSubmissionFiles(subsTrackingConnection, (ExperimentSubmission) submission, exportDir);
                } else if (submission instanceof ImportedExperimentSubmission) {
                    exportImportedExperimentSubmissionFiles(subsTrackingConnection, (ImportedExperimentSubmission) submission, exportDir);
                }

                subsTracking.sendSubmission(subsTrackingConnection, substrackingId);
                subsTrackingConnection.commit();
            }
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

    private File copyDataFiles(Submission submission, Integer subsTrackingId) throws SubsTrackingException {
        if (!(submission instanceof ExperimentSubmission)) {
            throw new SubsTrackingException(submission.getId() + " is not an Experiment Submission");
        }
        File exportDir = getExportDir(subsTrackingId);

        try {
            ExperimentProfile exp = ((ExperimentSubmission) submission).getExperimentProfile();
            Set<DataFile> dataFiles = dataFileManager.getAssignedFiles(submission);
            Set<DataFile> rawDataFiles = dataFileManager.getAssignedFiles(submission, FileType.RAW_FILE);
            boolean isSequencing = exp.getType().isSequencing();
            String dataFilesPostProcessingScript = properties.getSubsTrackingDataFilesPostProcessingScript();

            String ftpSubDirectory = submission.getFtpSubDirectory();

            if (isSequencing && rawDataFiles.size() > 0) {
                if (!ftpManager.doesExist(ftpSubDirectory)) {
                    ftpManager.createDirectory(ftpSubDirectory);
                }
            }

            File unpackedExportDirectory = new File(exportDir, "unpacked");
            if (!unpackedExportDirectory.exists()) {
                unpackedExportDirectory.mkdir();
                unpackedExportDirectory.setWritable(true, false);
            }


            if (dataFiles.size() > 0) {
                logger.debug("Will copy {} data files", dataFiles.size());
                Queue<DataFile> dataFileQueue = newArrayDeque();
                dataFileQueue.addAll(dataFiles);
                while (!dataFileQueue.isEmpty()) {
                    DataFile dataFile = dataFileQueue.poll();
                    logger.debug("Processing data file {}", dataFile.getName());
                    if (DataFileStatus.STORED == dataFile.getStatus()) {
                        URI destinationURI = (isSequencing && rawDataFiles.contains(dataFile))
                                ? new URI(ftpManager.getDirectory(ftpSubDirectory) + URLEncoder.encode(dataFile.getName(), StandardCharsets.UTF_8.toString()))
                                : new File(unpackedExportDirectory, dataFile.getName()).toURI();
                        DataFileHandle source = dataFileManager.getFileHandle(dataFile);
                        try{
                            DataFileHandle destination = source.copyTo(destinationURI);
                            logger.debug("Copied data file {} to {}", dataFile.getName(), destinationURI);
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
                        } catch (IOException e){
                            if(e.getMessage() != null && e.getMessage().contains("ssh_exchange_identification")){
                                dataFileQueue.add(dataFile);
                            }else if(e.getMessage() != null && e.getMessage().contains("failed to set permissions")){
                                logger.error(e.getMessage());
                                logger.info("Data file {} removed.", destinationURI);
                                DataFileHandle.createFromUri(destinationURI).delete();
                                dataFileQueue.add(dataFile);
                                logger.info("Will attempt to copy {} to {} ", source.getUri(), destinationURI);
                            }else {
                                throw new SubsTrackingException(e);
                            }
                        }
                    } else if (!dataFile.getStatus().isOk()) {
                        throw new IOException("Unable to process data file " + dataFile.getName() + ": " + dataFile.getStatus().getTitle());
                    }
                }
            }
        } catch (Throwable e) {
            throw new SubsTrackingException(e);
        }
        return exportDir;
    }

    private File getExportDir(Integer subsTrackingId) {
        File exportDir;
        if (properties.isSubsTrackingEnabled()) {
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
        return exportDir;
    }

    @Transactional
    public void processInCuration(Submission submission) throws SubsTrackingException, AEConnectionException {
        Integer subsTrackingId = submission.getSubsTrackingId();
        if (null != subsTrackingId) {

            // check if the accession has been assigned or updated in subs tracking
            String subsTrackingAccession = getSubsTrackingAccession(subsTrackingId);
            if (!Objects.equal(submission.getAccession(), subsTrackingAccession)) {
                String oldFtpSubDirectory = submission.getFtpSubDirectory();
                submission.setAccession(subsTrackingAccession);

                try {
                    messenger.updateTicket(
                            new ImmutableMap.Builder<String, String>()
                                    .put(RtFieldNames.ACCESSION_NUMBER, submission.getAccession())
                                    .build(),
                            submission.getRtTicketNumber()
                    );
                } catch (Exception x) {
                    messenger.send("There was a problem updating Accession Number " + submission.getRtTicketNumber(), x);
                }
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
                        new ImmutableMap.Builder<String, String>()
                                .put("to.name", submission.getCreatedBy().getName())
                                .put("to.email", submission.getCreatedBy().getEmail())
                                .put("submission.id", String.valueOf(submission.getId()))
                                .put("submission.title", submission.getTitle())
                                .put("submission.accession", submission.getAccession())
                                .put("submission.date", submission.getUpdated().toString())
                                .build(),
                        submission
                );
            }

            // check if the submission has been rejected
            if (!isInCuration(submission.getSubsTrackingId())) {
                reOpenSubmission(submission);
            }

            // This status change functionality migrated to submissions manager service.
//            else if (properties.isAeConnectionEnabled()) {
////                String accession = submission.getAccession();
////                if (!isNullOrEmpty(accession)) {
////                    AEConnection.SubmissionState state = aeConnection.getSubmissionState(accession);
////                    if (PRIVATE == state) {
////                        submission.setStatus(SubmissionStatus.PRIVATE_IN_AE);
////                        submissionManager.save(submission);
////                    } else if (PUBLIC == state) {
////                        submission.setStatus(SubmissionStatus.PUBLIC_IN_AE);
////                        submissionManager.save(submission);
////                    }
////                }
//            }
        }
    }

    @Transactional(rollbackOn = {AEConnectionException.class})
    public void processPrivateInAE(Submission submission) throws AEConnectionException, SubsTrackingException {

            String accession = submission.getAccession();
        // check if the submission has been rejected
        if (!isInCuration(submission.getSubsTrackingId())) {
            reOpenSubmission(submission);
        }
        else{
            // Status change functionality migrated to submissions manager service.
            if (isNullOrEmpty(accession)) {
                submission.setStatus(SubmissionStatus.IN_CURATION);
                submissionManager.save(submission);
            }
        }

    }

    private void reOpenSubmission(Submission submission) {
        submission.setStatus(SubmissionStatus.IN_PROGRESS);
        submission.setSubmitted(null);
        submission.setOwnedBy(submission.getCreatedBy());
        submissionManager.save(submission);
        sendEmail(
                EmailTemplates.REJECTED_SUBMISSION_TEMPLATE,
                new ImmutableMap.Builder<String, String>()
                        .put("to.name", submission.getCreatedBy().getName())
                        .put("to.email", submission.getCreatedBy().getEmail())
                        .put("submission.id", String.valueOf(submission.getId()))
                        .put("submission.title", submission.getTitle())
                        .put("submission.date", submission.getUpdated().toString())
                        .build(),
                submission
        );
    }

    @Transactional(rollbackOn = {AEConnectionException.class})
    public void processPublicInAE(Submission submission) throws AEConnectionException, SubsTrackingException {

            String accession = submission.getAccession();
        // check if the submission has been rejected
        if (!isInCuration(submission.getSubsTrackingId())) {
            reOpenSubmission(submission);
        }
        else{
            // Status change functionality migrated to submissions manager service.
            if (isNullOrEmpty(accession))  {
                submission.setStatus(SubmissionStatus.IN_CURATION);
                submissionManager.save(submission);
            }
        }

    }

    @Transactional(rollbackOn = {SubsTrackingException.class})
    public Pair<SubmissionOutcome, Integer> submitSubmission(Submission submission) throws SubsTrackingException {

        SubmissionOutcome result = SubmissionOutcome.SUBMISSION_FAILED;
        Connection subsTrackingConnection = null;
        Integer subsTrackingId = -1;

        try {
            // check all files are in a good shape first; if not - skip
            Collection<DataFile> files = dataFileManager.getAssignedFiles(submission);
            for (DataFile file : files) {
                if (!file.getStatus().isFinal()) {
                    return new MutablePair<>(result, -1);
                }
            }

            if (properties.isSubsTrackingEnabled()) {
                subsTrackingConnection = subsTracking.getConnection();
                if (null == subsTrackingConnection) {
                    throw new SubsTrackingException(SubsTrackingException.UNABLE_TO_OBTAIN_CONNECTION);
                }

                subsTrackingConnection.setAutoCommit(false);
                subsTrackingId = submission.getSubsTrackingId();
                if (null == subsTrackingId) {
                    subsTrackingId = subsTracking.addSubmission(subsTrackingConnection, submission);
                    submission.setSubsTrackingId(subsTrackingId);
                    result = SubmissionOutcome.INITIAL_SUBMISSION_OK;
                } else {
                    subsTracking.updateSubmission(subsTrackingConnection, submission);
                    result = SubmissionOutcome.REPEAT_SUBMISSION_OK;
                }
                subsTrackingConnection.commit();

            }
            return new MutablePair<>(result, subsTrackingId);
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
                logger.debug("Savings {} data files in the db", dataFiles.size());
                for (DataFile dataFile : dataFiles) {
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

    private void sendEmail(String template, Map<String, String> params, Submission submission) {
        try {
            messenger.send(template, params, submission.getCreatedBy(), submission);
        } catch (RuntimeException e) {
            logger.error("Unable to send email", e);
        }
    }


}
