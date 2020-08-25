package uk.ac.ebi.fg.annotare2.integration;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.core.components.Messenger;
import uk.ac.ebi.fg.annotare2.core.components.SubmissionManager;
import uk.ac.ebi.fg.annotare2.core.transaction.Transactional;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.enums.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SubmissionPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(SubmissionPostProcessor.class);

    private final BlockingQueue<Pair<Submission, AeIntegrationWatchdog.SubmissionOutcome>> submissionsQueue;
    private final SubmissionManager submissionManager;
    private final ScheduledExecutorService scheduler;
    private final HibernateSessionFactory sessionFactory;
    private final ExtendedAnnotareProperties properties;
    private final Messenger messenger;

    @Inject
    public SubmissionPostProcessor(SubmissionManager submissionManager,
                                   HibernateSessionFactory sessionFactory,
                                   ExtendedAnnotareProperties properties,
                                   Messenger messenger){
        this.submissionManager = submissionManager;
        this.sessionFactory = sessionFactory;
        this.properties = properties;
        this.messenger = messenger;
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
                        processSubmission();
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

    @Transactional
    public void processSubmission() throws InterruptedException {
        Pair<Submission, AeIntegrationWatchdog.SubmissionOutcome> submissionsPair = submissionsQueue.take();
        Submission submission = submissionsPair.getLeft();
        submission.setStatus(SubmissionStatus.IN_CURATION);
        submissionManager.save(submission);
        logger.debug("Submission: {} status updated to IN_CURATION", submission.getId());

        boolean hasResubmitted = SubmissionStatus.RESUBMITTED == submission.getStatus();
        if (properties.isSubsTrackingEnabled()) {
            String otrsTemplate = (AeIntegrationWatchdog.SubmissionOutcome.INITIAL_SUBMISSION_OK == submissionsPair.getRight()) ?
                    EmailTemplates.INITIAL_SUBMISSION_OTRS_TEMPLATE : EmailTemplates.REPEAT_SUBMISSION_OTRS_TEMPLATE;

            String submissionType = "";
            if (submission instanceof ExperimentSubmission) {
                try {
                    ExperimentProfile exp = ((ExperimentSubmission) submission).getExperimentProfile();
                    if (exp.getType().isSequencing()) {
                        submissionType = "HTS";
                    } else {
                        submissionType = "MA";
                    }
                } catch (DataSerializationException x) {
                    logger.error("Error while getting experiment profile.", x);
                }
            }

            sendEmail(
                    otrsTemplate,
                    new ImmutableMap.Builder<String, String>()
                            .put("to.name", submission.getCreatedBy().getName())
                            .put("to.email", submission.getCreatedBy().getEmail())
                            .put("submission.id", String.valueOf(submission.getId()))
                            .put("submission.title", submission.getTitle())
                            .put("submission.date", submission.getUpdated().toString())
                            .put("submission.type", submissionType)
                            .put("subsTracking.user", properties.getSubsTrackingUser())
                            .put("subsTracking.experiment.type", properties.getSubsTrackingExperimentType())
                            .put("subsTracking.experiment.id", String.valueOf(submission.getSubsTrackingId()))
                            .build(),
                    submission
            );
        }
        if (!hasResubmitted) {
            sendEmail(
                    EmailTemplates.INITIAL_SUBMISSION_TEMPLATE,
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
    }

    public synchronized void add(Pair<Submission, AeIntegrationWatchdog.SubmissionOutcome> submission){
        submissionsQueue.add(submission);
    }

    private void sendEmail(String template, Map<String, String> params, Submission submission) {
        try {
            messenger.send(template, params, submission.getCreatedBy(), submission);
        } catch (RuntimeException e) {
            logger.error("Unable to send email", e);
        }
    }
}
