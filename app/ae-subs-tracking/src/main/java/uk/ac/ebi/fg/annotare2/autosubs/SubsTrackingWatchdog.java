package uk.ac.ebi.fg.annotare2.autosubs;

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
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.db.om.Submission;
import uk.ac.ebi.fg.annotare2.db.om.enums.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.*;

public class SubsTrackingWatchdog {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final SubsTrackingProperties properties;
    private final HibernateSessionFactory sessionFactory;
    private final SubmissionDao submissionDao;


    @Inject
    public SubsTrackingWatchdog(SubsTrackingProperties subsTrackingProperties, HibernateSessionFactory sessionFactory,
                                SubmissionDao submissionDao) {
        this.properties = subsTrackingProperties;
        this.sessionFactory = sessionFactory;
        this.submissionDao = submissionDao;

        if (properties.getAeSubsTrackingEnabled()) {
            start();
        }
    }

    public void start() {
        final Runnable watchdogProcess = new Runnable() {
            @Override
            public void run() {
                sessionFactory.openSession();
                try {
                    Collection<Submission> submissions = submissionDao.getSubmissionsByStatus(SubmissionStatus.IN_CURATION);
                    for (Submission submission : submissions) {
                        System.out.println(submission.getId());
                    }
                } finally {
                    sessionFactory.close();
                }
            }
        };

        scheduler.scheduleAtFixedRate(watchdogProcess, 0, 1, MINUTES);
    }
}