package uk.ac.ebi.fg.annotare2.web.server.rpc;

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


import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.core.AccessControlException;
import uk.ac.ebi.fg.annotare2.core.components.Messenger;
import uk.ac.ebi.fg.annotare2.core.transaction.Transactional;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.integration.EmailTemplates;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionCreateService;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountService;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManagerImpl;

public class SubmissionCreateServiceImpl extends SubmissionBasedRemoteService implements SubmissionCreateService {

    private final Messenger messenger;

    @Inject
    public SubmissionCreateServiceImpl(
            AccountService accountService,
            SubmissionManagerImpl submissionManager,
            Messenger messenger) {
        super(accountService, submissionManager, messenger);
        this.messenger = messenger;
    }

    @Transactional(rollbackOn = NoPermissionException.class)
    @Override
    public long createExperiment() throws NoPermissionException {
        try {
            ExperimentSubmission submission = createExperimentSubmission();

            //create ticket in RT
            messenger.send(
                    EmailTemplates.NEW_SUBMISSION_TEMPLATE,
                    new ImmutableMap.Builder<String, String>()
                            .put("to.name", submission.getCreatedBy().getName())
                            .put("to.email", submission.getCreatedBy().getEmail())
                            .put("submission.id", String.valueOf(submission.getId()))
                            .build()
                    , submission.getCreatedBy()
                    , submission
            );

            return submission.getId();
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
    }

    //@Transactional(rollbackOn = NoPermissionException.class)
    //@Override
    //public long createArrayDesign() throws NoPermissionException {
    //    try {
    //        return createArrayDesignSubmission().getId();
    //    } catch (AccessControlException e) {
    //        throw noPermission(e);
    //    }
    //}
}