/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package uk.ac.ebi.fg.annotare2.core.components;

import uk.ac.ebi.fg.annotare2.core.AccessControlException;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.User;
import uk.ac.ebi.fg.annotare2.db.model.enums.Permission;

import java.util.Collection;
import java.util.Date;

public interface SubmissionManager {

    Collection<Submission> getAllSubmissions(User user);

    Collection<Submission> getIncompleteSubmissions(User user);

    Collection<Submission> getCompletedSubmissions(User user);

    <T extends Submission> T getSubmission(User user, long id, Class<T> clazz, Permission permission)
            throws RecordNotFoundException, AccessControlException;

    <T extends Submission> T createSubmission(User user, Class<T> clazz) throws AccessControlException;

    void save(Submission submission);

    void deleteSubmissionSoftly(Submission submission);

    String generateUniqueFtpSubDirectory(Date creationDate);
}
