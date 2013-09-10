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

import com.google.gwt.user.server.rpc.UnexpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.om.ArrayDesignSubmission;
import uk.ac.ebi.fg.annotare2.db.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.om.Submission;
import uk.ac.ebi.fg.annotare2.db.om.enums.Permission;
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

    public Submission getSubmission(long id, Permission permission) throws ResourceNotFoundException, NoPermissionException {
        try {
            return submissionManager.getSubmission(getCurrentUser(), id, permission);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
    }

    protected ExperimentSubmission getExperimentSubmission(long id, Permission permission) throws ResourceNotFoundException, NoPermissionException {
        try {
            return submissionManager.getExperimentSubmission(getCurrentUser(), id, permission);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
    }

    protected ArrayDesignSubmission getArrayDesignSubmission(long id, Permission permission) throws ResourceNotFoundException, NoPermissionException {
        try {
            return submissionManager.getArrayDesignSubmission(getCurrentUser(), id, permission);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
    }

    protected ExperimentSubmission createExperimentSubmission() throws NoPermissionException {
        try {
            return submissionManager.createExperimentSubmission(getCurrentUser());
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
    }

    protected ArrayDesignSubmission createArrayDesignSubmission() throws NoPermissionException {
        try {
            return submissionManager.createArrayDesignSubmission(getCurrentUser());
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
    }

    protected void save(Submission submission) {
        submissionManager.save(submission);
    }

    protected static Throwable maybeNoPermission(Throwable e) throws NoPermissionException {
        if (e instanceof AccessControlException) {
            throw noPermission((AccessControlException) e);
        }
        return e;
    }

    protected static Throwable maybeNoSuchRecord(Throwable e) throws ResourceNotFoundException {
        if (e instanceof RecordNotFoundException) {
            throw noSuchRecord((RecordNotFoundException) e);
        }
        return e;
    }

    protected static UnexpectedException unexpected(Throwable e) {
        log.error("server error", e);
        return new UnexpectedException("Unexpected server error", e);
    }

    protected static ResourceNotFoundException noSuchRecord(RecordNotFoundException e) {
        log.error("server error", e);
        return new ResourceNotFoundException("Submission not found");
    }

    protected static NoPermissionException noPermission(AccessControlException e) {
        log.error("server error", e);
        return new NoPermissionException("No permission");
    }
}
