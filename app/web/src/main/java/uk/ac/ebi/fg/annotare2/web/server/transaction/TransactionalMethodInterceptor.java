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

package uk.ac.ebi.fg.annotare2.web.server.transaction;

import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.Transaction;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;

/**
 * @author Olga Melnichuk
 */
public class TransactionalMethodInterceptor implements MethodInterceptor {

    @Inject
    private HibernateSessionFactory sessionFactory;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Transactional txAnnotation = invocation.getMethod().getAnnotation(Transactional.class);
        Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
        try {
            Object obj = invocation.proceed();
            tx.commit();
            return obj;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        } catch (Exception e) {
            if (isRollbackNeeded(txAnnotation, e)) {
                tx.rollback();
            } else {
                tx.commit();
            }
            throw e;
        }
    }

    private boolean isRollbackNeeded(Transactional annotation, Exception e) {
        for (Class<? extends Exception> clazz : annotation.rollbackOn()) {
            if (clazz.equals(e.getClass())) {
                return true;
            }
        }
        return false;
    }
}
