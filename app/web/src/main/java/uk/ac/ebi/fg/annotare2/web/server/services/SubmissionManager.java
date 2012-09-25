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
import uk.ac.ebi.fg.annotare2.om.*;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SubmissionManager {

    private SubmissionDao submissionDao;

    @Inject
    public SubmissionManager(SubmissionDao submissionDao) {
        this.submissionDao = submissionDao;
    }

    public List<Submission> getAllSubmissions(User user) {
        return submissionDao.getSubmissionsByType(user, SubmissionType.EXPERIMENT);
    }

    public List<Submission> getIncompleteSubmissions(User user) {
        return submissionDao.getSubmissionsByStatus(user,
                SubmissionStatus.IN_PROGRESS,
                SubmissionStatus.IN_CURATION,
                SubmissionStatus.SUBMITTED);
    }

    public List<Submission> getCompletedSubmissions(User user) {
        return submissionDao.getSubmissionsByStatus(user,
                SubmissionStatus.PRIVATE_IN_AE,
                SubmissionStatus.PUBLIC_IN_AE);
    }

    public Submission getSubmission(User user, int id) throws RecordNotFoundException, AccessControlException {
        Submission sb = submissionDao.getSubmission(id);
        if(!user.isAllowed(sb, Permission.VIEW)) {
            throw new AccessControlException("User " + user + " doesn't have a permission to view the submission " + sb);
        }
        return sb;
    }

    public Submission getSubmission2Update(User user, int id) throws AccessControlException, RecordNotFoundException {
        Submission sb = getSubmission(user, id);
        if (!user.isAllowed(sb, Permission.UPDATE)) {
            throw new AccessControlException("User " + user + " doesn't have a permission to update the submission " + sb);
        }
        return sb;
    }

    public Submission createSubmission(User user) throws AccessControlException {
        SubmissionFactory factory = submissionDao.getSubmissionFactory(user);
        if (!user.isAllowed(factory, Permission.CREATE)) {
            throw new AccessControlException("User " + user + " doesn't have a permission to create a submission");
        }
        Submission submission = factory.createSubmission();
        submissionDao.save(submission);
        return submission;
    }
}
