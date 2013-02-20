/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import uk.ac.ebi.fg.annotare2.magetab.table.TsvParser;
import uk.ac.ebi.fg.annotare2.om.ArrayDesignSubmission;
import uk.ac.ebi.fg.annotare2.om.Permission;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AdfService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.server.login.AuthService;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;

import java.io.IOException;

/**
 * @author Olga Melnichuk
 */
public class AdfServiceImpl extends SubmissionBasedRemoteService implements AdfService {

    private static final Logger log = LoggerFactory.getLogger(AdfServiceImpl.class);

    @Inject
    public AdfServiceImpl(AuthService authService, SubmissionManager submissionManager) {
        super(authService, submissionManager);
    }

    @Override
    public Table loadData(int submissionId) throws NoPermissionException, ResourceNotFoundException {
        try {
            ArrayDesignSubmission submission = getArrayDesignSubmission(submissionId, Permission.VIEW);
            return new TsvParser().parse(submission.getBody());
        } catch (IOException e) {
            log.error("Can't parser IDF general info for submissionId=" + submissionId, e);
        }
        return null;
    }
}
