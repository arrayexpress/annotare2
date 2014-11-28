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

package uk.ac.ebi.fg.annotare2.db.dao;

import uk.ac.ebi.fg.annotare2.db.model.Acl;
import uk.ac.ebi.fg.annotare2.db.model.HasEffectiveAcl;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.User;
import uk.ac.ebi.fg.annotare2.db.model.enums.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.submission.transform.ModelVersion;

import java.util.Collection;

public interface SubmissionDao extends HasEffectiveAcl {

    Collection<Submission> getSubmissions();

    Collection<Submission> getSubmissions(User user);

    Collection<Submission> getSubmissionsByVersion(final ModelVersion... versions);

    Collection<Submission> getSubmissionsByStatus(SubmissionStatus... statuses);

    Collection<Submission> getSubmissionsByStatus(User user, SubmissionStatus... statuses);

    void save(Submission submission);

    Submission get(long id, boolean isDeletedAllowed) throws RecordNotFoundException;

    <T extends Submission> T get(long id, Class<T> clazz, boolean isDeletedAllowed) throws RecordNotFoundException;

    <T extends Submission> T createSubmission(User user, Class<T> clazz);

    Acl getAcl();

    void softDelete(Submission submission);

    void delete(Submission submission);
}
