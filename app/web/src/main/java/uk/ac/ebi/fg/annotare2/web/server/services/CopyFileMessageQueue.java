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

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.dao.DataFileDao;
import uk.ac.ebi.fg.annotare2.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.om.DataFile;

import javax.jms.*;
import java.io.File;
import java.io.IOException;

import static uk.ac.ebi.fg.annotare2.om.enums.DataFileStatus.ERROR;
import static uk.ac.ebi.fg.annotare2.om.enums.DataFileStatus.STORED;

/**
 * @author Olga Melnichuk
 */
public class CopyFileMessageQueue extends AbstractIdleService {

    private static final Logger log = LoggerFactory.getLogger(CopyFileMessageQueue.class);

    private final BrokerService broker;
    private final CopyFileMessageConsumer consumer;
    private final CopyFileMessageProducer producer;

    @Inject
    public CopyFileMessageQueue(DataFileStore fileStore, DataFileDao fileDao) {
        broker = new BrokerService();
        broker.setBrokerName("localhost");
        broker.setUseJmx(false);
        // todo broker.setDataDirectory();

        String topic = "COPY_FILE_QUEUE";
        producer = new CopyFileMessageProducer("vm://localhost?create=false", topic);
        consumer = new CopyFileMessageConsumer("vm://localhost?create=false", topic,
                new CopyFileMessageListener(fileStore, fileDao));
    }

    public void offer(File source, DataFile destination) throws JMSException {
        producer.send(new CopyFileMessage(source, destination));
    }

    @Override
    protected void startUp() throws Exception {
        log.info("Starting JMS broker...");
        broker.start();

        Thread.sleep(3000);
        consumer.startListening();
    }

    @Override
    protected void shutDown() throws Exception {
        consumer.stopListening();

        log.info("Stopping JMS broker...");
        broker.stop();
        broker.waitUntilStopped();
    }

    private static class CopyFileMessageProducer {

        private final String brokerUrl;
        private final String queueTopic;

        public CopyFileMessageProducer(String brokerUrl, String queueTopic) {
            this.brokerUrl = brokerUrl;
            this.queueTopic = queueTopic;
        }

        private void send(CopyFileMessage message) throws JMSException {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = connectionFactory.createConnection();
            Session session = null;
            MessageProducer producer = null;
            try {
                connection.start();

                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                producer = session.createProducer(session.createQueue(queueTopic));
                producer.setDeliveryMode(DeliveryMode.PERSISTENT);

                TextMessage textMessage = session.createTextMessage(message.asString());

                log.debug("Sent message: '{}' : {}", textMessage.getText(), Thread.currentThread().getName());
                producer.send(textMessage);

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

    private static class CopyFileMessageConsumer {

        private final String brokerUrl;
        private final String queueTopic;
        private final CopyFileMessageListener listener;
        private Connection connection;

        public CopyFileMessageConsumer(String brokerUrl, String queueTopic, CopyFileMessageListener listener) {
            this.brokerUrl = brokerUrl;
            this.queueTopic = queueTopic;
            this.listener = listener;
        }

        public void startListening() {
            log.info("Starting to listen JMS topic: {}", queueTopic);

            try {
                ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
                connection = factory.createConnection();
                connection.start();

                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageConsumer consumer = session.createConsumer(session.createQueue(queueTopic));
                consumer.setMessageListener(listener);
            } catch (JMSException e) {
                log.error("JMS queue listener is failed to start", e);
            }
        }

        public void stopListening() {
            log.info("Stopping to listen JMS topic: {}", queueTopic);
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (JMSException e) {
                // ignore
            }
        }
    }

    private static class CopyFileMessage {

        private static String DELIM = ",";
        private long destinationId;
        private File sourcePath;

        public CopyFileMessage(File source, DataFile destination) {
            this(source, destination.getId());
        }

        private CopyFileMessage(File sourcePath, long destinationId) {
            this.destinationId = destinationId;
            this.sourcePath = sourcePath;
        }

        public long getDestinationId() {
            return destinationId;
        }

        public File getSource() {
            return sourcePath;
        }

        public String asString() {
            return destinationId + DELIM + sourcePath;
        }

        public static CopyFileMessage fromString(String str) {
            int index = str.indexOf(DELIM);
            if (index > 0) {
                try {
                    int destId = Integer.parseInt(str.substring(0, index));
                    String sourcePath = str.substring(index + 1);
                    return new CopyFileMessage(new File(sourcePath), destId);
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
            log.error("Wrong message format: ", str);
            return null;
        }

        @Override
        public String toString() {
            return "CopyFileMessage{" +
                    "destinationId=" + destinationId +
                    ", sourcePath='" + sourcePath + '\'' +
                    '}';
        }
    }

    private static class CopyFileMessageListener implements MessageListener {

        private final DataFileStore fileStore;
        private final DataFileDao fileDao;

        private CopyFileMessageListener(DataFileStore fileStore, DataFileDao fileDao) {
            this.fileStore = fileStore;
            this.fileDao = fileDao;
        }

        @Override
        public void onMessage(Message message) {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                try {
                    log.debug("Received message: {}, {}", textMessage.getText(), Thread.currentThread().getName());
                    CopyFileMessage messageObject = CopyFileMessage.fromString(textMessage.getText());
                    log.debug("Restored message object: {}", messageObject);

                    copyFile(messageObject, true);

                    message.acknowledge();
                } catch (JMSException e) {
                    log.error("JMS message receive failure", e);
                }
            }
        }

        // TODO: just a draft; proper database integration is needed
        private void copyFile(CopyFileMessage message, boolean removeSource) {
            DataFile dataFile = null;
            try {
                dataFile = fileDao.get(message.getDestinationId());
            } catch (RecordNotFoundException e) {
                log.info("The file record with id='{}' was not found in the database. Skipping the task");
                return;
            }

            File source = message.getSource();
            try {
                if (source.exists() && !source.isDirectory()) {
                    String digest = fileStore.store(source);
                    dataFile.setDigest(digest);
                    dataFile.setStatus(STORED);
                    return;
                } else {
                    log.error("File doesn't exist or is a directory: {}", source);
                }
            } catch (IOException e) {
                log.error("Can't store file in the file store", e);
            } finally {
                if (removeSource) {
                    if (!source.delete()) {
                        log.warn("Can't delete file: " + source);
                    }
                }
            }
            dataFile.setStatus(ERROR);
        }
    }
}
