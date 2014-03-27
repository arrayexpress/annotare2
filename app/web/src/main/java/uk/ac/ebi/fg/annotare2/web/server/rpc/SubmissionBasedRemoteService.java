/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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
import uk.ac.ebi.fg.annotare2.db.model.ArrayDesignSubmission;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.enums.Permission;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.server.services.AccessControlException;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountService;
import uk.ac.ebi.fg.annotare2.web.server.services.EmailSender;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;

/**
 * @author Olga Melnichuk
 */
public abstract class SubmissionBasedRemoteService extends AuthBasedRemoteService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionBasedRemoteService.class);

    private final SubmissionManager submissionManager;
    private final EmailSender email;

    protected SubmissionBasedRemoteService(AccountService accountService,
                                           SubmissionManager submissionManager,
                                           EmailSender emailSender) {
        super(accountService, emailSender);
        this.email = emailSender;
        this.submissionManager = submissionManager;
    }

    protected Submission getSubmission(long id, Permission permission) throws RecordNotFoundException, AccessControlException {
        return submissionManager.getSubmission(getCurrentUser(), id, permission);
    }

    protected ExperimentSubmission getExperimentSubmission(long id, Permission permission) throws RecordNotFoundException, AccessControlException {
        return submissionManager.getExperimentSubmission(getCurrentUser(), id, permission);
    }

    protected ArrayDesignSubmission getArrayDesignSubmission(long id, Permission permission) throws RecordNotFoundException, AccessControlException {
        return submissionManager.getArrayDesignSubmission(getCurrentUser(), id, permission);
    }

    protected ExperimentSubmission createExperimentSubmission() throws AccessControlException {
        return submissionManager.createExperimentSubmission(getCurrentUser());
    }

    protected ArrayDesignSubmission createArrayDesignSubmission() throws AccessControlException {
        return submissionManager.createArrayDesignSubmission(getCurrentUser());
    }

    protected void save(Submission submission) {
        submissionManager.save(submission);
    }

    protected void deleteSubmissionSoftly(Submission submission) {
        submissionManager.deleteSubmissionSoftly(submission);
    }

    protected UnexpectedException unexpected(Throwable e) {
        log.error("server error", e);
        email.sendException("Unexpected server error for [" + getCurrentUserEmail() + "]", e);
        return new UnexpectedException("Unexpected server error", e);
    }

    protected ResourceNotFoundException noSuchRecord(RecordNotFoundException e) {
        log.error("server error", e);
        email.sendException("Submission not found for [" + getCurrentUserEmail() + "]", e);
        return new ResourceNotFoundException("Submission not found");
    }

    protected NoPermissionException noPermission(AccessControlException e) {
        log.error("server error", e);
        email.sendException("No permission for [" + getCurrentUserEmail() + "]", e);
        return new NoPermissionException("No permission");
    }
}
