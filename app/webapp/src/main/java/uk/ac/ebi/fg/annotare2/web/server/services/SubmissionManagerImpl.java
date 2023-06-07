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

package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.core.AccessControlException;
import uk.ac.ebi.fg.annotare2.core.components.SubmissionManager;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionStatusHistoryDao;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.User;
import uk.ac.ebi.fg.annotare2.db.model.enums.Permission;
import uk.ac.ebi.fg.annotare2.db.model.enums.SubmissionStatus;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collection;

import static com.google.common.base.Strings.isNullOrEmpty;

public class SubmissionManagerImpl implements SubmissionManager {

    private final SubmissionDao submissionDao;
    private final SecureRandom random;
    private final SubmissionStatusHistoryDao statusHistoryDao;

    @Inject
    public SubmissionManagerImpl(SubmissionDao submissionDao,
                                 SubmissionStatusHistoryDao statusHistoryDao) {
        this.submissionDao = submissionDao;
        this.statusHistoryDao = statusHistoryDao;
        random = new SecureRandom();
    }

    @Override
    public Collection<Submission> getAllSubmissions(User user) {
        return submissionDao.getSubmissions(user);
    }

    @Override
    public Collection<Submission> getIncompleteSubmissions(User user) {
        return submissionDao.getSubmissionsByStatus(user,
                SubmissionStatus.IN_PROGRESS,
                SubmissionStatus.IN_CURATION,
                SubmissionStatus.SUBMITTED);
    }

    @Override
    public Collection<Submission> getCompletedSubmissions(User user) {
        return submissionDao.getSubmissionsByStatus(user,
                SubmissionStatus.PRIVATE_IN_AE,
                SubmissionStatus.PUBLIC_IN_AE);
    }

    @Override
    public <T extends Submission> T getSubmission(User user, long id, Class<T> clazz, Permission permission) throws RecordNotFoundException, AccessControlException {
        T sb = submissionDao.get(id, clazz, false);
        return withPermission(user, permission, sb);
    }

    @Override
    public <T extends Submission> T createSubmission(User user, Class<T> clazz) throws AccessControlException {
        if (!user.isAllowed(submissionDao, Permission.CREATE)) {
            throw new AccessControlException("User " + user + " doesn't have a permission to create a submission");
        }
        T submission = submissionDao.createSubmission(user, clazz);
        submission.setAcl(submissionDao.getAcl());
        submission.setFtpSubDirectory(generateUniqueFtpSubDirectory(submission));
        submissionDao.save(submission);
        statusHistoryDao.saveStatusHistory(submission);
        return submission;
    }

    private <T extends Submission> T withPermission(User user, Permission permission, T sb) throws AccessControlException {
        if (!user.isAllowed(sb, permission)) {
            throw new AccessControlException("User " + user + " doesn't have a permission to [" + permission + "] the submission " + sb);
        }
        return sb;
    }

    @Override
    public void save(Submission submission) {
        submissionDao.save(submission);
    }

    @Override
    public void deleteSubmissionSoftly(Submission submission) {
        submissionDao.softDelete(submission);
    }

    @Override
    public String generateUniqueFtpSubDirectory(Submission submission) {
        String prefix = isNullOrEmpty(submission.getAccession())
                ? BigInteger.valueOf(submission.getCreated().getTime()).toString(36).toLowerCase()
                : submission.getAccession();

        return prefix + "-" + new BigInteger(64, random).toString(36).toLowerCase();
    }
}
