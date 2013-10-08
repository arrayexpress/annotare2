/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.common.io.Files;
import com.google.inject.Inject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.fg.annotare2.autosubs.SubsTracking;
import uk.ac.ebi.fg.annotare2.configmodel.ArrayDesignHeader;
import uk.ac.ebi.fg.annotare2.configmodel.DataSerializationException;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.dao.UserDao;
import uk.ac.ebi.fg.annotare2.db.om.ArrayDesignSubmission;
import uk.ac.ebi.fg.annotare2.db.om.DataFile;
import uk.ac.ebi.fg.annotare2.db.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.om.Submission;
import uk.ac.ebi.fg.annotare2.db.om.enums.Permission;
import uk.ac.ebi.fg.annotare2.db.om.enums.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import uk.ac.ebi.fg.annotare2.magetab.table.TsvParser;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.ArrayDesignDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.FtpFileInfo;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ExperimentUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.server.TransactionCallback;
import uk.ac.ebi.fg.annotare2.web.server.TransactionSupport;
import uk.ac.ebi.fg.annotare2.web.server.TransactionWrapException;
import uk.ac.ebi.fg.annotare2.web.server.login.AuthService;
import uk.ac.ebi.fg.annotare2.web.server.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.web.server.services.AccessControlException;
import uk.ac.ebi.fg.annotare2.web.server.services.DataFileManager;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;
import uk.ac.ebi.fg.annotare2.web.server.services.UploadedFiles;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.hash.Hashing.md5;
import static com.google.common.io.Files.hash;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.ExperimentUpdater.experimentUpdater;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.MageTabFormat.createMageTab;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.ExperimentBuilderFactory.createExperimentProfile;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter.*;

/**
 * @author Olga Melnichuk
 */
public class SubmissionServiceImpl extends SubmissionBasedRemoteService implements SubmissionService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionServiceImpl.class);

    private final DataFileManager dataFileManager;
    private final AnnotareProperties properties;
    private final TransactionSupport transactionSupport;
    private final SubsTracking subsTrackingDb;
    private final UserDao userDao;

    @Inject
    public SubmissionServiceImpl(AuthService authService, SubmissionManager submissionManager,
                                 DataFileManager dataFileManager, AnnotareProperties properties,
                                 TransactionSupport transactionSupport, SubsTracking subsTrackingDb, UserDao userDao) {
        super(authService, submissionManager);
        this.dataFileManager = dataFileManager;
        this.properties = properties;
        this.transactionSupport = transactionSupport;
        this.userDao = userDao;
        this.subsTrackingDb = subsTrackingDb;
    }

    @Override
    public SubmissionDetails getSubmission(long id) throws ResourceNotFoundException, NoPermissionException {
        Submission sb = getSubmission(id, Permission.VIEW);
        return uiSubmissionDetails(sb);
    }

    @Override
    public ArrayDesignDetailsDto getArrayDesignDetails(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ArrayDesignSubmission sb = getArrayDesignSubmission(id, Permission.VIEW);
            return uiArrayDesignDetails(sb);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    @Override
    public Table getIdfTable(long id) throws NoPermissionException, ResourceNotFoundException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.VIEW);
            ExperimentProfile exp = submission.getExperimentProfile();
            return toIdfTable(exp);
        } catch (IOException e) {
            throw unexpected(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (ParseException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    @Override
    public Table getSdrfTable(long id) throws NoPermissionException, ResourceNotFoundException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.VIEW);
            ExperimentProfile exp = submission.getExperimentProfile();
            return toSdrfTable(exp);
        } catch (IOException e) {
            throw unexpected(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (ParseException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    private Table toIdfTable(ExperimentProfile exp) throws IOException, ParseException {
        MageTabFormat mageTab = createMageTab(exp);
        return new TsvParser().parse(new FileInputStream(mageTab.getIdfFile()));
        //TODO: delete temporary file ?
    }

    private Table toSdrfTable(ExperimentProfile exp) throws IOException, ParseException {
        MageTabFormat mageTab = createMageTab(exp);
        return new TsvParser().parse(new FileInputStream(mageTab.getSdrfFile()));
        //TODO: delete temporary file ?
    }

    @Override
    public ExperimentProfile loadExperiment(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.VIEW);
            return submission.getExperimentProfile();
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    @Override
    public List<DataFileRow> loadDataFiles(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.VIEW);
            return uiDataFileRows(submission.getFiles());
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
    }

    @Override
    public long createExperiment() throws NoPermissionException {
        try {
            return transactionSupport.execute(new TransactionCallback<Long>() {
                @Override
                public Long doInTransaction() throws Exception {
                    return createExperimentSubmission().getId();
                }
            });
        } catch (TransactionWrapException e) {
            throw unexpected(maybeNoPermission(e.getCause()));
        }
    }

    @Override
    public long createArrayDesign() throws NoPermissionException {
        try {
            return transactionSupport.execute(new TransactionCallback<Long>() {
                @Override
                public Long doInTransaction() throws Exception {
                    return createArrayDesignSubmission().getId();
                }
            });
        } catch (TransactionWrapException e) {
            throw unexpected(maybeNoPermission(e.getCause()));
        }
    }

    @Override
    public void setupExperiment(final long id, final ExperimentSetupSettings settings) throws ResourceNotFoundException, NoPermissionException {
        try {
            transactionSupport.execute(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction() throws Exception {
                    ExperimentSubmission submission =
                            getExperimentSubmission(id, Permission.UPDATE);
                    submission.setExperimentProfile(createExperimentProfile(settings));
                    save(submission);
                    return null;
                }
            });
        } catch (TransactionWrapException e) {
            throw unexpected(maybeNoPermission(maybeNoSuchRecord(e.getCause())));
        }
    }

    @Override
    public void submitSubmission(final long id) throws ResourceNotFoundException, NoPermissionException {

        try {
            transactionSupport.execute(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction() throws Exception {
                    Submission submission = getSubmission(id, Permission.UPDATE);
                    File exportDir;

                    if (properties.getAeSubsTrackingEnabled()) {

                        Integer subsTrackingId = submission.getSubsTrackingId();
                        if (null == subsTrackingId) {
                            subsTrackingId = subsTrackingDb.addSubmission(submission);
                            submission.setSubsTrackingId(subsTrackingId);
                        } else {
                            subsTrackingDb.updateSubmission(submission);
                        }

                        exportDir = new File(properties.getAeSubsTrackingExportDir(), properties.getAeSubsTrackingUser());
                        if (!exportDir.exists()) {
                            exportDir.mkdir();
                        }
                        exportDir = new File(exportDir, properties.getAeSubsTrackingExperimentType() + "_" + String.valueOf(subsTrackingId));
                        if (!exportDir.exists()) {
                            exportDir.mkdir();
                        }
                    } else {
                        exportDir = properties.getExportDir();
                    }

                    if (submission instanceof ExperimentSubmission) {
                        exportSubmissionFiles((ExperimentSubmission)submission, exportDir);
                    }
                    if (properties.getAeSubsTrackingEnabled()) {
                        subsTrackingDb.sendSubmission(submission.getSubsTrackingId());
                    }
                    submission.setStatus(SubmissionStatus.SUBMITTED);
                    submission.setOwnedBy(userDao.getCuratorUser());
                    save(submission);
                    return null;
                }
            });
        } catch (TransactionWrapException e) {
            throw unexpected(maybeNoPermission(maybeNoSuchRecord(e.getCause())));
        }
    }

    @Override
    public void discardSubmissionData(final long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            transactionSupport.execute(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction() throws Exception {
                    Submission submission =
                            getSubmission(id, Permission.UPDATE);
                    submission.discardAll();
                    save(submission);
                    return null;
                }
            });
        } catch (TransactionWrapException e) {
            throw unexpected(maybeNoPermission(maybeNoSuchRecord(e.getCause())));
        }
    }

    @Override
    public ExperimentProfile updateExperiment(final long id, final List<ExperimentUpdateCommand> commands) throws ResourceNotFoundException, NoPermissionException {
        try {
            return transactionSupport.execute(new TransactionCallback<ExperimentProfile>() {
                @Override
                public ExperimentProfile doInTransaction() throws Exception {
                    ExperimentSubmission submission = getExperimentSubmission(id, Permission.UPDATE);
                    ExperimentProfile experiment = submission.getExperimentProfile();
                    experimentUpdater(experiment).run(commands);
                    submission.setExperimentProfile(experiment);
                    submission.setTitle(experiment.getTitle());
                    save(submission);
                    return experiment;
                }
            });
        } catch (TransactionWrapException e) {
            throw unexpected(maybeNoPermission(maybeNoSuchRecord(e.getCause())));
        }
    }

    @Override
    public ArrayDesignUpdateResult updateArrayDesign(final long id, final List<ArrayDesignUpdateCommand> commands) throws ResourceNotFoundException, NoPermissionException {
        try {
            return transactionSupport.execute(new TransactionCallback<ArrayDesignUpdateResult>() {
                @Override
                public ArrayDesignUpdateResult doInTransaction() throws Exception {
                    ArrayDesignSubmission submission = getArrayDesignSubmission(id, Permission.UPDATE);
                    ArrayDesignHeader header = submission.getHeader();
                    ArrayDesignUpdateResult result = new ArrayDesignUpdatePerformerImpl(header).run(commands);
                    submission.setHeader(header);
                    submission.setTitle(header.getName());
                    save(submission);
                    return result;
                }
            });
        } catch (TransactionWrapException e) {
            throw unexpected(maybeNoPermission(maybeNoSuchRecord(e.getCause())));
        }
    }

    @Override
    public void uploadDataFile(long id, String fileName) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.UPDATE);
            FileItem fileItem = UploadedFiles.get(getSession(), fileName);
            File file = new File(properties.getHttpUploadDir(), fileName);
            Streams.copy(fileItem.getInputStream(), new FileOutputStream(file), true);
            saveFile(file, submission);
        } catch (FileNotFoundException e) {
            throw unexpected(e);
        } catch (IOException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    @Override
    public Map<Integer, String> registryFtpFiles(long id, List<FtpFileInfo> details) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.UPDATE);
            File ftpRoot = properties.getFilePickUpDir();
            Map<Integer, String> errors = new HashMap<Integer, String>();
            int index = 0;
            for (FtpFileInfo info : details) {
                File file = new File(ftpRoot, info.getFileName());
                if (fileExists(file, info.getMd5())) {
                    saveFile(file, submission);
                } else {
                    errors.put(index, "File not found");
                }
                index++;
            }
            return errors;
        } catch (FileNotFoundException e) {
            throw unexpected(e);
        } catch (IOException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    @Override
    public void removeFile(final long id, final long fileId) throws ResourceNotFoundException, NoPermissionException {
        try {
            transactionSupport.execute(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction() throws Exception {
                    ExperimentSubmission submission = getExperimentSubmission(id, Permission.UPDATE);
                    DataFile dataFile = dataFileManager.get(fileId);
                    String fileName = dataFile.getName();
                    if (dataFileManager.removeFile(submission, dataFile)) {
                        submission.getExperimentProfile().removeFile(fileName);
                        save(submission);
                    }
                    return null;
                }
            });
        } catch (TransactionWrapException e) {
            throw unexpected(maybeNoPermission(maybeNoSuchRecord(e.getCause())));
        }
    }

    private void saveFile(final File file, final ExperimentSubmission submission) {
        String fileName = file.getName();
        Set<DataFile> files = submission.getFiles();
        for (DataFile dataFile : files) {
            if (fileName.equals(dataFile.getName())) {
                // TODO: show message: file with such name already exists
                return;
            }
        }

        try {
            transactionSupport.execute(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction() throws Exception {
                    dataFileManager.upload(file, submission);
                    save(submission);
                    return null;
                }
            });
        } catch (TransactionWrapException e) {
            throw unexpected(e.getCause());
        }
    }

    private void exportSubmissionFiles( ExperimentSubmission submission, File exportDirectory ) {
        try {
            ExperimentProfile exp = submission.getExperimentProfile();
            String accession = null == submission.getAccession() ? "submission" : submission.getAccession();
            MageTabFormat mageTab = createMageTab(exp);
            // copy idf
            String fileName = accession + ".idf.txt";
            Files.copy(mageTab.getIdfFile(), new File(exportDirectory, fileName));
            if (properties.getAeSubsTrackingEnabled()) {
                subsTrackingDb.deleteFiles(submission.getSubsTrackingId());
                subsTrackingDb.addMageTabFile(submission.getSubsTrackingId(), fileName);
            }
            // copy sdrf
            fileName = accession + ".sdrf.txt";
            Files.copy(mageTab.getSdrfFile(), new File(exportDirectory, fileName));
            if (properties.getAeSubsTrackingEnabled()) {
                subsTrackingDb.addMageTabFile(submission.getSubsTrackingId(), fileName);
            }
            // copy data files
            Set<DataFile> dataFiles = submission.getFiles();
            for (DataFile dataFile : dataFiles) {
                Files.copy(dataFileManager.getFile(dataFile), new File(exportDirectory, dataFile.getName()));
                if (properties.getAeSubsTrackingEnabled()) {
                    subsTrackingDb.addMageTabFile(submission.getSubsTrackingId(), dataFile.getName());
                }
            }
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (ParseException e) {
            throw unexpected(e);
        } catch (IOException e) {
            throw unexpected(e);
        }
    }


    private static boolean fileExists(File file, String md5) throws IOException {
        return file.exists() && md5.equals(hash(file, md5()).toString());
    }
}
