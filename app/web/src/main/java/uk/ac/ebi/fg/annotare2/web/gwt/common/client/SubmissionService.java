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

package uk.ac.ebi.fg.annotare2.web.gwt.common.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.ArrayDesignDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.FtpFileInfo;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ExperimentUpdateCommand;

import java.util.List;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
@RemoteServiceRelativePath(SubmissionService.NAME)
public interface SubmissionService extends RemoteService {

    public static final String NAME = "submissionService";

    SubmissionDetails getSubmission(int id) throws ResourceNotFoundException, NoPermissionException;

    ArrayDesignDetailsDto getArrayDesignDetails(int id) throws ResourceNotFoundException, NoPermissionException;

    Table getIdfTable(int submissionId) throws NoPermissionException, ResourceNotFoundException;

    Table getSdrfTable(int submissionId) throws NoPermissionException, ResourceNotFoundException;

    int createExperimentSubmission() throws NoPermissionException;

    int createArrayDesignSubmission() throws NoPermissionException;

    void setupExperimentSubmission(int id, ExperimentSetupSettings settings) throws ResourceNotFoundException, NoPermissionException;

    void discardSubmissionData(int id) throws ResourceNotFoundException, NoPermissionException;

    ArrayDesignUpdateResult updateArrayDesign(int id, List<ArrayDesignUpdateCommand> commands) throws ResourceNotFoundException, NoPermissionException;

    ExperimentProfile loadExperiment(int id) throws ResourceNotFoundException, NoPermissionException;

    ExperimentProfile updateExperiment(int id, List<ExperimentUpdateCommand> commands) throws ResourceNotFoundException, NoPermissionException;

    List<DataFileRow> loadDataFiles(int id) throws ResourceNotFoundException, NoPermissionException;

    void uploadDataFile(int id, String fileName) throws ResourceNotFoundException, NoPermissionException;

    Map<Integer, String> registryFtpFiles(int id, List<FtpFileInfo> details) throws ResourceNotFoundException, NoPermissionException;

    void removeFile(int id, long fileId) throws ResourceNotFoundException, NoPermissionException;
}
