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

package uk.ac.ebi.fg.annotare2.web.server.transaction;

import com.google.inject.Inject;
import org.hibernate.Transaction;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.web.server.TransactionWrapException;

/**
 * @author Olga Melnichuk
 */
public class TransactionSupport {

    private final HibernateSessionFactory sessionFactory;

    @Inject
    public TransactionSupport(HibernateSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T execute(TransactionCallback<T> transactionCallback) throws TransactionWrapException {
        Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
        try {
            T result = transactionCallback.doInTransaction();
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        } catch (Exception e) {
            tx.rollback();
            throw new TransactionWrapException(e);
        }
    }
}
