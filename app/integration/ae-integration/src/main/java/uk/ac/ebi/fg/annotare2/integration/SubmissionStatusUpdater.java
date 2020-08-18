package uk.ac.ebi.fg.annotare2.integration;

import com.google.inject.Inject;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.core.components.SubmissionManager;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.enums.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SubmissionStatusUpdater {
    private static final Logger logger = LoggerFactory.getLogger(SubmissionStatusUpdater.class);

    private final BlockingQueue<Submission> submissionsQueue;
    private final SubmissionManager submissionManager;
    private final ScheduledExecutorService scheduler;
    private final HibernateSessionFactory sessionFactory;

    @Inject
    public SubmissionStatusUpdater(SubmissionManager submissionManager,
                                   HibernateSessionFactory sessionFactory){
        this.submissionManager = submissionManager;
        this.sessionFactory = sessionFactory;
        submissionsQueue = new LinkedBlockingQueue<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    @PostConstruct
    public void init(){
        final Runnable updateStatus = new Runnable() {
            @Override
            public void run() {
                if(!submissionsQueue.isEmpty()){
                    Session session = sessionFactory.openSession();
                    try {
                        Submission submission = submissionsQueue.take();
                        submission.setStatus(SubmissionStatus.IN_CURATION);
                        submissionManager.save(submission);
                        logger.debug("Submission: {} status updated to IN_CURATION", submission.getId());
                    } catch (InterruptedException e) {
                        logger.error("Error while updating submission status", e);
                    } finally {
                        session.close();
                    }
                }
            }
        };

        scheduler.scheduleAtFixedRate(updateStatus,1000, 1000, TimeUnit.MILLISECONDS);
    }

    public synchronized void add(Submission submission){
        submissionsQueue.add(submission);
    }

}
