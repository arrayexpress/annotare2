package uk.ac.ebi.fg.annotare2.web.server.services;

/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.inject.Inject;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.autosubs.SubsTracking;
import uk.ac.ebi.fg.annotare2.autosubs.SubsTrackingException;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.enums.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.web.server.magetab.MageTabFiles;
import uk.ac.ebi.fg.annotare2.web.server.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.web.server.transaction.Transactional;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MINUTES;

public class SubsTrackingWatchdog {

    private static final Logger log = LoggerFactory.getLogger(SubsTrackingWatchdog.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final HibernateSessionFactory sessionFactory;
    private final SubsTracking subsTracking;
    private final SubmissionDao submissionDao;
    private final SubmissionManager submissionManager;
    private final DataFileManager dataFileManager;
    private final AnnotareProperties properties;
    private final EmailSender emailer;

    private enum SubmissionOutcome {
        INITIAL_SUBMISSION_OK,
        REPEAT_SUBMISSION_OK,
        SUBMISSION_FAILED
    }

    @Inject
    public SubsTrackingWatchdog(HibernateSessionFactory sessionFactory,
                                AnnotareProperties properties,
                                SubsTracking subsTracking,
                                SubmissionDao submissionDao,
                                SubmissionManager submissionManager,
                                DataFileManager dataFileManager,
                                EmailSender emailer) {
        this.sessionFactory = sessionFactory;
        this.subsTracking = subsTracking;
        this.submissionDao = submissionDao;
        this.submissionManager = submissionManager;
        this.dataFileManager = dataFileManager;
        this.properties = properties;
        this.emailer = emailer;

        if (properties.getAeSubsTrackingEnabled()) {
            start();
        }
    }

    public void start() {
        final Runnable periodicProcess = new Runnable() {
            @Override
            public void run() {
                Session session = sessionFactory.openSession();
                try {
                    periodicRun();
                } catch (Exception x) {
                    log.error("Submission watchdog process caught an exception:", x);
                } finally {
                    session.close();
                }
            }

        };

        scheduler.scheduleAtFixedRate(periodicProcess, 0, 1, MINUTES);
    }

    public void periodicRun() throws Exception {
        Collection<Submission> submissions = submissionDao.getSubmissionsByStatus(
                SubmissionStatus.SUBMITTED, SubmissionStatus.IN_CURATION
        );

        for (Submission submission : submissions) {
            switch (submission.getStatus()) {
                case SUBMITTED:
                    processSubmitted(submission);
                    break;

                case IN_CURATION:
                    processInCuration(submission);
            }
        }
    }

    @Transactional
    public void processSubmitted(Submission submission) throws SubsTrackingException {
        SubmissionOutcome outcome = submitSubmission(submission);
        if (SubmissionOutcome.SUBMISSION_FAILED != outcome) {
            submission.setStatus(SubmissionStatus.IN_CURATION);
            submissionManager.save(submission);
            try {
                emailer.sendFromTemplate(
                        EmailSender.INITIAL_SUBMISSION_TEMPLATE,
                        ImmutableMap.of(
                                "to.name", submission.getCreatedBy().getName(),
                                "to.email", submission.getCreatedBy().getEmail(),
                                "submission.title", submission.getTitle(),
                                "submission.date", submission.getUpdated().toString()
                        )
                );

                if (properties.getAeSubsTrackingEnabled()) {
                    String otrsTemplate = (SubmissionOutcome.INITIAL_SUBMISSION_OK == outcome) ?
                            EmailSender.INITIAL_SUBMISSION_OTRS_TEMPLATE : EmailSender.REPEAT_SUBMISSION_OTRS_TEMPLATE;

                    emailer.sendFromTemplate(
                            otrsTemplate,
                            new ImmutableMap.Builder<String,String>().
                                    put("to.name", submission.getCreatedBy().getName()).
                                    put("to.email", submission.getCreatedBy().getEmail()).
                                    put("submission.title", submission.getTitle()).
                                    put("submission.date", submission.getUpdated().toString()).
                                    put("subsTracking.user", properties.getAeSubsTrackingUser()).
                                    put("subsTracking.experiment.type",  properties.getAeSubsTrackingExperimentType()).
                                    put("subsTracking.experiment.id", String.valueOf(submission.getSubsTrackingId())).
                                    build()
                    );
                }
            } catch (Exception x) {
                log.error("Email process threw an exception:", x);
            }
        }
    }

    @Transactional
    public void processInCuration(Submission submission) throws SubsTrackingException {
        Integer subsTrackingId = submission.getSubsTrackingId();
        if (null != subsTrackingId) {

            String subsTrackingAccession = getSubsTrackingAccession(subsTrackingId);
            if (!Objects.equal(submission.getAccession(), subsTrackingAccession)) {
                submission.setAccession(subsTrackingAccession);
                submissionManager.save(submission);
                try {
                    emailer.sendFromTemplate(
                            EmailSender.ACCESSION_UPDATE_TEMPLATE,
                            ImmutableMap.of(
                                    "to.name", submission.getCreatedBy().getName(),
                                    "to.email", submission.getCreatedBy().getEmail(),
                                    "submission.title", submission.getTitle(),
                                    "submission.accession", submission.getAccession(),
                                    "submission.date", submission.getUpdated().toString()
                            )
                    );
                } catch (Exception x) {
                    log.error("Email process threw an exception:", x);
                }
            }

            if (!isInCuration(submission.getSubsTrackingId())) {
                submission.setStatus(SubmissionStatus.IN_PROGRESS);
                submission.setOwnedBy(submission.getCreatedBy());
                submissionManager.save(submission);
                try {
                    emailer.sendFromTemplate(
                            EmailSender.REJECTED_SUBMISSION_TEMPLATE,
                            ImmutableMap.of(
                                    "to.name", submission.getCreatedBy().getName(),
                                    "to.email", submission.getCreatedBy().getEmail(),
                                    "submission.title", submission.getTitle(),
                                    "submission.date", submission.getUpdated().toString()
                            )
                    );
                } catch (Exception x) {
                    log.error("Email process threw an exception:", x);
                }
            }
        }
    }

    private SubmissionOutcome submitSubmission(Submission submission) throws SubsTrackingException {

        SubmissionOutcome result = SubmissionOutcome.SUBMISSION_FAILED;
        Connection subsTrackingConnection = null;

        try {
            File exportDir;

            if (properties.getAeSubsTrackingEnabled()) {
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

                exportDir = new File(properties.getAeSubsTrackingExportDir(), properties.getAeSubsTrackingUser());
                if (!exportDir.exists()) {
                    exportDir.mkdir();
                    exportDir.setWritable(true, false);
                }
                exportDir = new File(exportDir, properties.getAeSubsTrackingExperimentType() + "_" + String.valueOf(subsTrackingId));
                if (!exportDir.exists()) {
                    exportDir.mkdir();
                    exportDir.setWritable(true, false);
                }
            } else {
                exportDir = properties.getExportDir();
            }

            if (submission instanceof ExperimentSubmission) {
                exportSubmissionFiles(subsTrackingConnection, (ExperimentSubmission)submission, exportDir);
            }

            if (properties.getAeSubsTrackingEnabled()) {
                subsTracking.sendSubmission(subsTrackingConnection, submission.getSubsTrackingId());
                subsTrackingConnection.commit();
            }

            return result;
        } catch (Throwable x) {
            try {
                if (null != subsTrackingConnection) {
                    subsTrackingConnection.rollback();
                }
            } catch (SQLException xx) {
                log.error("SQLException:", x);
            }
            throw new SubsTrackingException(SubsTrackingException.CAUGHT_EXCEPTION, x);
        } finally {
            if (properties.getAeSubsTrackingEnabled()) {
                subsTracking.releaseConnection(subsTrackingConnection);
            }
        }
    }

    private void exportSubmissionFiles(Connection connection, ExperimentSubmission submission, File exportDirectory)
            throws SubsTrackingException {
        try {
            ExperimentProfile exp = submission.getExperimentProfile();
            Integer subsTrackingId = submission.getSubsTrackingId();
            String fileName = submission.getAccession();
            if (null == fileName) {
                fileName = "submission" + submission.getId() + "_annotare";
            }
            if (properties.getAeSubsTrackingEnabled()) {
                int version = 1;
                while (subsTracking.hasMageTabFileAdded(
                        connection,
                        subsTrackingId,
                        fileName + "_v" + version + ".idf.txt")) {
                    version++;
                }
                fileName = fileName + "_v" + version;
            }
            MageTabFiles mageTab = MageTabFiles.createMageTabFiles(exp, exportDirectory, fileName + ".idf.txt", fileName + ".sdrf.txt");

            if (!mageTab.getIdfFile().exists() || !mageTab.getSdrfFile().exists()) {
                ; // throw something
            }
            mageTab.getIdfFile().setWritable(true, false);

            if (properties.getAeSubsTrackingEnabled()) {
                subsTracking.deleteFiles(connection, subsTrackingId);
                subsTracking.addMageTabFile(connection, subsTrackingId, mageTab.getIdfFile().getName());
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
            Set<DataFile> dataFiles = submission.getFiles();
            if (dataFiles.size() > 0) {

                for (DataFile dataFile : dataFiles) {
                    File f = new File(exportDirectory, dataFile.getName());
                    Files.copy(dataFileManager.getFile(dataFile), f);
                    f.setWritable(true, false);
                    if (properties.getAeSubsTrackingEnabled()) {
                        subsTracking.addDataFile(connection, subsTrackingId, dataFile.getName());
                    }
                }
            }
        } catch (Exception x) {
            throw new SubsTrackingException(SubsTrackingException.CAUGHT_EXCEPTION, x);
        }
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

}
