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

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import uk.ac.ebi.fg.annotare2.core.components.EmailMessengerService;
import uk.ac.ebi.fg.annotare2.core.components.Messenger;
import uk.ac.ebi.fg.annotare2.db.dao.MessageDao;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Message;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Override
    public void ticketUpdate(Map<String, String> params) throws Exception
    {
        if (StringUtils.isBlank(params.get("ticketNumber"))) return;

        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                builder.build());
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
                sslConnectionSocketFactory).setRedirectStrategy(new LaxRedirectStrategy()).build();

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                .addTextBody("user", properties.getRtIntegrationUser())
                .addTextBody("pass", properties.getRtIntegrationPassword())
                .addTextBody("content", "CF-Accession: "+ params.get("accessionNumber"));

        HttpPost httppost = new HttpPost(properties.getRtIntegrationUrl() + "ticket/"+params.get("ticketNumber")+"/edit");
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
        sb.append("\nCF-Accession: ");
        sb.append(message.getSubmission().getAccession());
        sb.append("\nCF-SubmissionID: ");
        sb.append(message.getSubmission().getId());
        sb.append("\nCF-Directory: ");
        sb.append("/ebi/microarray/home/fgpt/sw/lib/perl/testing/files/"+ properties.getSubsTrackingUser()+"/"+ properties.getSubsTrackingExperimentType() +"_"+message.getSubmission().getSubsTrackingId());
        sb.append("\nCF-ExperimentType: ");
        try {
            sb.append(((ExperimentSubmission) message.getSubmission()).getExperimentProfile().getType().isSequencing() ? "HTS" : "MA");
        }
        catch (Exception x)
        {
            messenger.send("Caanot get experiment type",x);
        }
        return sb.toString();
    }

    private String getReplyMessageContent(Message message) {
        //boolean isInternalSender = message.getFrom().matches(".*annotare[@]ebi[.]ac[.]uk.*");
        //boolean isInternalRecipient = message.getTo().matches(".*[a-z][@]ebi[.]ac[.]uk.*");
        // TODO: fix is internal recipient
        //boolean isInternalRecipient = message.getTo().matches(".*awais[@]ebi[.]ac[.]uk.*");

        String[] str = message.getTo().split("<");
        boolean isCurator = str[0].replaceAll("\"","").equalsIgnoreCase("Annotare RT ");

        StringBuilder sb = new StringBuilder();
        sb.append("Action: ");
        sb.append(isCurator ? "comment" : "correspond");
        sb.append("\nSubject: ");
        sb.append(message.getSubject());
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

}
