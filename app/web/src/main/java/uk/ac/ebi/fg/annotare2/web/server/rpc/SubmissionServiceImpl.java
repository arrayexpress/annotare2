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

import com.google.gwt.user.server.rpc.UnexpectedException;
import com.google.inject.Inject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.fg.annotare2.configmodel.ArrayDesignHeader;
import uk.ac.ebi.fg.annotare2.configmodel.DataSerializationException;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import uk.ac.ebi.fg.annotare2.magetab.table.TsvParser;
import uk.ac.ebi.fg.annotare2.om.ArrayDesignSubmission;
import uk.ac.ebi.fg.annotare2.om.DataFile;
import uk.ac.ebi.fg.annotare2.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.om.enums.Permission;
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
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.ExperimentBuilderFactory.createExperiment;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter.*;

/**
 * @author Olga Melnichuk
 */
public class SubmissionServiceImpl extends AuthBasedRemoteService implements SubmissionService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionServiceImpl.class);

    private final SubmissionManager submissionManager;
    private final DataFileManager dataFileManager;
    private final AnnotareProperties properties;

    @Inject
    public SubmissionServiceImpl(AuthService authService, SubmissionManager submissionManager,
                                 DataFileManager dataFileManager, AnnotareProperties properties) {
        super(authService);
        this.submissionManager = submissionManager;
        this.dataFileManager = dataFileManager;
        this.properties = properties;
    }

    @Override
    public SubmissionDetails getSubmission(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission sb = submissionManager.getSubmission(getCurrentUser(), id, Permission.VIEW);
            return uiSubmissionDetails(sb);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.VIEW);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    @Override
    public ArrayDesignDetailsDto getArrayDesignDetails(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ArrayDesignSubmission sb = submissionManager.getArrayDesignSubmission(getCurrentUser(), id, Permission.VIEW);
            return uiArrayDesignDetails(sb);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.VIEW);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    @Override
    public Table getIdfTable(long id) throws NoPermissionException, ResourceNotFoundException {
        try {
            ExperimentSubmission submission = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.VIEW);
            ExperimentProfile exp = submission.getExperimentProfile();
            return toIdfTable(exp);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.VIEW);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (IOException e) {
            throw unexpected(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (ParseException e) {
            throw unexpected(e);
        }
    }

    @Override
    public Table getSdrfTable(long id) throws NoPermissionException, ResourceNotFoundException {
        try {
            ExperimentSubmission submission = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.VIEW);
            ExperimentProfile exp = submission.getExperimentProfile();
            return toSdrfTable(exp);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.VIEW);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (IOException e) {
            throw unexpected(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (ParseException e) {
            throw unexpected(e);
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
    public long createExperimentSubmission() throws NoPermissionException {
        try {
            return submissionManager.createExperimentSubmission(getCurrentUser()).getId();
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.CREATE);
        }
    }

    @Override
    public long createArrayDesignSubmission() throws NoPermissionException {
        try {
            return submissionManager.createArrayDesignSubmission(getCurrentUser()).getId();
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.CREATE);
        }
    }

    @Override
    public void setupExperimentSubmission(long id, ExperimentSetupSettings settings) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission =
                    submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.UPDATE);
            submission.setExperimentProfile(createExperiment(settings));
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.UPDATE);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    @Override
    public void discardSubmissionData(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission =
                    submissionManager.getSubmission(getCurrentUser(), id, Permission.UPDATE);
            submission.discardAll();
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.UPDATE);
        }
    }

    @Override
    public ExperimentProfile updateExperiment(long id, List<ExperimentUpdateCommand> commands) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.UPDATE);
            ExperimentProfile experiment = submission.getExperimentProfile();
            experimentUpdater(experiment).run(commands);
            submission.setExperimentProfile(experiment);
            return experiment;
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.UPDATE);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    @Override
    public ArrayDesignUpdateResult updateArrayDesign(long id, List<ArrayDesignUpdateCommand> commands) throws ResourceNotFoundException, NoPermissionException {
        try {
            ArrayDesignSubmission submission = submissionManager.getArrayDesignSubmission(getCurrentUser(), id, Permission.UPDATE);
            ArrayDesignHeader header = submission.getHeader();
            ArrayDesignUpdateResult result = new ArrayDesignUpdatePerformerImpl(header).run(commands);
            submission.setHeader(header);
            return result;
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.UPDATE);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    @Override
    public ExperimentProfile loadExperiment(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.VIEW);
            return submission.getExperimentProfile();
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.VIEW);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    @Override
    public List<DataFileRow> loadDataFiles(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.VIEW);
            return uiDataFileRows(submission.getFiles());
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.VIEW);
        }
    }

    @Override
    public void uploadDataFile(long id, String fileName) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.UPDATE);
            FileItem fileItem = UploadedFiles.get(getSession(), fileName);
            File file = new File(properties.getHttpUploadDir(), fileName);
            Streams.copy(fileItem.getInputStream(), new FileOutputStream(file), true);
            saveFile(file, submission);
            //TODO: save submission
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.UPDATE);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (FileNotFoundException e) {
            throw unexpected(e);
        } catch (IOException e) {
            throw unexpected(e);
        }
    }

    @Override
    public Map<Integer, String> registryFtpFiles(long id, List<FtpFileInfo> details) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.UPDATE);
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
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.UPDATE);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (FileNotFoundException e) {
            throw unexpected(e);
        } catch (IOException e) {
            throw unexpected(e);
        }
    }

    @Override
    public void removeFile(long id, long fileId) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.UPDATE);
            dataFileManager.removeFile(submission, fileId);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.UPDATE);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    private void saveFile(File file, ExperimentSubmission submission) {
        String fileName = file.getName();
        Set<DataFile> files = submission.getFiles();
        for(DataFile dataFile : files) {
            if (fileName.equals(dataFile.getName())) {
                // TODO: show message: file with such name already exists
                return;
            }
        }

        DataFile dataFile = dataFileManager.upload(file);
        files.add(dataFile);
        //TODO: save submission
    }
    private static boolean fileExists(File file, String md5) throws IOException {
        return file.exists() && md5.equals(hash(file, md5()).toString());
    }

    private static UnexpectedException unexpected(Exception e) {
        log.error("server error", e);
        return new UnexpectedException("Unexpected server error", e);
    }

    private static ResourceNotFoundException noSuchRecord(RecordNotFoundException e) {
        log.error("server error", e);
        return new ResourceNotFoundException("Submission not found");
    }

    private static NoPermissionException noPermission(AccessControlException e, Permission permission) {
        log.error("server error", e);
        return new NoPermissionException("Sorry you do not have permission to '" + permission + "' the submission");
    }
}
