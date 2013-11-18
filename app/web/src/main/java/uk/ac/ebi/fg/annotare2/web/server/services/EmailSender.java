package uk.ac.ebi.fg.annotare2.web.server.services;

/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

import com.google.inject.Inject;
import org.apache.commons.lang.text.StrSubstitutor;
import uk.ac.ebi.fg.annotare2.web.server.properties.AnnotareProperties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;

public class EmailSender
{
    private final AnnotareProperties properties;

    public static final String NEW_USER_TEMPLATE = "new-user";

    @Inject
    public EmailSender(AnnotareProperties properties) {
        this.properties = properties;
    }

    public void sendFromTemplate(String template, Map<String, String> parameters)
            throws MessagingException {
        StrSubstitutor sub = new StrSubstitutor(parameters);
        String to = sub.replace(properties.getEmailToAddress(template));
        String bcc = properties.getEmailBccAddress();
        String subject = sub.replace(properties.getEmailSubject(template));
        String body = sub.replace(properties.getEmailTemplate(template));
        if (null != to) {
            send(to.split("\\s,\\s"),
                    null != bcc ? bcc.split("\\s,\\s") : null,
                    subject,
                    body,
                    properties.getEmailFromAddress()
            );
        }
    }

    public void send(String recipients[], String hiddenRecipients[], String subject, String message, String from)
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
        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // set hidden recipients (BCC) address
        if (null != hiddenRecipients) {
            InternetAddress[] addressBcc = new InternetAddress[hiddenRecipients.length];
            for (int i = 0; i < hiddenRecipients.length; i++) {
                addressBcc[i] = new InternetAddress(hiddenRecipients[i]);
            }
            msg.setRecipients(Message.RecipientType.BCC, addressBcc);
        }

        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setText(message, "UTF-8");
        Transport.send(msg);
    }
}