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

import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

public class OtrsMessengerService extends EmailMessengerService {

    //private static final Logger log = LoggerFactory.getLogger(OtrsMessengerService.class);

    private final ExtendedAnnotareProperties properties;
    //private final HibernateSessionFactory sessionFactory;
    private final Messenger messenger;
    private final SubmissionDao submissionDao;
    private final OtrsMessageParser messageParser;

    @Inject
    public OtrsMessengerService(HibernateSessionFactory sessionFactory,
                                ExtendedAnnotareProperties properties,
                                Messenger messenger,
                                MessageDao messageDao,
                                SubmissionDao submissionDao) {
        super(properties);
        //this.sessionFactory = sessionFactory;
        this.messenger = messenger;
        this.properties = properties;
        this.submissionDao = submissionDao;
        messageParser = new OtrsMessageParser();
    }

    @Override
    protected void sendMessage(Message message) throws Exception {
        if (properties.isOtrsIntegrationEnabled() && null != message.getSubmission()) {
            try {
                Submission submission = message.getSubmission();
                String ticketNumber = submission.getOtrsTicketNumber();
                boolean isNewTicket = false;
                if (isNullOrEmpty(ticketNumber)) {
                    isNewTicket = true;
                    ticketNumber = createOtrsTicket(submission);
                    submission.setOtrsTicketNumber(ticketNumber);
                    submissionDao.save(submission);
                }

                sendOtrsMessage(ticketNumber, message, isNewTicket);
            } catch (Throwable x){
                messenger.send("There was a problem sending message " + String.valueOf(message.getId()), x);
                throw x;
            }
        } else {
            super.sendMessage(message);
        }
    }

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
    }
}
