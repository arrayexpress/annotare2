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
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.core.components.EmailMessengerService;
import uk.ac.ebi.fg.annotare2.core.components.Messenger;
import uk.ac.ebi.fg.annotare2.db.dao.MessageDao;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Message;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.util.HibernateEntity;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;

public class RtMessengerService extends EmailMessengerService {

    private static final Logger logger = LoggerFactory.getLogger(RtMessengerService.class);

    private final ExtendedAnnotareProperties properties;
    private final Messenger messenger;
    private final SubmissionDao submissionDao;
    private final HibernateSessionFactory sessionFactory;

    @Inject
    public RtMessengerService(HibernateSessionFactory sessionFactory,
                              ExtendedAnnotareProperties properties,
                              Messenger messenger,
                              MessageDao messageDao,
                              SubmissionDao submissionDao) {
        super(sessionFactory, properties, messageDao);
        this.messenger = messenger;
        this.properties = properties;
        this.submissionDao = submissionDao;
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected void sendMessage(Message message) throws Exception {
        if (properties.isRtIntegrationEnabled() && null != message.getSubmission()) {
            try {
                String errorTrace = "";
                Submission submission = message.getSubmission();
                submission = (Submission) sessionFactory.getCurrentSession().get( Submission.class, submission.getId() );
                submission = HibernateEntity.deproxy(submission, Submission.class);
                String ticketNumber = submission.getRtTicketNumber();
                logger.debug("Checking if RT ticket number is null: "+ ticketNumber);
                if (isNullOrEmpty(ticketNumber)) {
                    ticketNumber = createRtTicket(submission, message);
                    if (ticketNumber==null) {
                        throw new Exception ("Unable to create RT ticket\n"+ errorTrace);
                    }
                    submission.setRtTicketNumber(ticketNumber);
                    logger.debug("Saving new RT ticket number: "+ ticketNumber);
                    submissionDao.save(submission);
                    logger.debug("Flushing session for "+ ticketNumber);
                    sessionFactory.getCurrentSession().flush();
                    submission = HibernateEntity.deproxy(submission, Submission.class);
                    logger.debug("New RT ticket number is : "+ submission.getRtTicketNumber());
                }
                else {
                    sendRtMessage(ticketNumber, message);
                    logger.debug("RT ticket number is : "+ submission.getRtTicketNumber());
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
    public boolean checkRtServerStatus() throws Exception
    {
        boolean serverStatus = false;
        try {

            List<BasicNameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair("user",properties.getRtIntegrationUser()));
            params.add(new BasicNameValuePair("pass",properties.getRtIntegrationPassword()));
            params.add(new BasicNameValuePair("content",""));

            SSLContextBuilder builder = new SSLContextBuilder();

            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                    builder.build());
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
                    sslConnectionSocketFactory).setRedirectStrategy(new LaxRedirectStrategy()).build();

            /*MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                    .addTextBody("user", properties.getRtIntegrationUser())
                    .addTextBody("pass", properties.getRtIntegrationPassword())
                    .addTextBody("content", "");*/

            HttpPost httppost = new HttpPost(properties.getRtIntegrationUrl());
            httppost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r = response.getEntity();
            BufferedReader inp = new BufferedReader(new InputStreamReader(r.getContent()));
            String line;
            try {
                Pattern p = Pattern.compile("RT/4.2.12 200 Ok");
                while ((line = inp.readLine()) != null) {
                    logger.debug(line);
                    Matcher m = p.matcher(line);
                    if (m.find()) {
                        serverStatus = true;
                    }
                }
            } catch (Exception e) {
                logger.error("Error parsing RT response",e);
            }
    //        httpclient.close();
        }catch(Exception e)
        {
            logger.error("Error checking Rt Server Status",e);
            serverStatus = false;

        }

        return serverStatus;
    }

    @Override
    public void ticketUpdate(Map<String, String> param, String ticketNumber) throws Exception
    {
        if (StringUtils.isBlank(ticketNumber)) {
            logger.debug("Rt ticket is null for params: "+ param);
            throw new Exception("Rt ticket is null");
        }
        logger.debug("Updating Rt ticket "+ ticketNumber);
        boolean ticketUpdated = false;
        String errorTrace = "";
        List<BasicNameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("user",properties.getRtIntegrationUser()));
        params.add(new BasicNameValuePair("pass",properties.getRtIntegrationPassword()));
        params.add(new BasicNameValuePair("content",getMessageContent(param)));


        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                builder.build());
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
                sslConnectionSocketFactory).setRedirectStrategy(new LaxRedirectStrategy()).build();

        /*MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                .addTextBody("user", properties.getRtIntegrationUser())
                .addTextBody("pass", properties.getRtIntegrationPassword())
                .addTextBody("content", getMessageContent(param));*/

        HttpPost httppost = new HttpPost(properties.getRtIntegrationUrl() + "ticket/"+ticketNumber+"/edit");
        httppost.setEntity(new UrlEncodedFormEntity(params,StandardCharsets.UTF_8));
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity r = response.getEntity();
        BufferedReader inp = new BufferedReader(new InputStreamReader(r.getContent()));
        String line;
        try
        {
            Pattern p = Pattern.compile("RT/4.2.12 200 Ok");
            while ((line = inp.readLine()) != null) {
                logger.debug(line);
                Matcher m = p.matcher(line);
                if (m.find()) {
                    ticketUpdated = true;
                }
            }
            if (!ticketUpdated) {
                throw new Exception("RT was unable to update ticket.");
            }
        } catch (Exception e) {
            logger.error("Error updating RT ticket",e);
            while ((line = inp.readLine()) != null) {
                logger.debug(line);
                errorTrace = errorTrace + line + "\n";
            }
        }

  //      httpclient.close();
    }

    private String getMessageContent(Map<String, String> params)
    {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry: params.entrySet()
                ) {
            sb.append(entry.getKey());
            sb.append(": ");
            sb.append(entry.getValue());
            sb.append("\n");
        }
        return sb.toString();
    }

    private String createRtTicket(Submission submission, Message message) throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        boolean ticketCreated = false;
        List<BasicNameValuePair> params = new ArrayList<>();

        String errorTrace = "";
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                builder.build());
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
                sslConnectionSocketFactory).setRedirectStrategy(new LaxRedirectStrategy()).build();

        params.add(new BasicNameValuePair("user",properties.getRtIntegrationUser()));
        params.add(new BasicNameValuePair("pass",properties.getRtIntegrationPassword()));
        params.add(new BasicNameValuePair("content",getMessageContent(getNewTicketFieldsMap(message))));


        /*MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                .addTextBody("user", properties.getRtIntegrationUser())
                .addTextBody("pass", properties.getRtIntegrationPassword())
                .addTextBody("content", getMessageContent(getNewTicketFieldsMap(message)));*/

        HttpPost httppost = new HttpPost( properties.getRtIntegrationUrl() + "ticket/new");
        httppost.setEntity(new UrlEncodedFormEntity(params,StandardCharsets.UTF_8));
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity r = response.getEntity();
        BufferedReader inp = new BufferedReader(new InputStreamReader(r.getContent()));
        String line, ticket = null;

        try {
            //Pattern p = Pattern.compile("RT//4.2.12 200 Ok");
            Pattern p = Pattern.compile("# Ticket (\\d+) created.");
            while ((line = inp.readLine()) != null) {
                logger.debug(line);
                Matcher m = p.matcher(line);
                if (m.find()) {
                    ticket = m.group(1);
                    ticketCreated = true;
                }
            }
            if (!ticketCreated) {
                throw new Exception("RT Ticket Creation Failed");
            }
        } catch (Exception e){
            logger.error("Error creating RT ticket",e);
            while ((line = inp.readLine()) != null) {
                logger.error(line);
                errorTrace = errorTrace + line + "\n";
            }
        }
        if(!isNullOrEmpty(ticket))
        {
            try {
                ticketUpdate(
                        new ImmutableMap.Builder<String, String>()
                                .put(RtFieldNames.STATUS, "stalled")
                                .build(),
                        ticket
                );
            } catch (Exception x) {
                messenger.send("There was a problem updating ticket status to stalled " + submission.getRtTicketNumber(), x);
            }
        }
//        httpclient.close();
        return ticket;
    }

    private Map<String,String> getNewTicketFieldsMap(Message message)
    {
        //ExperimentProfileType submissionType = ExperimentProfileType.ONE_COLOR_MICROARRAY;
        Submission submission = message.getSubmission();
        submission = HibernateEntity.deproxy(submission, Submission.class);
        /*if (submission instanceof ExperimentSubmission) {
            try {
                ExperimentProfile exp = ((ExperimentSubmission) submission).getExperimentProfile();
                submissionType = exp.getType();
            } catch (DataSerializationException x) {
                logger.error("Error creating new ticket fields map", x);
            }
        }*/

        return new ImmutableMap.Builder<String, String>()
                .put(RtFieldNames.REQUESTOR, message.getUser().getEmail())
                .put(RtFieldNames.SUBJECT, message.getSubject())
                .put(RtFieldNames.TEXT, message.getBody().replaceAll("\\n","\n "))
                .put(RtFieldNames.QUEUE, properties.getRtQueueName())
                .put(RtFieldNames.SUBMISSION_ID,String.valueOf(submission.getId()))
                .build();
    }

    private Map<String,String> getTicketUpdateFieldsMap(Message message, Submission submission, ExperimentProfileType submissionType)
    {
        if(submissionType != null)
            return new ImmutableMap.Builder<String, String>()
                    .put(RtFieldNames.DIRECTORY, properties.getSubsTrackingExperimentType() + "_" + submission.getSubsTrackingId())
                    .put(RtFieldNames.SUBJECT, message.getSubject())
                    .put(RtFieldNames.EXPERIMENT_TYPE, submissionType.isSequencing() ? "HTS" : "MA")
                    .build();
        else
            return new ImmutableMap.Builder<String, String>()
                    .put(RtFieldNames.DIRECTORY, properties.getSubsTrackingExperimentType() + "_" + submission.getSubsTrackingId())
                    .put(RtFieldNames.SUBJECT, message.getSubject())
                    .build();

    }

    private Map<String,String> getFieldsMap(Message message)
    {
        String[] str = message.getTo().split("<");
        boolean isCurator = (str[0].replaceAll("\"","").equalsIgnoreCase("Annotare RT ")) || (str[0].replaceAll("\"","").equalsIgnoreCase("Annotare Admin "));

        return new ImmutableMap.Builder<String, String>()
                .put(RtFieldNames.ACTION, isCurator ? "comment" : "correspond")
                .put(RtFieldNames.SUBJECT, message.getSubject())
                .put(RtFieldNames.TEXT, message.getBody().replaceAll("\\n","\n "))
                .build();
    }

    private void sendRtMessage(String ticketNumber, Message message) throws Exception {
        if (StringUtils.isBlank(ticketNumber)) {
            logger.debug("Rt ticket is null for message: "+ message.getId());
            throw new Exception("Rt ticket is null");
        }

        List<BasicNameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("user",properties.getRtIntegrationUser()));
        params.add(new BasicNameValuePair("pass",properties.getRtIntegrationPassword()));
        params.add(new BasicNameValuePair("content",getMessageContent(getFieldsMap(message))));

        ExperimentProfileType submissionType = ExperimentProfileType.ONE_COLOR_MICROARRAY;
        Submission submission = message.getSubmission();
        submission = HibernateEntity.deproxy(submission, Submission.class);

        if (submission instanceof ExperimentSubmission) {
            try {
                ExperimentProfile exp = ((ExperimentSubmission) submission).getExperimentProfile();
                if(exp != null)
                    submissionType = exp.getType();
                else
                    submissionType = null;
            } catch (DataSerializationException x) {
                logger.error("Error sending RT message", x);
            }
        }

        logger.debug("Rt ticket number is "+ ticketNumber);
        logger.debug("Adding message to  Rt ticket "+ submission.getRtTicketNumber());

        if(null != submission.getSubsTrackingId() && !message.getSubject().equalsIgnoreCase(RtFieldNames.CONTACT_US_SUBJECT)) {
            try {
                ticketUpdate(getTicketUpdateFieldsMap(message,submission,submissionType), ticketNumber);
            } catch (Exception x) {
                messenger.send("There was a problem updating Submission Directory " + submission.getRtTicketNumber(), x);
            }
        }
        else
        {
            try {
                    ticketUpdate(
                            new ImmutableMap.Builder<String, String>()
                                    .put(RtFieldNames.SUBJECT, message.getSubject())
                                    .put(RtFieldNames.STATUS, "open")
                                    .build(),
                            ticketNumber
                    );
                } catch (Exception x) {
                    messenger.send("There was a problem updating ticket subject " + submission.getRtTicketNumber(), x);
                }
        }

        boolean messageSent = false;
        String errorTrace = "";
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                builder.build());
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
                sslConnectionSocketFactory).setRedirectStrategy(new LaxRedirectStrategy()).build();

        /*MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                .addTextBody("user", properties.getRtIntegrationUser())
                .addTextBody("pass", properties.getRtIntegrationPassword())
                .addTextBody("content", getMessageContent(getFieldsMap(message)));*/

        HttpPost httppost = new HttpPost(properties.getRtIntegrationUrl() + "ticket/"+ticketNumber+"/comment");
        httppost.setEntity(new UrlEncodedFormEntity(params,StandardCharsets.UTF_8));
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity r = response.getEntity();
        BufferedReader inp = new BufferedReader(new InputStreamReader(r.getContent()));
        String line;
        try
        {
            Pattern p = Pattern.compile("RT/4.2.12 200 Ok");
            while ((line = inp.readLine()) != null) {
                logger.debug(line);
                Matcher m = p.matcher(line);
                if (m.find()) {
                    messageSent = true;
                }
            }
            if (!messageSent) {
                throw new Exception("RT was unable to send Message");
            }
        }catch (Exception e)
        {
            logger.error("Error sending RT message",e);
            while ((line = inp.readLine()) != null) {
                logger.error(line);
                errorTrace = errorTrace + line + "\n";
            }
        }

        //httpclient.close();
    }

}
