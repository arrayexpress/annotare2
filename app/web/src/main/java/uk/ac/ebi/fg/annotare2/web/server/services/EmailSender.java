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

package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.web.server.properties.AnnotareProperties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Properties;

public class EmailSender {
    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    private final AnnotareProperties properties;

    private final static String UTF8 = "UTF-8";

    public static final String NEW_USER_TEMPLATE = "new-user";
    public static final String VERIFY_EMAIL_TEMPLATE = "verify-email";
    public static final String WELCOME_TEMPLATE = "welcome";
    public static final String CHANGE_PASSWORD_REQUEST_TEMPLATE = "change-password-request";
    public static final String CHANGE_PASSWORD_CONFIRMATION_TEMPLATE = "change-password-confirmation";
    public static final String INITIAL_SUBMISSION_TEMPLATE = "initial-submission";
    public static final String INITIAL_SUBMISSION_OTRS_TEMPLATE = "initial-submission-otrs";
    public static final String REPEAT_SUBMISSION_OTRS_TEMPLATE = "repeat-submission-otrs";
    public static final String REJECTED_SUBMISSION_TEMPLATE = "rejected-submission";
    public static final String ACCESSION_UPDATE_TEMPLATE = "accession-update-submission";
    public static final String EXCEPTION_REPORT_TEMPLATE = "exception-report";

    @Inject
    public EmailSender(AnnotareProperties properties) {
        this.properties = properties;
    }

    public void sendException(String note, Throwable x) {
        try {
            Thread currentThread = Thread.currentThread();
            String hostName = "unknown";
            try {
                InetAddress localMachine = InetAddress.getLocalHost();
                hostName = localMachine.getHostName();
            } catch (UnknownHostException xx) {
                log.error("Unable to obtain a hostname", xx);
            }
            sendFromTemplate(
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
    public void sendFromTemplate(String template, Map<String, String> parameters)
            throws MessagingException {
        StrSubstitutor sub = new StrSubstitutor(parameters);
        String to = sub.replace(properties.getEmailToAddress(template));
        String bcc = properties.getEmailBccAddress();
        String subject = sub.replace(properties.getEmailSubject(template));
        String body = sub.replace(properties.getEmailTemplate(template));
        if (null != to) {
            send(to, bcc, subject, body, properties.getEmailFromAddress());
        }
    }

    private void send(String recipients, String hiddenRecipients, String subject, String message, String from)
            throws MessagingException {

        //Set the host SMTP address and port
        Properties p = new Properties();
        p.put("mail.smtp.host", properties.getEmailSmtpHost());
        p.put("mail.smtp.port", properties.getEmailSmtpPort());

        // create some properties and get the default Session
        Session session = Session.getDefaultInstance(p, null);
        session.setDebug(false);

        // create a message
        MimeMessage msg = new MimeMessage(session);

        // set originator (FROM) address
        InternetAddress addressFrom = new InternetAddress(from);
        msg.setFrom(addressFrom);

        // set recipients (TO) address
        msg.setRecipients(Message.RecipientType.TO, parseAddresses(recipients));

        // set hidden recipients (BCC) address
        if (null != hiddenRecipients) {
            msg.setRecipients(Message.RecipientType.BCC, parseAddresses(hiddenRecipients));
        }
        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setText(message, UTF8);
        Transport.send(msg);
    }

    InternetAddress[] parseAddresses(String addresses) throws AddressException {
        InternetAddress[] recips = InternetAddress.parse(addresses, false);
        for(int i=0; i<recips.length; i++) {
            try {
                recips[i] = new InternetAddress(recips[i].getAddress(), recips[i].getPersonal(), UTF8);
            } catch(UnsupportedEncodingException e) {
                throw new RuntimeException("Unable to set encoding", e);
            }
        }
        return recips;
    }

    private String getStackTrace(Throwable x) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        x.printStackTrace(printWriter);
        return result.toString();
    }
}