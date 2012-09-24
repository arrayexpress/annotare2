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
import uk.ac.ebi.fg.annotare2.magetab.base.operation.Operation;
import uk.ac.ebi.fg.annotare2.magetab.base.Table;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UIGeneralInfo;

/**
 * @author Olga Melnichuk
 */
@RemoteServiceRelativePath(IdfService.NAME)
public interface IdfService extends RemoteService {

    public static final String NAME = "idfService";

    UIGeneralInfo getGeneralInfo(int submissionId) throws NoPermissionException, ResourceNotFoundException;

    Table loadInvestigation(int submissionId) throws NoPermissionException, ResourceNotFoundException;

    void updateInvestigation(int submissionId, Operation operation) throws NoPermissionException, ResourceNotFoundException;

    /**
     * Imports investigation data from a file stored in the current session.
     *
     * @param submissionId a submission identifier to replace investigation data in
     * @throws NoPermissionException     If the user doesn't have permission to change the submission content
     * @throws ResourceNotFoundException If the submission or the imported file doesn't exist
     * @throws DataImportException       If any error happened during the data import process
     */
    void importInvestigation(int submissionId) throws NoPermissionException, ResourceNotFoundException, DataImportException;
}
