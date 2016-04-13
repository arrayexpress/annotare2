/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.common.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.ArrayDesignDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ExperimentUpdateCommand;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
@RemoteServiceRelativePath(SubmissionService.NAME)
public interface SubmissionService extends RemoteService {

    String NAME = "submissionService";

    SubmissionDetails getSubmissionDetails(long id) throws ResourceNotFoundException, NoPermissionException;

    ArrayDesignDetailsDto getArrayDesignDetails(long id) throws ResourceNotFoundException, NoPermissionException;

    Table getIdfTable(long id) throws ResourceNotFoundException, NoPermissionException;

    Table getSdrfTable(long id) throws ResourceNotFoundException, NoPermissionException;

    String getGeneratedSamplesPreview(long id, int numOfSamples, String namingPattern, int startingNumber) throws ResourceNotFoundException, NoPermissionException;

    void setupExperiment(long id, ExperimentSetupSettings settings) throws ResourceNotFoundException, NoPermissionException;

    void assignSubmissionToMe(long id) throws ResourceNotFoundException, NoPermissionException;

    void assignSubmissionToCreator(long id) throws ResourceNotFoundException, NoPermissionException;

    ValidationResult validateSubmission(long id) throws ResourceNotFoundException, NoPermissionException;

    void submitSubmission(long id) throws ResourceNotFoundException, NoPermissionException;

    ArrayDesignUpdateResult updateArrayDesign(long id, List<ArrayDesignUpdateCommand> commands) throws ResourceNotFoundException, NoPermissionException;

    ExperimentProfile loadExperiment(long id) throws ResourceNotFoundException, NoPermissionException;

    ExperimentProfile updateExperiment(long id, List<ExperimentUpdateCommand> commands) throws ResourceNotFoundException, NoPermissionException;

    void deleteSubmission(long id) throws ResourceNotFoundException, NoPermissionException;

    void sendMessage(long id, String subject, String message) throws ResourceNotFoundException, NoPermissionException;

    void postFeedback(long id, Byte score, String comment) throws ResourceNotFoundException, NoPermissionException;
}
