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

import com.google.inject.Inject;
import org.hibernate.Session;
import uk.ac.ebi.fg.annotare2.autosubs.SubsTracking;
import uk.ac.ebi.fg.annotare2.autosubs.SubsTrackingProperties;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.db.om.Submission;
import uk.ac.ebi.fg.annotare2.db.om.enums.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.web.server.transaction.Transactional;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MINUTES;

public class SubsTrackingWatchdog {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final HibernateSessionFactory sessionFactory;
    private final SubsTracking subsTracking;
    private final SubmissionDao submissionDao;
    private final SubmissionManager submissionManager;


    @Inject
    public SubsTrackingWatchdog(HibernateSessionFactory sessionFactory,
                                SubsTrackingProperties subsTrackingProperties, SubsTracking subsTracking,
                                SubmissionDao submissionDao, SubmissionManager submissionManager) {
        this.sessionFactory = sessionFactory;
        this.subsTracking = subsTracking;
        this.submissionDao = submissionDao;
        this.submissionManager = submissionManager;

        if (subsTrackingProperties.getAeSubsTrackingEnabled()) {
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
        Collection<Submission> submissions = submissionDao.getSubmissionsByStatus(SubmissionStatus.SUBMITTED);
        for (Submission submission : submissions) {
            if (!subsTracking.isInCuration(submission.getSubsTrackingId())) {
                submission.setStatus(SubmissionStatus.IN_PROGRESS);
                submission.setOwnedBy(submission.getCreatedBy());
                submissionManager.save(submission);
            }
        }
    }

}
