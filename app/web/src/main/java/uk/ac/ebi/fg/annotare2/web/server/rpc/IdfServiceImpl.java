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
import uk.ac.ebi.fg.annotare2.magetab.base.Operation;
import uk.ac.ebi.fg.annotare2.magetab.base.Table;
import uk.ac.ebi.fg.annotare2.magetab.base.TsvGenerator;
import uk.ac.ebi.fg.annotare2.magetab.base.TsvParser;
import uk.ac.ebi.fg.annotare2.magetab.idf.IdfParser;
import uk.ac.ebi.fg.annotare2.magetab.idf.Investigation;
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.IdfService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UIGeneralInfo;
import uk.ac.ebi.fg.annotare2.web.server.services.AccessControlException;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Charsets.UTF_8;

/**
 * @author Olga Melnichuk
 */
public class IdfServiceImpl extends RemoteServiceBase implements IdfService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionServiceImpl.class);

    @Inject
    private SubmissionManager submissionManager;

    @Override
    public UIGeneralInfo getGeneralInfo(int submissionId) throws NoPermissionException, ResourceNotFoundException {
        try {
            Submission submission = submissionManager.getSubmission(getCurrentUser(), submissionId);
            return parseGeneralInfo(submission.getInvestigation());
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
    public Table loadInvestigation(int submissionId) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = submissionManager.getSubmission(getCurrentUser(), submissionId);
            return new TsvParser().parse(submission.getInvestigation());
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
    public void updateInvestigation(int submissionId, List<Operation> operations) throws NoPermissionException, ResourceNotFoundException {
        try {
            Submission submission = submissionManager.getSubmission(getCurrentUser(), submissionId);
            Table table = new TsvParser().parse(submission.getInvestigation());
            table.applyChanges(operations);
            submission.setInvestigation(asString(table));
        } catch (RecordNotFoundException e) {
            log.warn("getGeneralInfo(" + submissionId + ") failure", e);
            throw new ResourceNotFoundException("Submission with id=" + submissionId + "doesn't exist");
        } catch (AccessControlException e) {
            log.warn("getGeneralInfo(" + submissionId + ") failure", e);
            throw new NoPermissionException("Sorry, you do not have access to this resource");
        } catch (IOException e) {
            log.error("Can't parser IDF general info for submissionId=" + submissionId, e);
        }
    }

    private UIGeneralInfo parseGeneralInfo(InputStream in) throws IOException {
        Investigation inv = IdfParser.parse(in);
        return new UIGeneralInfo(
                inv.getTitle().getValue(),
                inv.getDescription().getValue(),
                new Date(),
                new Date());  //TODO propagate proper values here
    }

    //TODO move this functionality in the proper place
    private String asString(Table table) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new TsvGenerator(table).generate(out);
        return out.toString(UTF_8.name());
    }
}
