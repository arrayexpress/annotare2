/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.server.services.AccessControlException;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;

/**
 * @author Olga Melnichuk
 */
public class SubmissionServiceImpl extends RemoteServiceBase implements SubmissionService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionServiceImpl.class);

    @Inject
    private SubmissionManager submissionManager;

    public SubmissionDetails getSubmission(int id) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission sb = submissionManager.getSubmission(getCurrentUser(), id);

            return new SubmissionDetails(
                    sb.getId(),
                    sb.getTitle(),
                    sb.getDescription(),
                    sb.getCreated()
            );
        } catch (RecordNotFoundException e) {
            log.warn("getSubmission(" + id + ") failure", e);
            throw new ResourceNotFoundException("Submission with id=" + id + "doesn't exist");
        } catch (AccessControlException e) {
            log.warn("getSubmission(" + id + ") failure", e);
            throw new NoPermissionException("Sorry, you do not have access to this resource");
        }
    }
}
