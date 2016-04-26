/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.inject.Inject;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.core.components.MessengerService;
import uk.ac.ebi.fg.annotare2.core.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.core.transaction.Transactional;
import uk.ac.ebi.fg.annotare2.db.dao.MessageDao;
import uk.ac.ebi.fg.annotare2.db.model.Message;
import uk.ac.ebi.fg.annotare2.db.model.enums.MessageStatus;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class EmailMessengerService implements MessengerService {
    private static final Logger logger = LoggerFactory.getLogger(EmailMessengerService.class);

    private static final String EMAIL_ENCODING_UTF_8 = "UTF-8";

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Session session = sessionFactory.openSession();
            try {
                periodicRun();
            } catch (Throwable x) {
                logger.error(x.getMessage(), x);
            } finally {
                session.close();
            }
        }
    };

    private final HibernateSessionFactory sessionFactory;
    private final AnnotareProperties properties;
    private final MessageDao messageDao;

    @Inject
    public EmailMessengerService(HibernateSessionFactory sessionFactory,
                                 AnnotareProperties properties,
                                 MessageDao messageDao) {
        this.sessionFactory = sessionFactory;
        this.properties = properties;
        this.messageDao = messageDao;

    }

    @PostConstruct
    protected void startUp() throws Exception {
        scheduler.scheduleWithFixedDelay(runnable, 0, 1, MINUTES);
    }

    @PreDestroy
    protected void shutDown() throws Exception {
        scheduler.shutdown();
        if (scheduler.awaitTermination(1, MINUTES)) {
            logger.info("Messaging service periodic process has shut down");
        } else {
            logger.warn("Messaging service periodic process has failed to shut down properly");
        }
    }

    @Override
    public void processQueue() {
        scheduler.schedule(runnable, 100, MILLISECONDS);
    }

    private void periodicRun() throws Exception {
        for (Message msg : messageDao.getMessagesByStatus(MessageStatus.QUEUED)) {
            deliverMessage(msg);
        }
    }

    @Transactional
    protected void deliverMessage(Message message) {
        try {
            sendEmail(message.getFrom(), message.getTo(), message.getSubject(), message.getBody());
            messageDao.markSent(message);
        } catch (Exception x) {
            messageDao.markFailed(message);
        }
    }

    private void sendEmail(String from, String recipients, String subject, String message)
        throws MessagingException, UnsupportedEncodingException {

        //Set the host SMTP address and port
        Properties p = new Properties();
        p.put("mail.smtp.host", properties.getEmailSmtpHost());
        p.put("mail.smtp.port", properties.getEmailSmtpPort());

        // create some properties and get the default Session
        javax.mail.Session session = javax.mail.Session.getDefaultInstance(p, null);
        session.setDebug(false);

        // create a message
        MimeMessage msg = new MimeMessage(session);

        // set originator (FROM) address
        InternetAddress addressFrom = parseAddresses(from)[0];
        msg.setFrom(addressFrom);

        // set recipients (TO) address
        msg.setRecipients(javax.mail.Message.RecipientType.TO, parseAddresses(recipients));

        // set hidden recipients (BCC) address
        if (!isNullOrEmpty(properties.getEmailBccAddress())) {
            msg.setRecipients(javax.mail.Message.RecipientType.BCC, parseAddresses(properties.getEmailBccAddress()));
        }
        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setText(message, EMAIL_ENCODING_UTF_8);
        Transport.send(msg);
    }

    InternetAddress[] parseAddresses(String addresses) throws AddressException, UnsupportedEncodingException {
        InternetAddress[] recipients = InternetAddress.parse(addresses, false);
        for(int i=0; i<recipients.length; i++) {
            recipients[i] = new InternetAddress(recipients[i].getAddress(), recipients[i].getPersonal(), EMAIL_ENCODING_UTF_8);
        }
        return recipients;
    }
}