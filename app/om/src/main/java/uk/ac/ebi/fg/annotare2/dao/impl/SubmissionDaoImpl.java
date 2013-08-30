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

package uk.ac.ebi.fg.annotare2.dao.impl;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.om.ArrayDesignSubmission;
import uk.ac.ebi.fg.annotare2.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.om.User;
import uk.ac.ebi.fg.annotare2.om.enums.SubmissionStatus;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Collections2.filter;
import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class SubmissionDaoImpl extends AbstractDaoImpl<Submission> implements SubmissionDao {

    private final ExperimentSubmissionDaoImpl expSmbDao;
    private final ArrayDesignSubmissionDaoImpl arrayDesignSbmDao;

    @Inject
    public SubmissionDaoImpl(HibernateSessionFactory sessionFactory) {
        super(sessionFactory);
        expSmbDao = new ExperimentSubmissionDaoImpl(sessionFactory);
        arrayDesignSbmDao = new ArrayDesignSubmissionDaoImpl(sessionFactory);
    }

    @Override
    public Submission get(long id) throws RecordNotFoundException {
        return get(id, Submission.class);
    }

    @Override
    public ExperimentSubmission getExperimentSubmission(long id) throws RecordNotFoundException {
        return expSmbDao.get(id);
    }

    @Override
    public ArrayDesignSubmission getArrayDesignSubmission(long id) throws RecordNotFoundException {
        return arrayDesignSbmDao.get(id);
    }

    @Override
    public List<Submission> getSubmissions(User user) {
        return user.getSubmissions();
    }

    @Override
    public Collection<Submission> getSubmissionsByStatus(User user, final SubmissionStatus... statuses) {
        return filter(getSubmissions(user), new Predicate<Submission>() {
            public boolean apply(@Nullable Submission input) {
                return input != null && asList(statuses).contains(input.getStatus());
            }
        });
    }

    @Override
    public ExperimentSubmission createExperimentSubmission(User user) {
        ExperimentSubmission submission = new ExperimentSubmission(user);
        save(submission);
        return submission;
    }

    @Override
    public ArrayDesignSubmission createArrayDesignSubmission(User user) {
        ArrayDesignSubmission submission = new ArrayDesignSubmission(user);
        save(submission);
        return submission;
    }
}
