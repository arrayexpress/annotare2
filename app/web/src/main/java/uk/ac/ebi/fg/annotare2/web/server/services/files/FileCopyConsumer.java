package uk.ac.ebi.fg.annotare2.web.server.services.files;

/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import com.google.common.base.Objects;
import com.google.inject.Inject;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.RedeliveryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.db.dao.DataFileDao;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.web.server.transaction.Transactional;

import javax.jms.*;
import java.io.IOException;

import static uk.ac.ebi.fg.annotare2.db.model.enums.DataFileStatus.ERROR;
import static uk.ac.ebi.fg.annotare2.db.model.enums.DataFileStatus.STORED;

public class FileCopyConsumer implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(FileCopyConsumer.class);

    private final DataFileStore fileStore;
    private final DataFileDao fileDao;
    private final HibernateSessionFactory sessionFactory;

    private ActiveMQConnection connection;
    private Session session;
    private MessageConsumer consumer;

    @Inject
    public FileCopyConsumer(DataFileStore fileStore, DataFileDao fileDao, HibernateSessionFactory sessionFactory) {
        this.fileStore = fileStore;
        this.fileDao = fileDao;
        this.sessionFactory = sessionFactory;

        this.connection = null;
        this.session = null;
        this.consumer = null;
    }

    public void start(ConnectionFactory connectionFactory, Queue queue) {
        log.info("Starting JMS queue listener", queue);

        try {
            RedeliveryPolicy queuePolicy = new RedeliveryPolicy();
            queuePolicy.setInitialRedeliveryDelay(100);
            queuePolicy.setRedeliveryDelay(1000);
            queuePolicy.setUseExponentialBackOff(false);
            queuePolicy.setMaximumRedeliveries(4);

            connection = (ActiveMQConnection) connectionFactory.createConnection();
            connection.setRedeliveryPolicy(queuePolicy);
            connection.start();

            session = connection.createSession(false, ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE);
            consumer = session.createConsumer(queue);
            consumer.setMessageListener(this);
        } catch (JMSException e) {
            log.error("JMS queue listener has failed to start", e);
        }
    }

    public void stop() {
        if (null != consumer) {
            try {
                consumer.close();
            } catch (JMSException e) {
                log.error("Unable to close JMS consumer", e);
            }
        }
        if (null != session) {
            try {
                session.close();
            } catch (JMSException e) {
                log.error("Unable to close JMS session", e);
            }
        }
        if (null != connection) {
            try {
                connection.stop();
                connection.close();
            } catch (JMSException e) {
                log.error("Unable to close JMS connection", e);
            }
        }

    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                log.debug("Received message: {}, {}", objectMessage, Thread.currentThread().getName());
                FileCopyMessage messageObject = (FileCopyMessage)objectMessage.getObject();
                log.debug("Restored message object: {}", messageObject);

                sessionFactory.openSession();
                copyFile(messageObject);
                message.acknowledge();
            } catch (JMSException e) {
                log.error("Caught JMS exception", e);
            } catch (RecordNotFoundException e) {
                log.error("Data file record is not in the database yet, will retry once again in few moments");
                requestRedevlivery();       // sometimes the datafile is not yet in the database when message is received
            } finally {
                sessionFactory.closeSession();
            }
        }
    }

    private void requestRedevlivery() {
        try {
            session.recover();
        } catch (JMSException e) {
            log.error("Caught JMS exception", e);
        }
    }
    @Transactional
    public void copyFile(FileCopyMessage message) throws RecordNotFoundException {
        DataFile dataFile = fileDao.get(message.getDestinationId());

        DataFileSource source = message.getSource();
        try {
            if (source.exists()) {
                String digest = fileStore.store(source);
                if (!Objects.equal(digest, source.getDigest())) {
                    throw new IOException("MD5 is different between the source and the stored file");
                }
                dataFile.setStatus(STORED);
                if (message.shouldRemoveSource()) {
                    source.delete();
                    dataFile.setSourceUri(null);
                }
                fileDao.save(dataFile);
                return;
            } else {
                log.error("Unable to find source file {}", source.getName());
            }
        } catch (IOException e) {
            log.error("Unable to store file", e);
        }
        dataFile.setStatus(ERROR);
        fileDao.save(dataFile);
    }
}