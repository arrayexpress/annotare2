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
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.server.login.AuthService;
import uk.ac.ebi.fg.annotare2.web.server.services.AccessControlException;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public abstract class SubmissionBasedRemoteService extends AuthBasedRemoteService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionBasedRemoteService.class);

    private final SubmissionManager submissionManager;

    protected SubmissionBasedRemoteService(AuthService authService, SubmissionManager submissionManager) {
        super(authService);
        this.submissionManager = submissionManager;
    }

    protected Submission getMySubmission(int id) throws NoPermissionException, ResourceNotFoundException {
        try {
            return submissionManager.getSubmission(getCurrentUser(), id);
        } catch (RecordNotFoundException e) {
            log.warn("getMySubmission(" + id + ") failure", e);
            throw new ResourceNotFoundException("Submission with id=" + id + " doesn't exist");
        } catch (AccessControlException e) {
            log.warn("getMySubmission(" + id + ") failure", e);
            throw new NoPermissionException("Sorry, you do not have access to this resource");
        }
    }

    protected Submission getMySubmission2Update(int id) throws ResourceNotFoundException, NoPermissionException {
        try {
            return submissionManager.getSubmission2Update(getCurrentUser(), id);
        } catch (RecordNotFoundException e) {
            log.warn("getMySubmission2Update(" + id + ") failure", e);
            throw new ResourceNotFoundException("Submission with id=" + id + " doesn't exist");
        } catch (AccessControlException e) {
            log.warn("getMySubmission2Update(" + id + ") failure", e);
            throw new NoPermissionException("Sorry, you do not have access to this resource");
        }
    }

    public Submission newSubmission() throws NoPermissionException {
        try {
            return submissionManager.createSubmission(getCurrentUser());
        } catch (AccessControlException e) {
            log.warn("createSubmission() failure", e);
            throw new NoPermissionException("Sorry, you do not have access to this resource");
        }
    }

    public List<Submission> getMyAllSubmissions() {
        return submissionManager.getAllSubmissions(getCurrentUser());
    }

    public List<Submission> getMyCompletedSubmissions() {
        return submissionManager.getCompletedSubmissions(getCurrentUser());
    }

    public List<Submission> getMyIncompleteSubmissions() {
        return submissionManager.getIncompleteSubmissions(getCurrentUser());
    }
}
