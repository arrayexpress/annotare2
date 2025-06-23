package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.gwt.core.client.Scheduler;
import uk.ac.ebi.fg.annotare2.core.UnexpectedException;
import uk.ac.ebi.fg.annotare2.core.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.core.utils.LinuxShellCommandExecutor;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionExceptionDao;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.SubmissionException;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataStoreManager {
    public final AnnotareProperties annotareProperties;
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(1);
    private final LinuxShellCommandExecutor shellCommandExecutor = new LinuxShellCommandExecutor();
    private final SubmissionExceptionDao submissionExceptionDao;
    private String jobStatus;

    public DataStoreManager(AnnotareProperties annotareProperties, SubmissionExceptionDao submissionExceptionDao) {
        this.annotareProperties = annotareProperties;
        this.submissionExceptionDao = submissionExceptionDao;
    }

    public <T extends Submission> void createDataStoreAsync(T submission) {
        EXECUTOR.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final String command = buildDataStoreCommand(submission.getId(), submission.getFtpSubDirectory());
                    boolean success = shellCommandExecutor.execute(command);
                    if (success) {
                        String output = shellCommandExecutor.getOutput();
                        jobStatus = parseSlurmOutputAndGetStatus(output);
                        if(!jobStatus.equalsIgnoreCase("COMPLETED")) {
                            logErrorMessage(submission, "Failed to create data store or FTP sub directory", null);
                        }
                    }
                    else{
                        logErrorMessage(submission, "Error while submitting data store create slurm job", null);
                    }
                } catch (final IOException e) {
                    logErrorMessage(submission, "Error while submitting data store create slurm job", e);
                }
            }
        });

        try {
            shellCommandExecutor.execute(annotareProperties.getCreateDataStoreScript() + " " + submission.getId() + " " + submission.getFtpSubDirectory());
        } catch (IOException e) {
            throw new UnexpectedException("Failed to create data store for submission " + submission.getId(), e);
        }
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
}