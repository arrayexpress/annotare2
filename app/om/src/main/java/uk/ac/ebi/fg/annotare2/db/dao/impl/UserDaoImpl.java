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

package uk.ac.ebi.fg.annotare2.db.dao.impl;

import com.google.inject.Inject;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.dao.UserDao;
import uk.ac.ebi.fg.annotare2.db.model.enums.Role;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.db.model.User;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class UserDaoImpl extends AbstractDaoImpl<User> implements UserDao {

    @Inject
    public UserDaoImpl(HibernateSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public User get(long id) throws RecordNotFoundException {
        return get(id, User.class);
    }

    @Override
    public User create(String email, String password) {
        User user = new User(email, password);
        save(user);
        return user;
    }

    @Override
    @SuppressWarnings("unchecked")
    public User getUserByEmailAndPassword(String email, String password) {
        List<User> users = (List<User>) getCurrentSession().createCriteria(User.class)
                .add(Restrictions.eq("email", email))
                .add(Restrictions.eq("password", password))
                .list();
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public User getUserByEmail(String email) {
        List<User> users = (List<User>) getCurrentSession().createCriteria(User.class)
                .add(Restrictions.eq("email", email))
                .list();
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public User getCuratorUser() {
        List<User> users = (List<User>) getCurrentSession().createCriteria(User.class)
                .createCriteria("roles")
                .add(Restrictions.eq("role", Role.CURATOR))
                .list();
        return users.isEmpty() ? null : users.get(0);
    }
}
