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
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ExperimentUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.server.login.AuthService;
import uk.ac.ebi.fg.annotare2.web.server.services.AccessControlException;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;
import uk.ac.ebi.fg.annotare2.web.server.services.UploadedFiles;
import uk.ac.ebi.fg.annotare2.web.server.services.datafiles.DataFileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

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

    @Inject
    public SubmissionServiceImpl(AuthService authService, SubmissionManager submissionManager,
                                 DataFileManager dataFileManager) {
        super(authService);
        this.submissionManager = submissionManager;
        this.dataFileManager = dataFileManager;
    }

    @Override
    public SubmissionDetails getSubmission(int id) throws ResourceNotFoundException, NoPermissionException {
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
    public ArrayDesignDetailsDto getArrayDesignDetails(int id) throws ResourceNotFoundException, NoPermissionException {
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
    public Table getIdfTable(int id) throws NoPermissionException, ResourceNotFoundException {
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
    public Table getSdrfTable(int id) throws NoPermissionException, ResourceNotFoundException {
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
    public int createExperimentSubmission() throws NoPermissionException {
        try {
            return submissionManager.createExperimentSubmission(getCurrentUser()).getId();
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.CREATE);
        }
    }

    @Override
    public int createArrayDesignSubmission() throws NoPermissionException {
        try {
            return submissionManager.createArrayDesignSubmission(getCurrentUser()).getId();
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.CREATE);
        }
    }

    @Override
    public void setupExperimentSubmission(int id, ExperimentSetupSettings settings) throws ResourceNotFoundException, NoPermissionException {
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
    public void discardSubmissionData(int id) throws ResourceNotFoundException, NoPermissionException {
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
    public ExperimentProfile updateExperiment(int id, List<ExperimentUpdateCommand> commands) throws ResourceNotFoundException, NoPermissionException {
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
    public ArrayDesignUpdateResult updateArrayDesign(int id, List<ArrayDesignUpdateCommand> commands) throws ResourceNotFoundException, NoPermissionException {
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
    public ExperimentProfile loadExperiment(int id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.VIEW);
            return submission.getExperimentProfile();
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.UPDATE);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    @Override
    public List<DataFileRow> loadDataFiles(int id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.VIEW);
            return uiDataFileRows(submission.getFiles());
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.UPDATE);
        }
    }

    @Override
    public void uploadDataFile(int id, String fileName) throws ResourceNotFoundException, NoPermissionException {
        //TODO
        try {
            FileItem fileItem = UploadedFiles.get(getSession(), fileName);
            File tmp = File.createTempFile("annotare", "upload");
            fileItem.write(tmp);
            dataFileManager.upload(tmp);
        } catch (FileNotFoundException e) {
            throw unexpected(e);
        } catch (Exception e) {
            throw unexpected(e);
        }
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
