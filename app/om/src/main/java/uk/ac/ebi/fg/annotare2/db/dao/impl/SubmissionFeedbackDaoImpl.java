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

package uk.ac.ebi.fg.annotare2.db.dao.impl;

import com.google.inject.Inject;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionFeedbackDao;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.SubmissionFeedback;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;

import java.util.Collection;
import java.util.List;

public class SubmissionFeedbackDaoImpl extends AbstractDaoImpl<SubmissionFeedback> implements SubmissionFeedbackDao {

    @Inject
    public  SubmissionFeedbackDaoImpl(HibernateSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public SubmissionFeedback create(Byte score, Submission submission) {
        SubmissionFeedback feedback = new SubmissionFeedback(submission, score);
        save(feedback);
        return feedback;
    }

    @Override
    public SubmissionFeedback getLastFeedbackFor(Submission submission) {
        return (SubmissionFeedback)getCurrentSession()
                .createCriteria(SubmissionFeedback.class)
                .add(Restrictions.eq("relatesTo", submission))
                .add(Restrictions.ge("posted", submission.getSubmitted()))
                .addOrder(Order.desc("posted"))
                .setMaxResults(1)
                .uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<SubmissionFeedback> getFeedbackFor(Submission submission) {
        return (List<SubmissionFeedback>)getCurrentSession()
                        .createCriteria(SubmissionFeedback.class)
                        .add(Restrictions.eq("relatesTo", submission))
                        .addOrder(Order.desc("posted"))
                        .list();
    }
}
