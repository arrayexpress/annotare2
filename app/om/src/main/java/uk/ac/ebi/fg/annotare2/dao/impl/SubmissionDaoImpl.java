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

import uk.ac.ebi.fg.annotare2.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.om.ArrayDesignSubmission;
import uk.ac.ebi.fg.annotare2.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.om.User;
import uk.ac.ebi.fg.annotare2.om.enums.SubmissionStatus;

import java.util.Collection;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SubmissionDaoImpl extends AbstractDaoImpl implements SubmissionDao {

    public SubmissionDaoImpl(HibernateSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public ExperimentSubmission getExperimentSubmission(long id) throws RecordNotFoundException {
        return (ExperimentSubmission) getCurrentSession().get(ExperimentSubmission.class, id);
    }

    @Override
    public ArrayDesignSubmission getArrayDesignSubmission(long id) throws RecordNotFoundException {
        return (ArrayDesignSubmission) getCurrentSession().get(ArrayDesignSubmission.class, id);
    }

    @Override
    public List<Submission> getSubmissions(User user) {
        //TODO
        return null;
    }

    @Override
    public Collection<Submission> getSubmissionsByStatus(User user, SubmissionStatus... statuses) {
        //TODO
        return null;
    }

    @Override
    public void save(Submission submission) {
        getCurrentSession().save(submission);
    }

    @Override
    public Submission get(long id) throws RecordNotFoundException {
        return (Submission) getCurrentSession().get(Submission.class, id);
    }
}
