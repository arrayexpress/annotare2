/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.om.ArrayDesignSubmission;
import uk.ac.ebi.fg.annotare2.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.om.enums.Permission;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.server.login.AuthService;
import uk.ac.ebi.fg.annotare2.web.server.services.AccessControlException;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;

/**
 * @author Olga Melnichuk
 */
public abstract class SubmissionBasedRemoteService extends AuthBasedRemoteService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionBasedRemoteService.class);

    private final SubmissionManager submissionManager;

    protected SubmissionBasedRemoteService(AuthService authService,
                                           SubmissionManager submissionManager) {
        super(authService);
        this.submissionManager = submissionManager;
    }

    protected ExperimentSubmission getExperimentSubmission(int id, Permission permission) throws ResourceNotFoundException, NoPermissionException {
        try {
            return submissionManager.getExperimentSubmission(getCurrentUser(), id, permission);
        } catch (RecordNotFoundException e) {
            throw noSuchSubmission(id, e);
        } catch (AccessControlException e) {
            throw noSubmssionAccess(id, e);
        }
    }

    protected ArrayDesignSubmission getArrayDesignSubmission(int id, Permission permission) throws ResourceNotFoundException, NoPermissionException {
        try {
            return submissionManager.getArrayDesignSubmission(getCurrentUser(), id, permission);
        } catch (RecordNotFoundException e) {
            throw noSuchSubmission(id, e);
        } catch (AccessControlException e) {
            throw noSubmssionAccess(id, e);
        }
    }

    private ResourceNotFoundException noSuchSubmission(int id, RecordNotFoundException e) {
        log.warn("Can't find submission with id: " + id, e);
        return new ResourceNotFoundException("Submission with id=" + id + " doesn't exist");
    }

    private NoPermissionException noSubmssionAccess(int id, AccessControlException e) {
        log.warn("Access denied (user: " + getCurrentUser().getId() + ", submission:" + id + ")", e);
        return new NoPermissionException("You don't have a required permission to this resource");
    }
}
