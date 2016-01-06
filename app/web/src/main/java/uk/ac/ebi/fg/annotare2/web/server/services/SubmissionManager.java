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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.User;
import uk.ac.ebi.fg.annotare2.db.model.enums.Permission;
import uk.ac.ebi.fg.annotare2.db.model.enums.SubmissionStatus;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Date;

public class SubmissionManager {

    private final SubmissionDao submissionDao;
    private final SecureRandom random;

    @Inject
    public SubmissionManager(SubmissionDao submissionDao) {
        this.submissionDao = submissionDao;
        random = new SecureRandom();
    }

    public Collection<Submission> getAllSubmissions(User user) {
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

    public <T extends Submission> T getSubmission(User user, long id, Class<T> clazz, Permission permission) throws RecordNotFoundException, AccessControlException {
        T sb = submissionDao.get(id, clazz, false);
        return withPermission(user, permission, sb);
    }

    public <T extends Submission> T createSubmission(User user, Class<T> clazz) throws AccessControlException {
        if (!user.isAllowed(submissionDao, Permission.CREATE)) {
            throw new AccessControlException("User " + user + " doesn't have a permission to create a submission");
        }
        T submission = submissionDao.createSubmission(user, clazz);
        submission.setAcl(submissionDao.getAcl());
        submission.setFtpSubDirectory(generateUniqueName(submission.getCreated()));
        submissionDao.save(submission);
        return submission;
    }

    private <T extends Submission> T withPermission(User user, Permission permission, T sb) throws AccessControlException {
        if (!user.isAllowed(sb, permission)) {
            throw new AccessControlException("User " + user + " doesn't have a permission to [" + permission + "] the submission " + sb);
        }
        return sb;
    }

    public void save(Submission submission) {
        submissionDao.save(submission);
    }

    public void deleteSubmissionSoftly(Submission submission) {
        submissionDao.softDelete(submission);
    }

    private String generateUniqueName(Date creationDate) {
        return (BigInteger.valueOf(creationDate.getTime()).toString(36) + "-" +  new BigInteger(64, random).toString(36)).toLowerCase();
    }
}
