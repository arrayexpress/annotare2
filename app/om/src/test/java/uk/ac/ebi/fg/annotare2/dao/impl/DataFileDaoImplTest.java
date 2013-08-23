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

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.*;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.om.DataFile;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.junit.Assert.assertNotNull;

/**
 * @author Olga Melnichuk
 */
public class DataFileDaoImplTest {

    private static final Logger log = LoggerFactory.getLogger(DataFileDaoImplTest.class);

    private static InitialContext initialContext;
    private static HibernateSessionFactory sessionFactory;

    private Session session;

    @Rule
    private ExternalResource resource = new ExternalResource() {
        private Transaction tx;

        @Override
        protected void before() throws Throwable {
            session = sessionFactory.openSession();
            dao = new DataFileDaoImpl(sessionFactory);
            tx = session.beginTransaction();
        }

        @Override
        protected void after() {
            tx.rollback();
            sessionFactory.closeSession();
        }
    };

    private DataFileDaoImpl dao;

    @BeforeClass
    public static void beforeClass() throws NamingException, HibernateException {
        setUpJndi();
        setUpHibernateSessionFactory();
    }

    @AfterClass
    public static void afterClass() {
        if (initialContext != null) {
            try {
                initialContext.close();
            } catch (NamingException e) {
                log.error("Can't close initial context", e);
            }
        }

        if (sessionFactory != null) {
            try {
                sessionFactory.close();
            } catch (HibernateException e) {
                log.error("Can't close session factory", e);
            }
        }
    }

    private static void setUpJndi() throws NamingException {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES,
                "org.apache.naming");
        initialContext = new InitialContext();

        initialContext.createSubcontext("java:");
        initialContext.createSubcontext("java:comp");
        initialContext.createSubcontext("java:comp/env");
        initialContext.createSubcontext("java:comp/env/jdbc");

        //todo move to properties
        MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
        ds.setURL("jdbc:mysql://localhost:3306/annotare2_test");
        ds.setUser("test");
        ds.setPassword("test");

        initialContext.bind("java:comp/env/jdbc/annotareDataSource", ds);
    }

    private static void setUpHibernateSessionFactory() throws HibernateException {
        sessionFactory = HibernateSessionFactory.create();
    }

    @Test
    public void test() {
        DataFile dataFile = dao.create("test");
        session.flush();

        assertNotNull(dataFile);
        assertNotNull(dataFile.getId());
        assertNotNull(dataFile.getCreated());
        assertNotNull(dataFile.getStatus());
    }
}
