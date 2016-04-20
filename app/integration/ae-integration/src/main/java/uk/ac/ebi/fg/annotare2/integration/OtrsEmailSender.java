package uk.ac.ebi.fg.annotare2.integration;

import com.google.inject.Inject;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class OtrsEmailSender {
    private static final Logger log = LoggerFactory.getLogger(OtrsEmailSender.class);

    private final ExtendedAnnotareProperties properties;

    private final static String UTF8 = "UTF-8";

    @Inject
    public OtrsEmailSender(ExtendedAnnotareProperties properties) {
        this.properties = properties;
    }


    public void sendFromTemplate(String template, Map<String, String> parameters) {
        try {
            StrSubstitutor sub = new StrSubstitutor(parameters);

            String from = sub.replace(properties.getEmailFromAddress(template));
            String to = sub.replace(properties.getEmailToAddress(template));
            String bcc = properties.getEmailBccAddress();
            String subject = sub.replace(properties.getEmailSubject(template));
            String body = sub.replace(properties.getEmailTemplate(template));
            if (null != to) {
                send(to, bcc, subject, body, from);
            }
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }

    private void send(String recipients, String hiddenRecipients, String subject, String message, String from) {

    }
}
