/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.server;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.google.inject.Provider;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;

/**
 * @author Olga Melnichuk
 */
public class HibernateSessionFactoryProvider extends AbstractIdleService implements Provider<HibernateSessionFactory> {

    private final HibernateSessionFactory sessionFactory;
    private final DatabaseDataSource ds;

    @Inject
    public HibernateSessionFactoryProvider(DatabaseDataSource ds) {
        this.ds = ds;

        sessionFactory = HibernateSessionFactory.create();
    }

    @Override
    protected void startUp() throws Exception {
        //do nothing
    }

    @Override
    protected void shutDown() throws Exception {
        sessionFactory.close();
        ds.shutDown();
    }

    @Override
    public HibernateSessionFactory get() {
        return sessionFactory;
    }
}
