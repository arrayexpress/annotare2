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
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.magetab.base.Table;
import uk.ac.ebi.fg.annotare2.magetab.base.TsvGenerator;
import uk.ac.ebi.fg.annotare2.magetab.base.TsvParser;
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataImportException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SdrfService;
import uk.ac.ebi.fg.annotare2.web.server.services.AccessControlException;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;
import uk.ac.ebi.fg.annotare2.web.server.services.UploadedFiles;

import java.io.IOException;

/**
 * @author Olga Melnichuk
 */
public class SdrfServiceImpl extends RemoteServiceBase implements SdrfService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionServiceImpl.class);

    @Inject
    private SubmissionManager submissionManager;

    @Override
    public Table loadData(int submissionId) throws NoPermissionException, ResourceNotFoundException {
        try {
            Submission submission = submissionManager.getSubmission(getCurrentUser(), submissionId);
            return new TsvParser().parse(submission.getSampleAndDataRelationship());
        } catch (RecordNotFoundException e) {
            log.warn("getGeneralInfo(" + submissionId + ") failure", e);
            throw new ResourceNotFoundException("Submission with id=" + submissionId + "doesn't exist");
        } catch (AccessControlException e) {
            log.warn("getGeneralInfo(" + submissionId + ") failure", e);
            throw new NoPermissionException("Sorry, you do not have access to this resource");
        } catch (IOException e) {
            log.error("Can't parser IDF general info for submissionId=" + submissionId, e);
        }
        return null;
    }

    @Override
    public void importData(int submissionId) throws NoPermissionException, ResourceNotFoundException, DataImportException {
        try {
            Submission submission = submissionManager.getSubmission2Update(getCurrentUser(), submissionId);

            FileItem item = UploadedFiles.getOne(getSession());
            Table table = new TsvParser().parse(item.getInputStream());

            //TODO add more clever SDRF content validation here
            if (table.isEmpty()) {
                throw new DataImportException("Can't import an empty file.");
            }
            if (table.getWidth() <= 1 || table.getHeight() <= 1) {
                throw new DataImportException("The file contents don't look like a valid SDRF data.");
            }
            submission.setSampleAndDataRelationship(new TsvGenerator(table).generateString());
        } catch (RecordNotFoundException e) {
            log.warn("importInvestigation(" + submissionId + " failure", e);
            throw new ResourceNotFoundException("Submission with id=" + submissionId + "doesn't exist");
        } catch (AccessControlException e) {
            log.warn("importInvestigation(" + submissionId + ") failure", e);
            throw new NoPermissionException("Sorry, you do not have access to this resource");
        } catch (IOException e) {
            log.warn("importInvestigation(" + submissionId + ") failure", e);
            throw new DataImportException(e.getMessage());
        }
    }
}
