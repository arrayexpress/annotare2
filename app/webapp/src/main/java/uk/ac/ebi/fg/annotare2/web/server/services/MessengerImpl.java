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

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import org.apache.commons.lang.text.StrSubstitutor;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.core.components.Messenger;
import uk.ac.ebi.fg.annotare2.core.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.core.transaction.Transactional;
import uk.ac.ebi.fg.annotare2.db.dao.MessageDao;
import uk.ac.ebi.fg.annotare2.db.model.Message;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.User;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class MessengerImpl implements Messenger {
    private static final Logger log = LoggerFactory.getLogger(MessengerImpl.class);

    private final AnnotareProperties properties;
    private final HibernateSessionFactory sessionFactory;
    private final MessageDao messageDao;

    private final static String UTF8 = "UTF-8";

    public static final String NEW_USER_TEMPLATE = "new-user";
    public static final String VERIFY_EMAIL_TEMPLATE = "verify-email";
    public static final String WELCOME_TEMPLATE = "welcome";
    public static final String CHANGE_PASSWORD_REQUEST_TEMPLATE = "change-password-request";
    public static final String CHANGE_PASSWORD_CONFIRMATION_TEMPLATE = "change-password-confirmation";
    public static final String EXCEPTION_REPORT_TEMPLATE = "exception-report";
    public static final String SUBMISSION_FEEDBACK_TEMPLATE = "submission-feedback";
    public static final String CONTACT_US_TEMPLATE = "contact-us";

    @Inject
    public MessengerImpl(AnnotareProperties properties,
                         HibernateSessionFactory sessionFactory,
                         MessageDao messageDao) {
        this.properties = properties;
        this.sessionFactory = sessionFactory;
        this.messageDao = messageDao;
    }

    @Override
    public void send(String note, Throwable x) {
        try {
            Thread currentThread = Thread.currentThread();
            String hostName = "unknown";
            try {
                InetAddress localMachine = InetAddress.getLocalHost();
                hostName = localMachine.getHostName();
            } catch (UnknownHostException xx) {
                log.error("Unable to obtain a hostname", xx);
            }
            send(
                    EXCEPTION_REPORT_TEMPLATE,
                    ImmutableMap.of(
                            "application.host", hostName,
                            "application.thread", currentThread.getName(),
                            "exception.note", note,
                            "exception.message", (null != x && null != x.getMessage()) ? x.getMessage() : "",
                            "exception.stack", getStackTrace(x)
                    )
            );
        } catch (Throwable xxx) {
            log.error("[SEVERE] Unable to send exception report, error:", xxx);
        }
    }

    @Override
    public void send(String template, Map<String, String> parameters) {
        send(template, parameters, null, null);
    }

    @Override
    public void send(String template, Map<String, String> parameters, User user) {
        send(template, parameters, user, null);
    }

    @Override
    public void send(String template, Map<String, String> parameters, Submission submission) {
        send(template, parameters, submission.getOwnedBy(), submission);
    }

    private void send(String template, Map<String, String> parameters, User user, Submission submission) {
        StrSubstitutor sub = new StrSubstitutor(parameters);

        String from = sub.replace(properties.getEmailFromAddress(template));
        String to = sub.replace(properties.getEmailToAddress(template));
        String subject = sub.replace(properties.getEmailSubject(template));
        String body = sub.replace(properties.getEmailTemplate(template));

        if (null != from && null != to && null != subject && null != body) {
            Session session = sessionFactory.openSession();
            try {
                queueMessage(from, to, subject, body, user, submission);
            } catch (Throwable x) {
                log.error("Unable to queue a message", x);
                throw new RuntimeException(x);
            } finally {
                session.close();
            }
        } else {
            throw new RuntimeException("Unable to queue a message as there are null parameters");
        }
    }

    @Transactional
    private void queueMessage(String from, String to, String subject, String body, User user, Submission submission) {
        Message message = messageDao.create(from, to, subject, body);
        message.setUser(user);
        message.setSubmission(submission);
        messageDao.save(message);
    }

//    private void send(String recipients, String hiddenRecipients, String subject, String message, String from)
//            throws MessagingException {
//
//        //Set the host SMTP address and port
//        Properties p = new Properties();
//        p.put("mail.smtp.host", properties.getEmailSmtpHost());
//        p.put("mail.smtp.port", properties.getEmailSmtpPort());
//
//        // create some properties and get the default Session
//        Session session = Session.getDefaultInstance(p, null);
//        session.setDebug(false);
//
//        // create a message
//        MimeMessage msg = new MimeMessage(session);
//
//        // set originator (FROM) address
//        InternetAddress addressFrom = parseAddresses(from)[0];
//        msg.setFrom(addressFrom);
//
//        // set recipients (TO) address
//        msg.setRecipients(Message.RecipientType.TO, parseAddresses(recipients));
//
//        // set hidden recipients (BCC) address
//        if (null != hiddenRecipients) {
//            msg.setRecipients(Message.RecipientType.BCC, parseAddresses(hiddenRecipients));
//        }
//        // Setting the Subject and Content Type
//        msg.setSubject(subject);
//        msg.setText(message, UTF8);
//        Transport.send(msg);
//    }

//    InternetAddress[] parseAddresses(String addresses) throws AddressException {
//        InternetAddress[] recipients = InternetAddress.parse(addresses, false);
//        for(int i=0; i<recipients.length; i++) {
//            try {
//                recipients[i] = new InternetAddress(recipients[i].getAddress(), recipients[i].getPersonal(), UTF8);
//            } catch(UnsupportedEncodingException e) {
//                throw new RuntimeException("Unable to set encoding", e);
//            }
//        }
//        return recipients;
//    }

    private String getStackTrace(Throwable x) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        x.printStackTrace(printWriter);
        return result.toString();
    }
}