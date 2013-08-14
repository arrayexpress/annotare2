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
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.om.DataFile;

import javax.jms.*;

/**
 * @author Olga Melnichuk
 */
public class CopyFileMessageQueue extends AbstractIdleService {

    private static final Logger log = LoggerFactory.getLogger(CopyFileMessageQueue.class);

    private static final String TOPIC = "FILE_COPY_QUEUE";

    private final BrokerService broker;
    private final CopyFileMessageListener copyFileMessageListener;

    public CopyFileMessageQueue() {
        broker = new BrokerService();
        broker.setBrokerName("localhost");
        broker.setUseJmx(false);

        copyFileMessageListener = new CopyFileMessageListener();
    }

    public void offer(DataFile dataFile) throws JMSException {
        //TODO send a real message here
        send();
    }

    @Override
    protected void startUp() throws Exception {
        log.info("Starting JMS broker...");
        broker.start();

        Thread.sleep(3000);
        copyFileMessageListener.startListening();
    }

    @Override
    protected void shutDown() throws Exception {
        copyFileMessageListener.stopListening();

        log.info("Stopping JMS broker...");
        broker.stop();
    }

    private void send() throws JMSException {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?create=false");
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = session.createQueue(TOPIC);

            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            String text = "Hello world! From: " + Thread.currentThread().getName() + " : " + this.hashCode();
            TextMessage message = session.createTextMessage(text);

            log.debug("Sent message: '{}' : {}", message.hashCode(), Thread.currentThread().getName());
            producer.send(message);

            session.close();
            connection.close();
    }

    private static class CopyFileMessageListener implements MessageListener {
        private Connection connection;

        public void startListening() {
            log.info("Starting to listen JMS topic: {}", TOPIC);

            try {
                ConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost?create=false");
                connection = factory.createConnection();
                Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                Destination destination = session.createQueue(TOPIC);

                MessageConsumer consumer = session.createConsumer(destination);
                consumer.setMessageListener(this);

                connection.start();
            } catch (JMSException e) {
                log.error("The message listener is failed to start", e);
            }
        }

        public void stopListening() {
            log.info("Stopping to listen JMS topic: " + TOPIC);
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (JMSException e) {
                log.error("The message listener is failed to stop", e);
            }
        }

        public void onMessage(Message message) {
            if (message instanceof TextMessage) {
                TextMessage txtMsg = (TextMessage) message;
                try {
                    log.debug("got message: {}", txtMsg.getText());
                    message.acknowledge();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
