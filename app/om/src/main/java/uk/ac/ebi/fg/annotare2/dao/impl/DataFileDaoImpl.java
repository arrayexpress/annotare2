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

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.fg.annotare2.dao.DataFileDao;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.om.DataFile;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class DataFileDaoImpl implements DataFileDao {

    private final HibernateSessionFactory sessionFactory;

    public DataFileDaoImpl(HibernateSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public DataFile get(long id) {
        return (DataFile) getCurrentSession().get(DataFile.class, id);
    }

    @Override
    public void save(DataFile dataFile) {
        getCurrentSession().save(dataFile);
    }

    @Override
    public DataFile create(String fileName) {
        DataFile dataFile = new DataFile(fileName);
        save(dataFile);
        return dataFile;
    }

    @Override
    public void delete(DataFile dataFile) {
        getCurrentSession().delete(dataFile);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DataFile> getAllWithDigest(String digest) {
        return getCurrentSession().createCriteria(DataFile.class)
                .add(Restrictions.eq("digest", digest))
                .list();
    }

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}
