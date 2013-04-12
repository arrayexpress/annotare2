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
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import uk.ac.ebi.fg.annotare2.magetab.table.TsvGenerator;
import uk.ac.ebi.fg.annotare2.magetab.table.TsvParser;
import uk.ac.ebi.fg.annotare2.magetab.table.operation.Operation;
import uk.ac.ebi.fg.annotare2.magetab.rowbased.IdfParser;
import uk.ac.ebi.fg.annotare2.magetab.rowbased.Investigation;
import uk.ac.ebi.fg.annotare2.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.om.enums.Permission;
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

    private static final Logger log = LoggerFactory.getLogger(IdfServiceImpl.class);

    @Inject
    public IdfServiceImpl(AuthService authService, SubmissionManager submissionManager) {
        super(authService, submissionManager);
    }

    @Override
    public UIGeneralInfo getGeneralInfo(int submissionId) throws NoPermissionException, ResourceNotFoundException {
        try {
            return parseGeneralInfo(getExperimentSubmission(submissionId, Permission.VIEW).getInvestigation());
        } catch (IOException e) {
            log.error("Can't parse IDF general info section (submissionId: " + submissionId + ")", e);
        }
        return null;
    }

    @Override
    public Table loadInvestigation(int submissionId) throws ResourceNotFoundException, NoPermissionException {
        try {
            return new TsvParser().parse(getExperimentSubmission(submissionId, Permission.VIEW).getInvestigation());
        } catch (IOException e) {
            log.error("Can't load IDF table (submissionId: " + submissionId + ")", e);
        }
        return null;
    }

    @Override
    public void updateInvestigation(int submissionId, Operation operation) throws NoPermissionException, ResourceNotFoundException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(submissionId, Permission.UPDATE);
            Table table = new TsvParser().parse(submission.getInvestigation());
            operation.apply(table);
            Investigation inv = new Investigation(table);
            submission.setInvestigation(new TsvGenerator(table).generateString());
            submission.setTitle(inv.getTitle().getValue());
        } catch (IOException e) {
            log.error("Can't update IDF (submissionId: " + submissionId + ")", e);
        }
    }

    @Override
    public void importInvestigation(int submissionId) throws NoPermissionException,
            ResourceNotFoundException, DataImportException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(submissionId, Permission.UPDATE);

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
            log.warn("Can't import investigation (submissionId: " + submissionId + ")", e);
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
