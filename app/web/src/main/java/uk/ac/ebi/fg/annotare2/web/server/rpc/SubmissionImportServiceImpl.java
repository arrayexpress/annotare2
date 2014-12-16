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

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionFeedbackDao;
import uk.ac.ebi.fg.annotare2.db.dao.UserDao;
import uk.ac.ebi.fg.annotare2.submission.model.ImportedExperimentProfile;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionImportService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;
import uk.ac.ebi.fg.annotare2.web.server.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.web.server.services.*;
import uk.ac.ebi.fg.annotare2.web.server.transaction.Transactional;

public class SubmissionImportServiceImpl extends SubmissionBasedRemoteService implements SubmissionImportService {

    private static final long serialVersionUID = 23916206047841933L;

    private static final Logger log = LoggerFactory.getLogger(SubmissionServiceImpl.class);

    private final DataFileManager dataFileManager;
    private final AnnotareProperties properties;
    private final UserDao userDao;
    private final SubmissionFeedbackDao feedbackDao;
    private final EfoSearch efoSearch;
    private final EmailSender email;

    @Inject
    public SubmissionImportServiceImpl(AccountService accountService,
                                 SubmissionManager submissionManager,
                                 DataFileManager dataFileManager,
                                 AnnotareProperties properties,
                                 UserDao userDao,
                                 SubmissionFeedbackDao feedbackDao,
                                 EfoSearch efoSearch,
                                 EmailSender emailSender) {
        super(accountService, submissionManager, emailSender);
        this.dataFileManager = dataFileManager;
        this.properties = properties;
        this.userDao = userDao;
        this.feedbackDao = feedbackDao;
        this.efoSearch = efoSearch;
        this.email = emailSender;
    }

    @Transactional(rollbackOn = NoPermissionException.class)
    @Override
    public long createImportedExperiment() throws NoPermissionException {
        try {
            return createImportedExperimentSubmission().getId();
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
    }

    @Override
    public ImportedExperimentProfile getExperimentProfile(long id)
            throws ResourceNotFoundException, NoPermissionException  {

        return null;
    }

    @Override
    public void updateExperimentProfile(long id, ImportedExperimentProfile profile)
            throws ResourceNotFoundException, NoPermissionException {

    }

    @Override
    public ValidationResult validateSubmission(long id)
            throws ResourceNotFoundException, NoPermissionException {

        return null;
    }

    @Override
    public void submitSubmission(long id)
            throws ResourceNotFoundException, NoPermissionException {

    }

    @Override
    public void deleteSubmission(long id)
            throws ResourceNotFoundException, NoPermissionException {

    }

    @Override
    public void postFeedback(long id, Byte score, String comment)
            throws ResourceNotFoundException, NoPermissionException {

    }
}
