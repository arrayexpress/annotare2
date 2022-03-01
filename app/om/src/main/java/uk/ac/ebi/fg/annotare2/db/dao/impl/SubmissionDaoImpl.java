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

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.inject.Inject;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.db.model.Acl;
import uk.ac.ebi.fg.annotare2.db.model.EffectiveAcl;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.User;
import uk.ac.ebi.fg.annotare2.db.model.enums.AclType;
import uk.ac.ebi.fg.annotare2.db.model.enums.Permission;
import uk.ac.ebi.fg.annotare2.db.model.enums.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.submission.transform.ModelVersion;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Collections2.filter;
import static java.util.Arrays.asList;

public class SubmissionDaoImpl extends AbstractDaoImpl<Submission> implements SubmissionDao {

    @Inject
    public SubmissionDaoImpl(HibernateSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public  Submission get(long id, boolean isDeletedAllowed) throws RecordNotFoundException {
        Submission submission = get(id, Submission.class);
        if ((submission.isDeleted() && !isDeletedAllowed)) {
            throw new RecordNotFoundException("Object of class=" + Submission.class + " with id=" + id + " was not found");
        }
        return submission;
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T extends Submission> T get(long id, Class<T> clazz, boolean isDeletedAllowed) throws RecordNotFoundException {
        Submission submission = get(id, Submission.class);
        if (!clazz.isInstance(submission) || (submission.isDeleted() && !isDeletedAllowed)) {
            throw new RecordNotFoundException("Object of class=" + clazz + " with id=" + id + " was not found");
        }
        return (T)submission;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Submission> getSubmissions() {
        return getCurrentSession()
                .createCriteria(Submission.class)
                .list();
    }

    @Override
    public Collection<Submission> getSubmissions(final User user) {
        return getSubmissions().stream()
                .filter(input -> input != null && user.isAllowed(input, Permission.VIEW))
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Submission> getSubmissionsByStatus(final SubmissionStatus... statuses) {
        return getCurrentSession()
                .createCriteria(Submission.class)
                .add(Restrictions.in("status", statuses))
                .list();
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
    @SuppressWarnings("unchecked")
    public Collection<Submission> getSubmissionsByVersion(final ModelVersion... versions) {
        return getCurrentSession()
                .createCriteria(Submission.class)
                .add(Restrictions.in("version", versions))
                .list();
    }

    @Override
    public <T extends Submission> T createSubmission(User user, Class<T> clazz) {
        try {
            T submission = clazz.getDeclaredConstructor(User.class).newInstance(user);
            save(submission);
            return submission;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException x) {
            throw new Error(x);
        }
    }


    @Override
    public void softDelete(Submission submission) {
        submission.setDeleted(true);
        save(submission);
    }

    @Override
    public void save(Submission submission) {
        submission.setUpdated(new Date());
        super.save(submission);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Acl getAcl() {
        List<Acl> list = getCurrentSession().createCriteria(Acl.class)
                .add(Restrictions.eq("aclType", AclType.SUBMISSION))
                .list();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public EffectiveAcl getEffectiveAcl() {
        return new EffectiveAcl(getAcl(), Optional.<User>absent(), Optional.<User>absent());
    }
}
