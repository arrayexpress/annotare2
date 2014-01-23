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

package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.inject.Inject;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.RedeliveryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.db.dao.DataFileDao;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.web.server.JmsResources;
import uk.ac.ebi.fg.annotare2.web.server.TransactionWrapException;
import uk.ac.ebi.fg.annotare2.web.server.services.files.DataFileSource;
import uk.ac.ebi.fg.annotare2.web.server.services.files.DataFileStore;
import uk.ac.ebi.fg.annotare2.web.server.transaction.TransactionCallback;
import uk.ac.ebi.fg.annotare2.web.server.transaction.TransactionSupport;

import javax.jms.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static uk.ac.ebi.fg.annotare2.db.model.enums.DataFileStatus.ERROR;
import static uk.ac.ebi.fg.annotare2.db.model.enums.DataFileStatus.STORED;

/**
 * @author Olga Melnichuk
 */
public class FileCopyMessageQueue {

    private static final Logger log = LoggerFactory.getLogger(FileCopyMessageQueue.class);

    private final FileCopyProducer producer;

    @Inject
    public FileCopyMessageQueue(JmsResources jmsResources, DataFileStore fileStore, DataFileDao fileDao, HibernateSessionFactory sessionFactory,
                                TransactionSupport transactionSupport) {

        producer = new FileCopyProducer();
        FileCopyConsumer consumer = new FileCopyConsumer(fileStore, fileDao, sessionFactory, transactionSupport);

        producer.start(jmsResources.getConnectionFactory(), jmsResources.getFileCopyQueue());
        consumer.start(jmsResources.getConnectionFactory(), jmsResources.getFileCopyQueue());
    }

    public void offer(DataFileSource source, DataFile destination) throws JMSException {
        producer.send(new FileCopyMessage(source, destination));
    }

    private static class FileCopyProducer {

        private ConnectionFactory connectionFactory;
        private Queue queue;

        public void start(ConnectionFactory connectionFactory, Queue queue) {
            this.connectionFactory = connectionFactory;
            this.queue = queue;
        }

        private void send(FileCopyMessage message) throws JMSException {
            Connection connection = connectionFactory.createConnection();
            Session session = null;
            MessageProducer producer = null;
            try {
                connection.start();

                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                producer = session.createProducer(queue);
                producer.setDeliveryMode(DeliveryMode.PERSISTENT);

                ObjectMessage objectMessage = session.createObjectMessage(message);
                //TextMessage textMessage = session.createTextMessage(message.asString());

                log.debug("Sent message: '{}' : {}", objectMessage, Thread.currentThread().getName());
                producer.send(objectMessage);
            } finally {
                if (producer != null) {
                    try {
                        producer.close();
                    } catch (JMSException e) {
                        // ignore
                    }
                }
                if (session != null) {
                    try {
                        session.close();
                    } catch (JMSException e) {
                        // ignore
                    }
                }
                try {
                    connection.close();
                } catch (JMSException e) {
                    // ignore
                }
            }
        }
    }

    private static class FileCopyConsumer implements MessageListener {

        private final DataFileStore fileStore;
        private final DataFileDao fileDao;
        private final HibernateSessionFactory sessionFactory;
        private final TransactionSupport transactionSupport;

        private ActiveMQConnection connection;
        private Session session;

        public FileCopyConsumer(DataFileStore fileStore, DataFileDao fileDao,
                                HibernateSessionFactory sessionFactory, TransactionSupport transactionSupport) {
            this.fileStore = fileStore;
            this.fileDao = fileDao;
            this.sessionFactory = sessionFactory;
            this.transactionSupport = transactionSupport;
        }

        public void start(ConnectionFactory connectionFactory, Queue queue) {
            log.info("Starting JMS queue listener", queue);

            try {
                RedeliveryPolicy queuePolicy = new RedeliveryPolicy();
                queuePolicy.setInitialRedeliveryDelay(0);
                queuePolicy.setRedeliveryDelay(1000);
                queuePolicy.setUseExponentialBackOff(false);
                queuePolicy.setMaximumRedeliveries(4);

                connection = (ActiveMQConnection) connectionFactory.createConnection();
                connection.setRedeliveryPolicy(queuePolicy);
                connection.start();

                session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
                MessageConsumer consumer = session.createConsumer(queue);
                consumer.setMessageListener(this);
            } catch (JMSException e) {
                log.error("JMS queue listener is failed to start", e);
            }
        }

        /** TODO: make sure it is called somehow
        public void stop() {
            log.info("Stopping JMS queue listener");
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (JMSException e) {
                // ignore
            }
        }
        */

        @Override
        public void onMessage(Message message) {
            if (message instanceof ObjectMessage) {
                //TextMessage textMessage = (TextMessage) message;
                ObjectMessage objectMessage = (ObjectMessage) message;
                try {
                    log.debug("Received message: {}, {}", objectMessage, Thread.currentThread().getName());
                    //FileCopyMessage messageObject = FileCopyMessage.fromString(textMessage.getText());
                    FileCopyMessage messageObject = (FileCopyMessage)objectMessage.getObject();
                    log.debug("Restored message object: {}", messageObject);

                    copyFileInTransaction(messageObject, true);
                    commit();
                } catch (TransactionWrapException e) {
                    log.error("unexpected error", e);
                    rollback();
                } catch (JMSException e) {
                    log.error("JMS message receive failure", e);
                    rollback();
                }
            }
        }

        private void rollback() {
            try {
                session.rollback();
            } catch (JMSException ex) {
                log.error("can't rollback JMS session", ex);
            }
        }

        private void commit() throws JMSException {
            session.commit();
        }

        private void copyFileInTransaction(final FileCopyMessage message, final boolean removeSource) throws TransactionWrapException {
            sessionFactory.openSession();
            try {
                transactionSupport.execute(new TransactionCallback<Void>() {
                    @Override
                    public Void doInTransaction() throws Exception {
                        copyFile(message, removeSource);
                        return null;
                    }
                });
            } finally {
                sessionFactory.closeSession();
            }
        }

        private void copyFile(FileCopyMessage message, boolean removeSource) throws RecordNotFoundException {
            DataFile dataFile = fileDao.get(message.getDestinationId());

            DataFileSource source = message.getSource();
            try {
                if (source.exists()) {
                    String digest = fileStore.store(source);
                    dataFile.setDigest(digest);
                    dataFile.setStatus(STORED);
                    fileDao.save(dataFile);
                    if (removeSource) {
                        source.delete();
                    }
                    return;
                } else {
                    log.error("File doesn't exist or is a directory: {}", source);
                }
            } catch (IOException e) {
                log.error("Can't store file in the file store", e);
            }
            dataFile.setStatus(ERROR);
            fileDao.save(dataFile);
        }

        /**
        private void removeFile(File file) {
            try {
                if (!file.exists() || file.delete()) {
                    return;
                }
            } catch (SecurityException e) {
                log.warn("Can't delete file: " + file, e);
            }
            log.warn("Can't delete file: " + file);
        }
        */
    }

    private static class FileCopyMessage implements Serializable {

        private static final long serialVersionUID = 7526471155622776147L;

        //private static String DELIM = ",";
        private long destinationId;
        private DataFileSource source;

        public FileCopyMessage(DataFileSource source, DataFile destination) {
            this(source, destination.getId());
        }

        private FileCopyMessage(DataFileSource source, long destinationId) {
            this.destinationId = destinationId;
            this.source = source;
        }

        public long getDestinationId() {
            return destinationId;
        }

        public DataFileSource getSource() {
            return source;
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
        }

        /**
        public String asString() {
            return destinationId + DELIM + source.toString();
        }

        public static FileCopyMessage fromString(String str) {
            int index = str.indexOf(DELIM);
            if (index > 0) {
                try {
                    int destId = Integer.parseInt(str.substring(0, index));
                    String sourcePath = str.substring(index + 1);
                    return new FileCopyMessage(source, destId);
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
            log.error("Wrong message format: ", str);
            return null;
        }
        */

        @Override
        public String toString() {
            return getClass().getName() + "@{" +
                    "destinationId=" + destinationId +
                    ", source='" + source.toString() + '\'' +
                    '}';
        }
    }
}
