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
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ContactDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
@RemoteServiceRelativePath(SubmissionService.NAME)
public interface SubmissionService extends RemoteService {

    public static final String NAME = "submissionService";

    SubmissionDetails getSubmission(int id) throws ResourceNotFoundException, NoPermissionException;

    ExperimentSettings getExperimentSettings(int id) throws ResourceNotFoundException, NoPermissionException;

    ExperimentDetails getExperimentDetails(int id) throws ResourceNotFoundException, NoPermissionException;

    List<ContactDto> getContacts(int id) throws ResourceNotFoundException, NoPermissionException;

    List<SampleRow> getSamples(int id) throws ResourceNotFoundException, NoPermissionException;

    int createExperimentSubmission() throws NoPermissionException;

    int createArrayDesignSubmission() throws NoPermissionException;

    void setupExperimentSubmission(int id, ExperimentSetupSettings settings) throws ResourceNotFoundException, NoPermissionException;

    void discardSubmissionData(int id) throws ResourceNotFoundException, NoPermissionException;

    ExperimentDetails saveExperimentDetails(int id, ExperimentDetails details) throws ResourceNotFoundException, NoPermissionException;

    List<ContactDto> saveContacts(int id, List<ContactDto> contacts) throws ResourceNotFoundException, NoPermissionException;
}
