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

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;

/**
 * @author Olga Melnichuk
 */
public abstract class AbstractDaoImpl<T> {

    private final HibernateSessionFactory sessionFactory;

    public AbstractDaoImpl(HibernateSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    protected T get(long id, Class<T> clazz) throws RecordNotFoundException {
        T t;
        try {
            t = clazz.cast(getCurrentSession().get(clazz, id));
        } catch (ObjectNotFoundException x) {
            t = null;
        }
        if (null == t) {
            throw new RecordNotFoundException("Object of class=" + clazz + " with id=" + id + " was not found");
        }
        return t;

    }

    public void save(T t) {
        getCurrentSession().saveOrUpdate(t);
    }

    public void delete(T t) {
        getCurrentSession().delete(t);
    }
}
