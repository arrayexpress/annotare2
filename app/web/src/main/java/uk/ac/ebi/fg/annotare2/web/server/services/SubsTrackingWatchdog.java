package uk.ac.ebi.fg.annotare2.web.server.services;

/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

import com.google.common.io.Files;
import com.google.inject.Inject;
import org.hibernate.Session;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.fg.annotare2.autosubs.SubsTracking;
import uk.ac.ebi.fg.annotare2.configmodel.DataSerializationException;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.db.om.DataFile;
import uk.ac.ebi.fg.annotare2.db.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.om.Submission;
import uk.ac.ebi.fg.annotare2.db.om.enums.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.web.server.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.web.server.rpc.MageTabFormat;
import uk.ac.ebi.fg.annotare2.web.server.transaction.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MINUTES;

public class SubsTrackingWatchdog {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final HibernateSessionFactory sessionFactory;
    private final SubsTracking subsTracking;
    private final SubmissionDao submissionDao;
    private final SubmissionManager submissionManager;
    private final DataFileManager dataFileManager;
    private final AnnotareProperties properties;


    @Inject
    public SubsTrackingWatchdog(HibernateSessionFactory sessionFactory,
                                AnnotareProperties properties,
                                SubsTracking subsTracking,
                                SubmissionDao submissionDao,
                                SubmissionManager submissionManager,
                                DataFileManager dataFileManager) {
        this.sessionFactory = sessionFactory;
        this.subsTracking = subsTracking;
        this.submissionDao = submissionDao;
        this.submissionManager = submissionManager;
        this.dataFileManager = dataFileManager;
        this.properties = properties;

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
                    runTransaction();
                } finally {
                    session.close();
                }
            }

        };

        scheduler.scheduleAtFixedRate(periodicProcess, 0, 1, MINUTES);
    }

    @Transactional
    public void runTransaction() {
        Collection<Submission> submissions = submissionDao.getSubmissionsByStatus(
                SubmissionStatus.SUBMITTED, SubmissionStatus.IN_CURATION
        );

        for (Submission submission : submissions) {
            switch (submission.getStatus()) {
                case SUBMITTED:
                    if (submitSubmission(submission)) {
                        submission.setStatus(SubmissionStatus.IN_CURATION);
                        submissionManager.save(submission);
                    }
                    break;

                case IN_CURATION:
                    if (!subsTracking.isInCuration(submission.getSubsTrackingId())) {
                        submission.setStatus(SubmissionStatus.IN_PROGRESS);
                        submission.setOwnedBy(submission.getCreatedBy());
                        submissionManager.save(submission);
                    }
            }
        }
    }

    private boolean submitSubmission(Submission submission) {
        try {
            File exportDir;

            if (properties.getAeSubsTrackingEnabled()) {

                Integer subsTrackingId = submission.getSubsTrackingId();
                if (null == subsTrackingId) {
                    subsTrackingId = subsTracking.addSubmission(submission);
                    submission.setSubsTrackingId(subsTrackingId);
                } else {
                    subsTracking.updateSubmission(submission);
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
                exportSubmissionFiles((ExperimentSubmission)submission, exportDir);
            }
            if (properties.getAeSubsTrackingEnabled()) {
                subsTracking.sendSubmission(submission.getSubsTrackingId());
            }
        } catch (Exception x) {
            //
        }
        return false;
    }

    private void exportSubmissionFiles(ExperimentSubmission submission, File exportDirectory)
            throws DataSerializationException, ParseException, IOException {
        ExperimentProfile exp = submission.getExperimentProfile();
        Integer subsTrackingId = submission.getSubsTrackingId();
        String fileName = submission.getAccession();
        if (null == fileName) {
            fileName = "submission" + submission.getId() + "_annotare";
        }
        if (properties.getAeSubsTrackingEnabled()) {
            int version = 1;
            while (subsTracking.hasMageTabFileAdded(
                    subsTrackingId,
                    fileName + "_v" + version + ".idf.txt")) {
                version++;
            }
            fileName = fileName + "_v" + version;
        }
        MageTabFormat mageTab = MageTabFormat.exportMageTab(exp, exportDirectory, fileName + ".idf.txt", fileName + ".sdrf.txt");

        if (!mageTab.getIdfFile().exists() || !mageTab.getSdrfFile().exists()) {
            ; // throw something
        }
        mageTab.getIdfFile().setWritable(true, false);

        if (properties.getAeSubsTrackingEnabled()) {
            subsTracking.deleteFiles(subsTrackingId);
            subsTracking.addMageTabFile(subsTrackingId, mageTab.getIdfFile().getName());
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
                    subsTracking.addDataFile(subsTrackingId, dataFile.getName());
                }
            }
        }
    }

}
