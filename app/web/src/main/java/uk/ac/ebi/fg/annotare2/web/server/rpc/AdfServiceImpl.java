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

import com.google.gwt.user.server.rpc.UnexpectedException;
import com.google.inject.Inject;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.magetab.rowbased.AdfParser;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import uk.ac.ebi.fg.annotare2.magetab.table.TsvGenerator;
import uk.ac.ebi.fg.annotare2.magetab.table.TsvParser;
import uk.ac.ebi.fg.annotare2.db.om.ArrayDesignSubmission;
import uk.ac.ebi.fg.annotare2.db.om.enums.Permission;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AdfService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataImportException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.server.TransactionCallback;
import uk.ac.ebi.fg.annotare2.web.server.TransactionSupport;
import uk.ac.ebi.fg.annotare2.web.server.TransactionWrapException;
import uk.ac.ebi.fg.annotare2.web.server.login.AuthService;
import uk.ac.ebi.fg.annotare2.web.server.services.AccessControlException;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;
import uk.ac.ebi.fg.annotare2.web.server.services.UploadedFiles;

import java.io.IOException;

/**
 * @author Olga Melnichuk
 */
public class AdfServiceImpl extends AuthBasedRemoteService implements AdfService {

    private static final Logger log = LoggerFactory.getLogger(AdfServiceImpl.class);


    private SubmissionManager submissionManager;
    private TransactionSupport transactionSupport;

    @Inject
    public AdfServiceImpl(AuthService authService, SubmissionManager submissionManager,
                          TransactionSupport transactionSupport) {
        super(authService);
        this.submissionManager = submissionManager;
        this.transactionSupport = transactionSupport;
    }

    @Override
    public Table loadBodyData(int submissionId) throws NoPermissionException, ResourceNotFoundException {
        try {
            ArrayDesignSubmission submission = submissionManager.getArrayDesignSubmission(getCurrentUser(), submissionId, Permission.VIEW);
            return new TsvParser().parse(submission.getBody());
        } catch (IOException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    @Override
    public void importBodyData(int submissionId) throws NoPermissionException, ResourceNotFoundException, DataImportException {
        try {
            final ArrayDesignSubmission submission = submissionManager.getArrayDesignSubmission(getCurrentUser(), submissionId, Permission.UPDATE);

            FileItem item = UploadedFiles.getFirst(getSession());
            Table table = new AdfParser().parseBody(item.getInputStream());

            //TODO validation here
            if (table.isEmpty()) {
                throw new DataImportException("Can't import an empty file.");
            }
            if (table.getWidth() <= 1 || table.getHeight() <= 1) {
                throw new DataImportException("The file contents don't look like a valid ADF data.");
            }
            final String body = new TsvGenerator(table).generateString();

            transactionSupport.execute(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction() throws Exception {
                    submission.setBody(body);
                    submissionManager.save(submission);
                    return null;
                }
            });
        } catch (IOException e) {
            log.warn("Can't import ADF body data (submissionId: " + submissionId + ")", e);
            throw new DataImportException(e.getMessage());
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (TransactionWrapException e) {
           throw unexpected(e.getCause());
        }
    }

    private static UnexpectedException unexpected(Throwable e) {
        log.error("server error", e);
        return new UnexpectedException("Unexpected server error", e);
    }

    private static ResourceNotFoundException noSuchRecord(RecordNotFoundException e) {
        log.error("server error", e);
        return new ResourceNotFoundException("Submission not found");
    }

    private static NoPermissionException noPermission(AccessControlException e) {
        log.error("server error", e);
        return new NoPermissionException("No permission");
    }
}
