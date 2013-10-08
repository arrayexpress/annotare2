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

package uk.ac.ebi.fg.annotare2.web.server;

import com.google.inject.Provider;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author Olga Melnichuk
 */
public class JmsResources {

    private ConnectionFactory connectionFactory;
    private Queue fileCopyQueue;

    private void init() {
        try {
            Context context = new InitialContext();
            connectionFactory = (ConnectionFactory) context.lookup("java:comp/env/jms/jmsConnectionFactory");
            fileCopyQueue = (Queue) context.lookup("java:comp/env/jms/fileCopyQueue");
        } catch (NamingException e) {
            throw new RuntimeException("Can't find jms resources", e);
        }
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public Queue getFileCopyQueue() {
        return fileCopyQueue;
    }

    public static class Creator implements Provider<JmsResources> {
        private JmsResources resources;

        @Override
        public JmsResources get() {
            return resources == null ? create() : resources;
        }

        private JmsResources create() {
            resources = new JmsResources();
            resources.init();
            return resources;
        }
    }
}
