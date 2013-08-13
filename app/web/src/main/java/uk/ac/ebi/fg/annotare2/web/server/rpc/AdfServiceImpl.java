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
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.magetab.rowbased.AdfParser;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import uk.ac.ebi.fg.annotare2.magetab.table.TsvGenerator;
import uk.ac.ebi.fg.annotare2.magetab.table.TsvParser;
import uk.ac.ebi.fg.annotare2.om.ArrayDesignSubmission;
import uk.ac.ebi.fg.annotare2.om.enums.Permission;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AdfService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataImportException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.server.login.AuthService;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;
import uk.ac.ebi.fg.annotare2.web.server.services.UploadedFiles;

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
    public Table loadBodyData(int submissionId) throws NoPermissionException, ResourceNotFoundException {
        try {
            ArrayDesignSubmission submission = getArrayDesignSubmission(submissionId, Permission.VIEW);
            return new TsvParser().parse(submission.getBody());
        } catch (IOException e) {
            log.error("Can't load ADF data (submissionId: " + submissionId + ")", e);
        }
        return null;
    }

    @Override
    public void importBodyData(int submissionId) throws NoPermissionException, ResourceNotFoundException, DataImportException {
        try {
            ArrayDesignSubmission submission = getArrayDesignSubmission(submissionId, Permission.UPDATE);

            FileItem item = UploadedFiles.getFirst(getSession());
            Table table = new AdfParser().parseBody(item.getInputStream());

            //TODO validation here
            if (table.isEmpty()) {
                throw new DataImportException("Can't import an empty file.");
            }
            if (table.getWidth() <= 1 || table.getHeight() <= 1) {
                throw new DataImportException("The file contents don't look like a valid SDRF data.");
            }
            submission.setBody(new TsvGenerator(table).generateString());
        } catch (IOException e) {
            log.warn("Can't import ADF body data (submissionId: " + submissionId + ")", e);
            throw new DataImportException(e.getMessage());
        }
    }
}
