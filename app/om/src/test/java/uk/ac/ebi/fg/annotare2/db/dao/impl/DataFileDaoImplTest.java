/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.User;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * @author Olga Melnichuk
 */
public class DataFileDaoImplTest extends DaoTestBase {

    private DataFileDaoImpl dataFileDao;
    private SubmissionDaoImpl submissionDao;
    private UserDaoImpl userDao;

    @Before
    public void before() {
        dataFileDao = new DataFileDaoImpl(getSessionFactory());
        submissionDao = new SubmissionDaoImpl(getSessionFactory());
        userDao = new UserDaoImpl(getSessionFactory());
    }

    @Test
    public void createDataFileTest() {
        final String name = "create_test";
        DataFile dataFile = dataFileDao.create(name, createSubmission(""));
        flush();

        assertNotNull(dataFile);
        assertNotNull(dataFile.getId());
        assertNotNull(dataFile.getCreated());
        assertNotNull(dataFile.getStatus());
        assertEquals(name, dataFile.getName());
    }

    @Test
    public void deleteDataFileTest() throws RecordNotFoundException {
        DataFile dataFile = dataFileDao.create("delete_test", createSubmission(""));
        flush();
        Long id = dataFile.getId();

        dataFileDao.delete(dataFile);
        try {
            dataFileDao.get(id);
            fail("Exception is expected");
        } catch (RecordNotFoundException e) {
            // ok
        }
    }

    @Test
    public void getAllWithDigestTest() {
        final String digest = "12345";
        final int n = 3;
        for (int i = 0; i < n; i++) {
            DataFile dataFile1 = dataFileDao.create("test", createSubmission("" + i));
            dataFile1.setDigest(digest);
            dataFileDao.save(dataFile1);
        }
        flush();

        Collection<DataFile> list = dataFileDao.getFilesByDigest(digest);
        assertEquals(n, list.size());
    }

    private Submission createSubmission(String prefix) {
        User user = new User("name", "email" + prefix, "password");
        userDao.save(user);
        return submissionDao.createSubmission(user, ExperimentSubmission.class);
    }
}
