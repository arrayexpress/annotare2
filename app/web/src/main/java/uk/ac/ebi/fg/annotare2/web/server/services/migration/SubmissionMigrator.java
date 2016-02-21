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

package uk.ac.ebi.fg.annotare2.web.server.services.migration;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.FileColumn;
import uk.ac.ebi.fg.annotare2.submission.model.FileRef;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;
import uk.ac.ebi.fg.annotare2.submission.transform.ModelVersion;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;
import uk.ac.ebi.fg.annotare2.web.server.transaction.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Strings.isNullOrEmpty;

public class SubmissionMigrator extends AbstractIdleService {
    private static final Logger log = LoggerFactory.getLogger(SubmissionMigrator.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final HibernateSessionFactory sessionFactory;
    private final SubmissionDao submissionDao;
    private final SubmissionManager submissionManager;

    @Inject
    public SubmissionMigrator(HibernateSessionFactory sessionFactory,
                                SubmissionDao submissionDao,
                                SubmissionManager submissionManager) {
        this.sessionFactory = sessionFactory;
        this.submissionDao = submissionDao;
        this.submissionManager = submissionManager;
    }

    @Override
    protected void startUp() {
        final Runnable periodicProcess = new Runnable() {
            @Override
            public void run() {
                Session session = sessionFactory.openSession();
                try {
                    migrate10to11();
                    migrate11to12();
                    reorganizeFtp();
                } catch (Exception x) {
                    log.error("Submission watchdog process caught an exception:", x);
                } finally {
                    session.close();
                }
            }

        };

        scheduler.schedule(periodicProcess, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void shutDown() {
        scheduler.shutdown();
    }

    private void migrate10to11() throws DataSerializationException {
        Collection<Submission> submissions = submissionDao.getSubmissionsByVersion(ModelVersion.VERSION_1_0);

        for (Submission submission : submissions) {
            if (submission instanceof ExperimentSubmission) {
                migrateExperimentSubmission10((ExperimentSubmission)submission);
            }
        }
    }

    private void migrate11to12() throws DataSerializationException {
        Collection<Submission> submissions = submissionDao.getSubmissionsByVersion(ModelVersion.VERSION_1_1);

        for (Submission submission : submissions) {
            if (submission instanceof ExperimentSubmission) {
                migrateExperimentSubmission11((ExperimentSubmission)submission);
            }
        }
    }

    private void reorganizeFtp() {
        Collection<Submission> submissions = submissionDao.getSubmissions();

        for (Submission submission : submissions) {
            if (isNullOrEmpty(submission.getFtpSubDirectory())) {
                addFtpSubdirectory(submission);
            }
        }
    }

    @Transactional
    public void migrateExperimentSubmission10(ExperimentSubmission submission)
            throws DataSerializationException {
        ExperimentProfile exp = submission.getExperimentProfile();

        // build a map of file name -> md5 hash
        Map<String, String> hashes = new HashMap<String, String>();
        for (DataFile df : submission.getFiles()) {
            hashes.put(df.getName(), df.getDigest());
        }

        for (FileColumn column : exp.getFileColumns()) {
            for (String id : column.getLabeledExtractIds()) {
                FileRef file = column.getFileRef(id);
                if (null != file && isNullOrEmpty(file.getHash())) {
                    if (hashes.containsKey(file.getName())) {
                        file = new FileRef(file.getName(), hashes.get(file.getName()));
                        column.setFileRef(id, file);
                    }
                }
            }
        }
        submission.setVersion(ModelVersion.VERSION_1_1);
        submission.setExperimentProfile(exp);
        submissionManager.save(submission);
        log.info("Migrated experiment submission {} to version 1.1", submission.getId());
    }

    @Transactional
    public void migrateExperimentSubmission11(ExperimentSubmission submission)
            throws DataSerializationException {
        ExperimentProfile exp = submission.getExperimentProfile();

        submission.setVersion(ModelVersion.VERSION_1_2);
        submission.setExperimentProfile(exp);
        submissionManager.save(submission);
        log.info("Migrated experiment submission {} to version 1.2", submission.getId());
    }

    @Transactional
    public void addFtpSubdirectory(Submission submission) {
        String ftpSubDirectory = submissionManager.generateUniqueFtpSubDirectory(submission.getCreated());
        if (!isNullOrEmpty(submission.getAccession())) {
            ftpSubDirectory = ftpSubDirectory.replaceFirst("^[^-]+", submission.getAccession());
        }
        submission.setFtpSubDirectory(ftpSubDirectory);
        submissionManager.save(submission);
        log.info("Added FTP subdirectory {} to submission {}", ftpSubDirectory, submission.getId());
    }

}