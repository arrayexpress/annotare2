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

package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.om.ArrayDesignSubmission;
import uk.ac.ebi.fg.annotare2.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.om.User;
import uk.ac.ebi.fg.annotare2.om.enums.Permission;
import uk.ac.ebi.fg.annotare2.om.enums.SubmissionStatus;

import java.util.Collection;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SubmissionManager {

    private final SubmissionDao submissionDao;

    @Inject
    public SubmissionManager(SubmissionDao submissionDao) {
        this.submissionDao = submissionDao;
    }

    public List<Submission> getAllSubmissions(User user) {
        return submissionDao.getSubmissions(user);
    }

    public Collection<Submission> getIncompleteSubmissions(User user) {
        return submissionDao.getSubmissionsByStatus(user,
                SubmissionStatus.IN_PROGRESS,
                SubmissionStatus.IN_CURATION,
                SubmissionStatus.SUBMITTED);
    }

    public Collection<Submission> getCompletedSubmissions(User user) {
        return submissionDao.getSubmissionsByStatus(user,
                SubmissionStatus.PRIVATE_IN_AE,
                SubmissionStatus.PUBLIC_IN_AE);
    }

    public Submission getSubmission(User user, long id, Permission permission) throws AccessControlException, RecordNotFoundException {
        Submission submission = submissionDao.get(id);
        return withPermission(user, permission, submission);
    }

    public ExperimentSubmission getExperimentSubmission(User user, long id, Permission permission) throws RecordNotFoundException, AccessControlException {
        ExperimentSubmission sb = submissionDao.getExperimentSubmission(id);
        return withPermission(user, permission, sb);
    }

    public ArrayDesignSubmission getArrayDesignSubmission(User user, long id, Permission permission) throws RecordNotFoundException, AccessControlException {
        ArrayDesignSubmission sb = submissionDao.getArrayDesignSubmission(id);
        return withPermission(user, permission, sb);
    }

    public ExperimentSubmission createExperimentSubmission(User user) throws AccessControlException {
        if (!user.isAllowed(submissionDao, Permission.CREATE)) {
            throw new AccessControlException("User " + user + " doesn't have a permission to create a submission");
        }
        ExperimentSubmission submission = submissionDao.createExperimentSubmission(user);
        submission.setAcl(submissionDao.getAcl());
        submissionDao.save(submission);
        return submission;
    }

    public ArrayDesignSubmission createArrayDesignSubmission(User user) throws AccessControlException {
        if (!user.isAllowed(submissionDao, Permission.CREATE)) {
            throw new AccessControlException("User " + user + " doesn't have a permission to create a submission");
        }
        ArrayDesignSubmission submission = submissionDao.createArrayDesignSubmission(user);
        submission.setAcl(submissionDao.getAcl());
        submissionDao.save(submission);
        return submission;
    }

    private <T extends Submission> T withPermission(User user, Permission permission, T sb) throws AccessControlException {
        if (!user.isAllowed(sb, permission)) {
            throw new AccessControlException("User " + user + " doesn't have a permission to [" + permission + " ] the submission " + sb);
        }
        return sb;
    }

    public void save(Submission submission) {
        submissionDao.save(submission);
    }
}
