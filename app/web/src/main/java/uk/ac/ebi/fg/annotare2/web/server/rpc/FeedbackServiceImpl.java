/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.db.model.User;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.FeedbackService;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountService;
import uk.ac.ebi.fg.annotare2.web.server.services.EmailSender;

import javax.mail.MessagingException;

public class FeedbackServiceImpl extends AuthBasedRemoteService implements FeedbackService {

    private final EmailSender email;

    @Inject
    public FeedbackServiceImpl(AccountService accountService, EmailSender emailSender) {
        super(accountService, emailSender);
        this.email = emailSender;
    }

    public void provideGeneralFeedback(String message) {
        User u = getCurrentUser();
        try {
            email.sendFromTemplate(EmailSender.FEEDBACK_TEMPLATE,
                    ImmutableMap.of(
                            "from.name", u.getName(),
                            "from.email", u.getEmail(),
                            "feedback.message", message
                    ));
        } catch (MessagingException x) {
            //
        }
    }
}
