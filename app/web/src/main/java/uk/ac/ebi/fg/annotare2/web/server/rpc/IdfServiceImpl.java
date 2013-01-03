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
import uk.ac.ebi.fg.annotare2.magetab.base.Table;
import uk.ac.ebi.fg.annotare2.magetab.base.TsvGenerator;
import uk.ac.ebi.fg.annotare2.magetab.base.TsvParser;
import uk.ac.ebi.fg.annotare2.magetab.base.operation.Operation;
import uk.ac.ebi.fg.annotare2.magetab.idf.IdfParser;
import uk.ac.ebi.fg.annotare2.magetab.idf.Investigation;
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataImportException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.IdfService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UIGeneralInfo;
import uk.ac.ebi.fg.annotare2.web.server.login.AuthService;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;
import uk.ac.ebi.fg.annotare2.web.server.services.UploadedFiles;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Olga Melnichuk
 */
public class IdfServiceImpl extends SubmissionBasedRemoteService implements IdfService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionServiceImpl.class);

    @Inject
    public IdfServiceImpl(AuthService authService, SubmissionManager submissionManager) {
        super(authService, submissionManager);
    }

    @Override
    public UIGeneralInfo getGeneralInfo(int submissionId) throws NoPermissionException, ResourceNotFoundException {
        try {
            return parseGeneralInfo(getMySubmission(submissionId).getInvestigation());
        } catch (IOException e) {
            log.error("Can't parser IDF general info for submissionId=" + submissionId, e);
        }
        return null;
    }

    @Override
    public Table loadInvestigation(int submissionId) throws ResourceNotFoundException, NoPermissionException {
        try {
            return new TsvParser().parse(getMySubmission(submissionId).getInvestigation());
        } catch (IOException e) {
            log.error("Can't parser IDF general info for submissionId=" + submissionId, e);
        }
        return null;
    }

    @Override
    public void updateInvestigation(int submissionId, Operation operation) throws NoPermissionException, ResourceNotFoundException {
        try {
            Submission submission = getMySubmission2Update(submissionId);
            Table table = new TsvParser().parse(submission.getInvestigation());
            operation.apply(table);
            submission.setInvestigation(new TsvGenerator(table).generateString());
        } catch (IOException e) {
            log.error("Can't parser IDF general info for submissionId=" + submissionId, e);
        }
    }

    @Override
    public void importInvestigation(int submissionId) throws NoPermissionException,
            ResourceNotFoundException, DataImportException {
        try {
            Submission submission = getMySubmission2Update(submissionId);

            FileItem item = UploadedFiles.getOne(getSession());
            Table table = new TsvParser().parse(item.getInputStream());

            //TODO add more clever IDF content validation here
            if (table.isEmpty()) {
                throw new DataImportException("Can't import an empty file.");
            }
            if (table.getWidth() <= 1 || table.getHeight() <= 1) {
                throw new DataImportException("The file contents don't look like a valid IDF data.");
            }
            submission.setInvestigation(new TsvGenerator(table).generateString());
        } catch (IOException e) {
            log.warn("importInvestigation(" + submissionId + ") failure", e);
            throw new DataImportException(e.getMessage());
        }
    }

    private UIGeneralInfo parseGeneralInfo(InputStream in) throws IOException {
        Investigation inv = IdfParser.parse(in);
        return new UIGeneralInfo(
                inv.getTitle().getValue(),
                inv.getDescription().getValue(),
                inv.getDateOfExperiment().getValue(),
                inv.getDateOfPublicRelease().getValue());
    }
}
