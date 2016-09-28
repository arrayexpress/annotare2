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

package uk.ac.ebi.fg.annotare2.integration;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import uk.ac.ebi.fg.annotare2.core.components.EmailMessengerService;
import uk.ac.ebi.fg.annotare2.core.components.Messenger;
import uk.ac.ebi.fg.annotare2.db.dao.MessageDao;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Message;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.util.HibernateEntity;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.otrs.OtrsConnector;
import uk.ac.ebi.fg.annotare2.otrs.OtrsMessageParser;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;

import javax.mail.Multipart;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

import static com.google.common.base.Strings.isNullOrEmpty;

public class RtMessengerService extends EmailMessengerService {

    //private static final Logger log = LoggerFactory.getLogger(OtrsMessengerService.class);

    private final ExtendedAnnotareProperties properties;
    //private final HibernateSessionFactory sessionFactory;
    private final Messenger messenger;
    private final SubmissionDao submissionDao;

    @Inject
    public RtMessengerService(HibernateSessionFactory sessionFactory,
                              ExtendedAnnotareProperties properties,
                              Messenger messenger,
                              MessageDao messageDao,
                              SubmissionDao submissionDao) {
        super(sessionFactory, properties, messageDao);
        //this.sessionFactory = sessionFactory;
        this.messenger = messenger;
        this.properties = properties;
        this.submissionDao = submissionDao;
    }

    @Override
    protected void sendMessage(Message message) throws Exception {
        if (properties.isRtIntegrationEnabled() && null != message.getSubmission()) {
            try {
                Submission submission = message.getSubmission();
                String ticketNumber = submission.getRtTicketNumber();
                if (isNullOrEmpty(ticketNumber)) {
                    ticketNumber = createRtTicket(submission, message);
                    if (ticketNumber==null) {
                        throw new Exception ("Unable to create RT ticket");
                    }
                    submission.setRtTicketNumber(ticketNumber);
                    submissionDao.save(submission);
                }
                else {
                    sendRtMessage(ticketNumber, message);
                }
            } catch (Throwable x){
                messenger.send("There was a problem sending message " + String.valueOf(message.getId()), x);
                throw x;
            }
        } else {
            super.sendMessage(message);
        }
    }

    private String createRtTicket(Submission submission, Message message) throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                builder.build());
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
                sslConnectionSocketFactory).setRedirectStrategy(new LaxRedirectStrategy()).build();

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                .addTextBody("user", properties.getRtIntegrationUser())
                .addTextBody("pass", properties.getRtIntegrationPassword())
                .addTextBody("content", getNewMessageContent(message));

        HttpPost httppost = new HttpPost( properties.getRtIntegrationUrl() + "ticket/new");
        httppost.setEntity(entityBuilder.build());
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity r = response.getEntity();
        BufferedReader inp = new BufferedReader(new InputStreamReader(r.getContent()));
        String line, ticket = null;
        Pattern p = Pattern.compile("# Ticket (\\d+) created.");
        while ((line = inp.readLine()) != null) {
            Matcher m = p.matcher(line);
            if (m.find()) {
                ticket = m.group(1);
            }
        }
        return ticket;
    }

    private String getNewMessageContent(Message message) {
        /*boolean isInternalSender = message.getFrom().matches(".*annotare[@]ebi[.]ac[.]uk.*");
        boolean isInternalRecipient = message.getTo().matches(".*annotare[@]ebi[.]ac[.]uk.*");
        Map<String, String> templateParams = ImmutableMap.of(
                "original.subject", message.getSubject(),
                "original.body", message.getBody(),
                "ticket.number", "1234");// message.getSubmission().getRtTicketNumber());*/
        //StrSubstitutor sub = new StrSubstitutor(templateParams);
        StringBuilder sb = new StringBuilder();
        sb.append("Requestor: ");
        sb.append(message.getUser().getEmail());
        sb.append("\nSubject: ");
        sb.append(message.getSubject());
        sb.append("\nText: ");
        String body = message.getBody();
        sb.append(body.replaceAll("\\n","\n "));
        sb.append("\nQueue: ");
        sb.append(properties.getRtQueueName());
        return sb.toString();
    }

    private String getReplyMessageContent(Message message) {
        //boolean isInternalSender = message.getFrom().matches(".*annotare[@]ebi[.]ac[.]uk.*");
        //boolean isInternalRecipient = message.getTo().matches(".*annotare[@]ebi[.]ac[.]uk.*");
        // TODO: fix is internal recipient
        boolean isInternalRecipient = message.getTo().matches(".*awais[@]ebi[.]ac[.]uk.*");

        StringBuilder sb = new StringBuilder();
        sb.append("Action: ");
        sb.append(isInternalRecipient ? "comment" : "correspond");
        sb.append("\nText: ");
        String body = message.getBody();
        sb.append(body.replaceAll("\\n","\n "));
        return sb.toString();
    }


    private void sendRtMessage(String ticketNumber, Message message) throws Exception {
        if (StringUtils.isBlank(ticketNumber)) return;

        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                builder.build());
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
                sslConnectionSocketFactory).setRedirectStrategy(new LaxRedirectStrategy()).build();

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                .addTextBody("user", properties.getRtIntegrationUser())
                .addTextBody("pass", properties.getRtIntegrationPassword())
                .addTextBody("content", getReplyMessageContent(message));

        HttpPost httppost = new HttpPost(properties.getRtIntegrationUrl() + "ticket/"+ticketNumber+"/comment");
        httppost.setEntity(entityBuilder.build());
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity r = response.getEntity();
        BufferedReader inp = new BufferedReader(new InputStreamReader(r.getContent()));
        String line, ticket = null;
        Pattern p = Pattern.compile("# Ticket (\\d+) created.");
        while ((line = inp.readLine()) != null) {
            System.out.println(line);
        }
    }



    /*
    private String createOtrsTicket(Submission submission) throws Exception {
        OtrsConnector otrs = getOtrsConnector();

        ExperimentProfileType submissionType = ExperimentProfileType.ONE_COLOR_MICROARRAY;
        submission = HibernateEntity.deproxy(submission, Submission.class);
        if (submission instanceof ExperimentSubmission) {
            try {
                ExperimentProfile exp = ((ExperimentSubmission) submission).getExperimentProfile();
                submissionType = exp.getType();
            } catch (DataSerializationException x) {}
        }

        Object ticketId = messageParser.toObject(
                otrs.dispatchCall(
                    "TicketObject",
                    "TicketCreate",
                    new ImmutableMap.Builder<String, Object>()
                            .put("TypeID", 1)
                            .put("QueueID", submissionType.isSequencing() ? 72 : 62)
                            .put("LockID", 1)
                            .put("Title", (submissionType.isSequencing() ? "HTS" : "MA") + " submission: " + submission.getTitle())
                            .put("OwnerID", 1)
                            .put("UserID", 1)
                            .put("PriorityID", 3)
                            .put("State", "new")
                            .put("CustomerID", submission.getCreatedBy().getEmail())
                            .put("CustomerUser", submission.getCreatedBy().getEmail())
                            .build()
                    ),
                Object.class
        );
        if (null == ticketId) {
            throw new Exception("Unable to create OTRS ticket");
        }
        for (int i = 0; i < 10; ++i) {
            Object ticketNumber = messageParser.toObject(
                    otrs.dispatchCall(
                            "TicketObject",
                            "TicketNumberLookup",
                            ImmutableMap.of("TicketID", ticketId)
                    ),
                    Object.class
            );
            if (null != ticketNumber) {
                return String.valueOf(ticketNumber);
            }
            Thread.sleep(500);
        }
        throw new Exception("Unable to obtain OTRS ticket number for ticket " + String.valueOf(ticketId));
    }

    private void sendOtrsMessage(String ticketNumber, Message message, boolean isNewTicket) throws Exception {
        OtrsConnector otrs = getOtrsConnector();

        Object ticketId = messageParser.toObject(
                otrs.dispatchCall(
                        "TicketObject",
                        "TicketCheckNumber",
                        ImmutableMap.of("Tn", (Object)ticketNumber)
                ),
                Object.class
        );
        if (null != ticketId) {
            boolean isInternalSender = message.getFrom().matches(".*annotare[@]ebi[.]ac[.]uk.*");
            boolean isInternalRecipient = message.getTo().matches(".*annotare[@]ebi[.]ac[.]uk.*");
            Map<String, String> templateParams = ImmutableMap.of(
                    "original.subject", message.getSubject(),
                    "original.body", message.getBody(),
                    "ticket.number", ticketNumber);
            StrSubstitutor sub = new StrSubstitutor(templateParams);

            for (int i = 0; i < 3; ++i) {
                Object articleId = messageParser.toObject(
                        otrs.dispatchCall(
                                "TicketObject",
                                isInternalRecipient ? "ArticleCreate" : "ArticleSend",
                                new ImmutableMap.Builder<String, Object>()
                                        .put("TicketID", ticketId)
                                        .put("ArticleType", isInternalSender ? "email-internal" : "email-external")
                                        .put("SenderType", isInternalSender ? "agent" : "customer")
                                        .put("HistoryType", isNewTicket ? "EmailCustomer" : "FollowUp")
                                        .put("HistoryComment", "Sent from Annotare")
                                        .put("From", message.getFrom())
                                        .put("To", message.getTo())
                                        .put("Subject", isInternalRecipient ? message.getSubject() : sub.replace(properties.getOtrsIntegrationSubjectTemplate()))
                                        .put("Type", "text/plain")
                                        .put("Charset", EMAIL_ENCODING_UTF_8)
                                        .put("Body", isInternalRecipient ? message.getBody() : sub.replace(properties.getOtrsIntegrationBodyTemplate()))
                                        .put("UserID", 1)
                                        .build()
                        ),
                        Object.class
                );
                if (null != articleId) {
                    return;
                }
                Thread.sleep(500);
            }
            throw new Exception("Unable to create article for ticket " + String.valueOf(ticketId));
        } else {
            throw new Exception("Unable to get ticket ID for ticket #" + ticketNumber);
        }
    }

    private OtrsConnector getOtrsConnector() throws Exception {
        return new OtrsConnector(
                properties.getOtrsIntegrationUrl(),
                properties.getOtrsIntegrationUser(),
                properties.getOtrsIntegrationPassword()
        );
    }*/
}
