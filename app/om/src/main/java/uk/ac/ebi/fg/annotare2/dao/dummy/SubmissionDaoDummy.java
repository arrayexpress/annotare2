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

package uk.ac.ebi.fg.annotare2.dao.dummy;

import com.google.common.base.Predicate;
import uk.ac.ebi.fg.annotare2.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.om.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.om.SubmissionType;
import uk.ac.ebi.fg.annotare2.om.User;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static uk.ac.ebi.fg.annotare2.dao.dummy.DummyData.getSubmission;
import static uk.ac.ebi.fg.annotare2.dao.dummy.DummyData.getSubmissions;

/**
 * @author Olga Melnichuk
 */
public class SubmissionDaoDummy implements SubmissionDao {

    public Submission getSubmission(int id) throws RecordNotFoundException {
        Submission s = DummyData.getSubmission(id);
        if (s == null) {
            throw new RecordNotFoundException("Submission with id=" + id + " not found");
        }
        return s;
    }

    public List<Submission> getSubmissionsByType(User user, final SubmissionType type) {
        return getSubmissions(user, new Predicate<Submission>() {
            public boolean apply(@Nullable Submission input) {
                return input.getType().equals(type);
            }
        });
    }

    public List<Submission> getSubmissionsByStatus(User user, final SubmissionStatus... statuses) {
        return getSubmissions(user, new Predicate<Submission>() {
            public boolean apply(@Nullable Submission input) {
                return asList(statuses).contains(input.getStatus());
            }
        });
    }
}
