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

package uk.ac.ebi.fg.annotare2.web.server.services.files;

import com.google.inject.Inject;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.ScheduledMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.web.server.JmsResources;

import javax.jms.*;

/**
 * @author Olga Melnichuk
 */
public class FileCopyMessageQueue {

    private static final Logger log = LoggerFactory.getLogger(FileCopyMessageQueue.class);

    private final FileCopyProducer producer;
    private final FileCopyConsumer consumer;

    @Inject
    public FileCopyMessageQueue(JmsResources jmsResources, FileCopyConsumer consumer) {

        producer = new FileCopyProducer();
        this.consumer = consumer;

        producer.start(jmsResources.getConnectionFactory(), jmsResources.getFileCopyQueue());
        consumer.start(jmsResources.getConnectionFactory(), jmsResources.getFileCopyQueue());
    }

    public void schedule(DataFileSource source, DataFile destination, boolean shouldRemoveSource)
            throws JMSException {
        producer.send(new FileCopyMessage(source, destination, shouldRemoveSource));
    }

    public void shutdown() {
        producer.stop();
        consumer.stop();
    }

    private static class FileCopyProducer {

        private Queue queue;
        private ActiveMQConnection connection;

        public void start(ConnectionFactory connectionFactory, Queue queue) {
            try {
                this.queue = queue;
                this.connection = (ActiveMQConnection)connectionFactory.createConnection();
                this.connection.setUseAsyncSend(true);
                this.connection.start();
            } catch (JMSException e) {
                log.error("JMS sender has failed to start", e);
            }
        }

        public void stop() {
            try {
                connection.stop();
                connection.close();
            } catch (JMSException e) {
                log.error("Unable to stop/close JMS connection", e);
            }
        }

        private void send(FileCopyMessage message) throws JMSException {
            Session session = null;
            MessageProducer producer = null;
            try {
                session = connection.createSession(false, ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE);

                producer = session.createProducer(queue);
                producer.setDeliveryMode(DeliveryMode.PERSISTENT);

                ObjectMessage objectMessage = session.createObjectMessage(message);
                objectMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000);

                log.debug("Sent message: '{}' : {}", objectMessage, Thread.currentThread().getName());
                producer.send(objectMessage);
            } finally {
                if (producer != null) {
                    try {
                        producer.close();
                    } catch (JMSException e) {
                        log.error("Unable to close JMS producer", e);
                    }
                }
                if (session != null) {
                    try {
                        session.close();
                    } catch (JMSException e) {
                        log.error("Unable to close JMS session", e);
                    }
                }

            }
        }
    }
}
