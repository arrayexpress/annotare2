package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.core.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.core.utils.LinuxShellCommandExecutor;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionExceptionDao;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.SubmissionException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class DataStoreManager {
    private static final Logger logger = LoggerFactory.getLogger(DataStoreManager.class);
    public final AnnotareProperties annotareProperties;
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(1);
    private final LinuxShellCommandExecutor shellCommandExecutor = new LinuxShellCommandExecutor();
    private final SubmissionExceptionDao submissionExceptionDao;
    private String jobStatus;
    private final BlockingQueue<Submission> submissionQueue = new LinkedBlockingQueue<>();
    private volatile boolean isProcessing = false;


    @Inject
    public DataStoreManager(AnnotareProperties annotareProperties, SubmissionExceptionDao submissionExceptionDao) {
        this.annotareProperties = annotareProperties;
        this.submissionExceptionDao = submissionExceptionDao;
        startQueueProcessor();
    }

    private void startQueueProcessor() {
        EXECUTOR.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            Submission submission = submissionQueue.take();
                            isProcessing = true;
                            logger.info("Processing submission " + submission.getId() + " from queue");
                            processSubmission(submission);
                            isProcessing = false;
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            logger.warn("Queue processor interrupted: " + e.getMessage());
                            break;
                        } catch (Exception e) {
                            logger.error("Error in queue processor", e);
                            isProcessing = false;
                        }
                    }
                } finally {
                    logger.info("Queue processor shutting down");
                }
            }
        });
        logger.info("Data store queue processor started");
    }

    private <T extends Submission> void processSubmission(T submission) {
        try {
            final String command = buildDataStoreCommand(submission.getId(), submission.getFtpSubDirectory());
            boolean success = shellCommandExecutor.execute(command);
            if (success) {
                String output = shellCommandExecutor.getOutput();
                jobStatus = parseSlurmOutputAndGetStatus(output);
                if (jobStatus == null || !jobStatus.equalsIgnoreCase("COMPLETED")) {
                    logErrorMessage(submission, "Failed to create data store or FTP sub directory", null);
                } else {
                    logger.info("Successfully created data store for submission " + submission.getId());
                    notifyGlobusTransferApi(submission);
                }
            } else {
                logErrorMessage(submission, "Error while submitting data store create slurm job", null);
            }
        } catch (final IOException e) {
            logErrorMessage(submission, "Error while submitting data store create slurm job", e);
        }
    }


    public <T extends Submission> void createDataStoreAsync(T submission) {
        submissionQueue.add(submission);
        logger.info("Added submission " + submission.getId() + " to processing queue. Queue size: " + submissionQueue.size());
    }

    private <T extends Submission> void logErrorMessage(T submission, String exceptionMessage, Exception error) {
        SubmissionException exception = new SubmissionException();
        exception.setSubmission(submission);
        exception.setExceptionMessage(null != error ? exceptionMessage+ ": " + error.getMessage() : exceptionMessage);
        exception.setExceptionStackTrace(null != error ? Arrays.toString(error.getStackTrace()) : null);
        exception.setFixed(false);
        submissionExceptionDao.save(exception);
    }

    private String buildDataStoreCommand(Long submissionId, String ftpSubDirectory) {
        return annotareProperties.getCreateDataStoreScript() + " " + submissionId + " " + ftpSubDirectory;
    }

    private String parseSlurmOutputAndGetStatus(String output) {
        if (output != null) {
            return Arrays.stream(output.split(System.lineSeparator()))
                    .filter(line -> line.contains("finished with status"))
                    .map(line -> {
                        String[] parts = line.split(":");
                        return parts.length > 1 ? parts[1].trim() : "";
                    })
                    .findFirst().orElse(null);
        }
        return null;
    }

    public int getQueueSize() {
        return submissionQueue.size();
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    public void shutdown() {
        EXECUTOR.shutdownNow();
    }

    private <T extends Submission> void notifyGlobusTransferApi(T submission) {
        try {
            String globusApiUrl = annotareProperties.getGlobusTransferAPIURL();
            String endpoint = globusApiUrl + "/globus-collection/" + submission.getId();

            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Send empty POST request
            connection.getOutputStream().close();

            int responseCode = connection.getResponseCode();
            if (responseCode == 202) {
                logger.info("Globus collection creation initiated for submission " + submission.getId());
            } else {
                logger.error("Failed to initiate Globus collection creation for submission " + 
                             submission.getId() + ", response code: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            logger.error("Error calling Globus Transfer API for submission " + submission.getId(), e);
        }
    }

}