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
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.fg.annotare2.db.dao.DataFileDao;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.enums.DataFileStatus;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;

import java.util.Collection;

/**
 * @author Olga Melnichuk
 */
public class DataFileDaoImpl extends AbstractDaoImpl<DataFile> implements DataFileDao {

    @Inject
    public DataFileDaoImpl(HibernateSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public DataFile get(long id) throws RecordNotFoundException {
        return get(id, DataFile.class);
    }

    @Override
    public DataFile create(String fileName, Submission submission) {
        DataFile dataFile = new DataFile(fileName);
        dataFile.setOwnedBy(submission);
        save(dataFile);
        return dataFile;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<DataFile> getFilesByDigest(String digest) {
        return getCurrentSession().createCriteria(DataFile.class)
                .add(Restrictions.eq("digest", digest))
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<DataFile> getFilesByStatus(DataFileStatus... statuses) {
        return getCurrentSession().createCriteria(DataFile.class)
                .add(Restrictions.in("status", statuses))
                .list();
    }

    @Override
    public void softDelete(DataFile dataFile) {
        dataFile.setDeleted(true);
        save(dataFile);
    }
}
