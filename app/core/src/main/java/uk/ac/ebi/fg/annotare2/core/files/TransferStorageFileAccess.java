package uk.ac.ebi.fg.annotare2.core.files;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.core.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.core.utils.LinuxShellCommandExecutor;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TransferStorageFileAccess implements RemoteFileAccess, Serializable {

    private static final long serialVersionUID = 752647115562277515L;
    private static final Logger log = LoggerFactory.getLogger(TransferStorageFileAccess.class);

    private final AnnotareProperties annotareProperties;
    private final Configuration freemarkerConfig;

    public TransferStorageFileAccess(AnnotareProperties annotareProperties) {
        this.annotareProperties = annotareProperties;
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_34);
        freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/");
    }

    @Override
    public boolean isSupported(URI file) {
        return "scp".equalsIgnoreCase(file.getScheme());
    }

    @Override
    public boolean isAccessible(URI file) throws IOException {
        String commands = "[ -f " + file.getPath() + " ] && echo 'exists' || echo 'not_exists'";
        SlurmJobResult jobResult = executeSlurmJob(commands, file.getPath(), "isAccessible");
        try {
            String output = new String(Files.readAllBytes(jobResult.getOutputFile()));
            return output.trim().equals("exists");
        } finally {
            jobResult.cleanup();
        }
    }

    @Override
    public String getDigest(URI file) throws IOException {
        String commands = "md5sum " + file.getPath() + " | awk '{print $1}'";
        SlurmJobResult jobResult = executeSlurmJob(commands, file.getPath(), "getDigest");
        try {
            String output = new String(Files.readAllBytes(jobResult.getOutputFile()));
            if (output.trim().isEmpty()) {
                throw new IOException("Failed to get digest for file: " + file.getPath() + ". Slurm job output was empty.");
            }
            return output.trim();
        } finally {
            jobResult.cleanup();
        }
    }

    @Override
    public void copy(URI file, URI destination) throws IOException {
        String commands = "cp " + file.getPath() + " " + destination.getPath();
        SlurmJobResult jobResult = executeSlurmJob(commands, file.getPath(), "copy");
        jobResult.checkForErrors();
        jobResult.cleanup();
    }

    @Override
    public URI rename(URI file, String newName) throws IOException {
        URI newUri;
        try {
            Path oldPath = new File(file.getPath()).toPath();
            Path newPath = oldPath.resolveSibling(newName);
            newUri = newPath.toUri();
        } catch (Exception e) {
            throw new IOException("Failed to construct new URI for rename operation: " + e.getMessage(), e);
        }
        String commands = "mv " + file.getPath() + " " + newUri.getPath();
        SlurmJobResult jobResult = executeSlurmJob(commands, file.getPath(), "rename");
        jobResult.checkForErrors();
        jobResult.cleanup();
        return newUri;
    }

    @Override
    public void delete(URI file) throws IOException {
        String commands = "rm " + file.getPath();
        SlurmJobResult jobResult = executeSlurmJob(commands, file.getPath(), "delete");
        jobResult.checkForErrors();
        jobResult.cleanup();
    }

    public void createDirectory(URI directory) throws IOException {
        String commands = "mkdir -p -m 775 " + directory.getPath();
        SlurmJobResult jobResult = executeSlurmJob(commands, directory.getPath(), "delete");
        jobResult.checkForErrors();
        jobResult.cleanup();
    }

    public List<String> listFiles(URI file) throws IOException {
        if (!isSupported(file)) {
            throw new IOException("Unsupported URI scheme: " + file.getScheme());
        }
        Path parentPath = Paths.get(file.getPath()).getParent();
        if (parentPath == null) {
            throw new IOException("Cannot get parent directory for URI: " + file);
        }
        String commands = "ls -l " + parentPath;
        SlurmJobResult jobResult = executeSlurmJob(commands, file.getPath(), "listFiles");
        jobResult.checkForErrors();
        try {
            String output = new String(Files.readAllBytes(jobResult.getOutputFile()));
            List<String> fileList = new ArrayList<>();
            if (!output.trim().isEmpty()) {
                String[] files = output.split("\\r?\\n");
                for (String fileName : files) {
                    if (!fileName.trim().isEmpty()) {
                        fileList.add(fileName.trim());
                    }
                }
            }
            return fileList;
        } finally {
            jobResult.cleanup();
        }
    }

    private SlurmJobResult executeSlurmJob(String commands, String filePath, String operation) throws IOException {
        Path tempScriptFile = null;
        Path outputDir = Paths.get(annotareProperties.getSlurmOutputDirectory());
        String uniqueId = UUID.randomUUID().toString();
        String baseFileName = new File(filePath).getParentFile().getName().replaceAll("[^a-zA-Z0-9_.-]", "_") + "_" + uniqueId;
        Path outputFile = outputDir.resolve(baseFileName + ".out");
        Path errorFile = outputDir.resolve(baseFileName + ".err");

        try {
            tempScriptFile = Files.createTempFile("slurm_job_", ".sh");
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("commands", commands);
            templateData.put("jobName", operation);
            templateData.put("outputFile", outputFile);
            templateData.put("errorFile", errorFile);

            Template template = freemarkerConfig.getTemplate("slurm_batch_job_template.ftl");
            try (FileWriter writer = new FileWriter(tempScriptFile.toFile())) {
                template.process(templateData, writer);
            } catch (Exception e) {
                throw new IOException("Failed to process FreeMarker template: " + e.getMessage(), e);
            }

            String jobSubmitterScript = annotareProperties.getJobSubmitterScriptPath();
            if (jobSubmitterScript == null || jobSubmitterScript.trim().isEmpty()) {
                throw new IOException("Slurm job submitter script path is not configured.");
            }

            // Execute the job submitter script with the temporary script path
            LinuxShellCommandExecutor executor = new LinuxShellCommandExecutor();
            executor.execute(jobSubmitterScript + " " + tempScriptFile.toAbsolutePath());

            if (!StringUtils.isEmpty(executor.getErrors())) {
                throw new IOException("Slurm job submission failed: " + executor.getErrors());
            }

            // Wait for job completion (simple polling)
            int maxAttempts = 6; // Wait up to 1 minute
            int attempt = 0;
            while (!Files.exists(outputFile) && !Files.exists(errorFile) && attempt < maxAttempts) {
                TimeUnit.SECONDS.sleep(10);
                attempt++;
            }

            if (!Files.exists(outputFile) && !Files.exists(errorFile)) {
                throw new IOException("Slurm job output files not found after waiting.");
            }
            return new SlurmJobResult(outputFile, errorFile, tempScriptFile);
        } catch (Exception e) {
            throw new IOException("Failed to execute Slurm job for operation '" + operation + "' on file " + filePath + ": " + e.getMessage(), e);
        } finally {
            if (tempScriptFile != null) {
                Files.deleteIfExists(tempScriptFile);
            }
        }
    }

    private static class SlurmJobResult {
        private final Path outputFile;
        private final Path errorFile;
        private final Path tempScriptFile;

        public SlurmJobResult(Path outputFile, Path errorFile, Path tempScriptFile) {
            this.outputFile = outputFile;
            this.errorFile = errorFile;
            this.tempScriptFile = tempScriptFile;
        }

        public Path getOutputFile() {
            return outputFile;
        }

        public void checkForErrors() throws IOException {
            if (Files.exists(errorFile)) {
                String errors = new String(Files.readAllBytes(errorFile));
                if (!errors.trim().isEmpty()) {
                    throw new IOException("Slurm job reported errors:\n" + errors);
                }
            }
        }

        public void cleanup() {
            try {
                Files.deleteIfExists(outputFile);
                Files.deleteIfExists(errorFile);
                Files.deleteIfExists(tempScriptFile);
            } catch (IOException e) {
                log.warn("Failed to cleanup Slurm job result: " + e.getMessage(), e);
            }
        }
    }
}