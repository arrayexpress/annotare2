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

package uk.ac.ebi.fg.annotare2.db.dao.dummy;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.db.om.*;
import uk.ac.ebi.fg.annotare2.db.om.enums.SubmissionStatus;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class SubmissionDaoDummy implements SubmissionDao {

    @Override
    public Submission get(long id) throws RecordNotFoundException {
        return DummyData.getSubmission(id, Submission.class);
    }

    @Override
    public ExperimentSubmission getExperimentSubmission(long id) throws RecordNotFoundException {
        return DummyData.getSubmission(id, ExperimentSubmission.class);
    }

    @Override
    public ArrayDesignSubmission getArrayDesignSubmission(long id) throws RecordNotFoundException {
        return DummyData.getSubmission(id, ArrayDesignSubmission.class);
    }

    @Override
    public List<Submission> getSubmissions(User user) {
        return DummyData.getSubmissions(user);
    }

    @Override
    public Collection<Submission> getSubmissionsByStatus(final SubmissionStatus... statuses) {
        return Collections2.filter(DummyData.getSubmissions(), new Predicate<Submission>() {
            public boolean apply(@Nullable Submission input) {
                return input != null && asList(statuses).contains(input.getStatus());
            }
        });
    }

    @Override
    public Collection<Submission> getSubmissionsByStatus(User user, final SubmissionStatus... statuses) {
        return Collections2.filter(getSubmissions(user), new Predicate<Submission>() {
            public boolean apply(@Nullable Submission input) {
                return input != null && asList(statuses).contains(input.getStatus());
            }
        });
    }

    @Override
    public void save(Submission submission) {
        DummyData.save(submission);
    }

    @Override
    public ExperimentSubmission createExperimentSubmission(User user) {
        return SubmissionFactory.createExperimentSubmission(user);
    }

    @Override
    public ArrayDesignSubmission createArrayDesignSubmission(User user) {
        return SubmissionFactory.createArrayDesignSubmission(user);
    }

    @Override
    public Acl getAcl() {
        return SubmissionFactory.getAcl();
    }

    @Override
    public EffectiveAcl getEffectiveAcl() {
        return SubmissionFactory.getEffectiveAcl();
    }
}
