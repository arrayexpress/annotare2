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

import com.google.gwt.user.server.rpc.UnexpectedException;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.renderer.IDFWriter;
import uk.ac.ebi.arrayexpress2.magetab.renderer.SDRFWriter;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentConfig;
import uk.ac.ebi.fg.annotare2.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.magetab.integration.MageTabGenerator;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import uk.ac.ebi.fg.annotare2.magetab.table.TsvParser;
import uk.ac.ebi.fg.annotare2.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.om.enums.Permission;
import uk.ac.ebi.fg.annotare2.submissionmodel.DataSerializationException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.*;
import uk.ac.ebi.fg.annotare2.web.server.login.AuthService;
import uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter;
import uk.ac.ebi.fg.annotare2.web.server.services.AccessControlException;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static com.google.common.io.Closeables.close;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.ExperimentFactory.createExperiment;

/**
 * @author Olga Melnichuk
 */
public class SubmissionServiceImpl extends AuthBasedRemoteService implements SubmissionService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionServiceImpl.class);

    private final SubmissionManager submissionManager;

    @Inject
    public SubmissionServiceImpl(AuthService authService, SubmissionManager submissionManager) {
        super(authService);
        this.submissionManager = submissionManager;
    }

    @Override
    public SubmissionDetails getSubmission(int id) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission sb = submissionManager.getSubmission(getCurrentUser(), id, Permission.VIEW);
            return UIObjectConverter.uiSubmissionDetails(sb);
        } catch (AccessControlException e) {
            log.warn("getSubmission(" + id + ") failure", e);
            throw new NoPermissionException("Sorry, you do not have access to this resource");
        } catch (RecordNotFoundException e) {
            log.warn("getSubmission(" + id + ") failure", e);
            throw new ResourceNotFoundException("Submission with id=" + id + " doesn't exist");
        }
    }

    @Override
    public ExperimentSettings getExperimentSettings(int id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission sb = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.VIEW);
            return UIObjectConverter.uiExperimentSubmissionSettings(sb);
        } catch (AccessControlException e) {
            log.warn("getExperimentSettings(" + id + ") failure", e);
            throw new NoPermissionException("Sorry, you do not have access to this resource");
        } catch (RecordNotFoundException e) {
            log.warn("getExperimentSettings(" + id + ") failure", e);
            throw new ResourceNotFoundException("Submission with id=" + id + " doesn't exist");
        } catch (DataSerializationException e) {
            log.error("getExperimentSettings(" + id + ") failure", e);
            throw new UnexpectedException("extract experiment settings failure", e);
        }
    }

    @Override
    public DetailsDto getExperimentDetails(int id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission sb = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.VIEW);
            return UIObjectConverter.uiExperimentDetails(sb);
        } catch (AccessControlException e) {
            log.warn("getExperimentSettings(" + id + ") failure", e);
            throw new NoPermissionException("Sorry, you do not have access to this resource");
        } catch (RecordNotFoundException e) {
            log.warn("getExperimentSettings(" + id + ") failure", e);
            throw new ResourceNotFoundException("Submission with id=" + id + " doesn't exist");
        } catch (DataSerializationException e) {
            log.error("getExperimentSettings(" + id + ") failure", e);
            throw new UnexpectedException("extract experiment settings failure", e);
        }
    }

    @Override
    public List<ContactDto> getContacts(int id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission sb = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.VIEW);
            return UIObjectConverter.uiContacts(sb);
        } catch (AccessControlException e) {
            log.warn("getContacts(" + id + ") failure", e);
            throw new NoPermissionException("Sorry, you do not have access to this resource");
        } catch (RecordNotFoundException e) {
            log.warn("getContacts(" + id + ") failure", e);
            throw new ResourceNotFoundException("Submission with id=" + id + " doesn't exist");
        } catch (DataSerializationException e) {
            log.error("getContacts(" + id + ") failure", e);
            throw new UnexpectedException("get experiment contacts failure", e);
        }
    }

    @Override
    public List<PublicationDto> getPublications(int id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission sb = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.VIEW);
            return UIObjectConverter.uiPublications(sb);
        } catch (AccessControlException e) {
            log.warn("getPublications(" + id + ") failure", e);
            throw new NoPermissionException("Sorry, you do not have access to this resource");
        } catch (RecordNotFoundException e) {
            log.warn("getPublications(" + id + ") failure", e);
            throw new ResourceNotFoundException("Submission with id=" + id + " doesn't exist");
        } catch (DataSerializationException e) {
            log.error("getPublications(" + id + ") failure", e);
            throw new UnexpectedException("get experiment publications failure", e);
        }
    }

    @Override
    public List<SampleRow> getSamples(int id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission sb = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.VIEW);
            return UIObjectConverter.uiSampleRows(sb);
        } catch (AccessControlException e) {
            log.warn("getSamples(" + id + ") failure", e);
            throw new NoPermissionException("Sorry, you do not have access to this resource");
        } catch (RecordNotFoundException e) {
            log.warn("getSamples(" + id + ") failure", e);
            throw new ResourceNotFoundException("Submission with id=" + id + " doesn't exist");
        } catch (DataSerializationException e) {
            log.error("getSamples(" + id + ") failure", e);
            throw new UnexpectedException("get experiment samples failure", e);
        }
    }


    @Override
    public Table getIdfTable(int id) throws NoPermissionException, ResourceNotFoundException {
        try {
            ExperimentSubmission submission = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.VIEW);
            ExperimentConfig exp = submission.getExperimentConfig();
            MAGETABInvestigation inv = new MageTabGenerator(exp).generate();
            return asTable(inv.IDF);
        } catch (AccessControlException e) {
            log.warn("getIdfTable(" + id + ") failure", e);
            throw new NoPermissionException("Sorry, you do not have access to this resource");
        } catch (RecordNotFoundException e) {
            log.warn("getIdfTable(" + id + ") failure", e);
            throw new ResourceNotFoundException("Submission with id=" + id + " doesn't exist");
        } catch (IOException e) {
            log.error("getIdfTable(" + id + ") failure", e);
            throw new UnexpectedException("IDF generate failure", e);
        } catch (DataSerializationException e) {
            log.error("getIdfTable(" + id + ") failure", e);
            throw new UnexpectedException("IDF generate failure", e);
        } catch (ParseException e) {
            log.error("getIdfTable(" + id + ") failure", e);
            throw new UnexpectedException("IDF generate failure", e);
        }
    }

    @Override
    public Table getSdrfTable(int id) throws NoPermissionException, ResourceNotFoundException {
        try {
            ExperimentSubmission submission = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.VIEW);
            ExperimentConfig exp = submission.getExperimentConfig();
            MAGETABInvestigation inv = (new MageTabGenerator(exp)).generate();
            return asTable(inv.SDRF);
        } catch (AccessControlException e) {
            log.warn("getSdrfTable(" + id + ") failure", e);
            throw new NoPermissionException("Sorry, you do not have access to this resource");
        } catch (RecordNotFoundException e) {
            log.warn("getSdrfTable(" + id + ") failure", e);
            throw new ResourceNotFoundException("Submission with id=" + id + " doesn't exist");
        } catch (IOException e) {
            log.error("getSdrfTable(" + id + ") failure", e);
            throw new UnexpectedException("SDRF generate failure", e);
        } catch (DataSerializationException e) {
            log.error("getSDRFTable(" + id + ") failure", e);
            throw new UnexpectedException("SDRF generate failure", e);
        } catch (ParseException e) {
            log.error("getSDRFTable(" + id + ") failure", e);
            throw new UnexpectedException("SDRF generate failure", e);
        }
    }

    private Table asTable(IDF idf) throws IOException {
        File tmpFile = File.createTempFile("idf", "tmp");
        IDFWriter writer = null;
        try {
            writer = new IDFWriter(new FileWriter(tmpFile));
            writer.write(idf);
            return new TsvParser().parse(new FileInputStream(tmpFile));
        } finally {
            close(writer, true);
            //TODO delete temporary file ?
        }
    }

    private Table asTable(SDRF sdrf) throws IOException {
        File tmpFile = File.createTempFile("sdrf", "tmp");
        SDRFWriter writer = null;
        try {
            writer = new SDRFWriter(new FileWriter(tmpFile));
            writer.write(sdrf);
            return new TsvParser().parse(new FileInputStream(tmpFile));
        } finally {
            close(writer, true);
            //TODO delete temporary file ?
        }
    }

    @Override
    public int createExperimentSubmission() throws NoPermissionException {
        try {
            return submissionManager.createExperimentSubmission(getCurrentUser()).getId();
        } catch (AccessControlException e) {
            log.warn("createSubmission() failure", e);
            throw new NoPermissionException("no permission to create a submission");
        }
    }

    @Override
    public int createArrayDesignSubmission() throws NoPermissionException {
        try {
            return submissionManager.createArrayDesignSubmission(getCurrentUser()).getId();
        } catch (AccessControlException e) {
            log.warn("createSubmission() failure", e);
            throw new NoPermissionException("no permission to create a submission");
        }
    }

    @Override
    public void setupExperimentSubmission(int id, ExperimentSetupSettings settings) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission =
                    submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.UPDATE);
            submission.setExperimentConfig(createExperiment(settings));
        } catch (RecordNotFoundException e) {
            log.warn("setupExperimentSubmission(" + id + ") failure", e);
            throw new ResourceNotFoundException("Submission with id=" + id + " doesn't exist");
        } catch (AccessControlException e) {
            log.warn("setupExperimentSubmission(" + id + ") failure", e);
            throw new NoPermissionException("no permission to update submission: " + id);
        } catch (DataSerializationException e) {
            log.error("setupExperimentSubmission(" + id + ") failure", e);
            throw new UnexpectedException("experiment setup failed", e);
        }
    }

    @Override
    public void discardSubmissionData(int id) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission =
                    submissionManager.getSubmission(getCurrentUser(), id, Permission.UPDATE);
            submission.discardAll();
        } catch (RecordNotFoundException e) {
            log.warn("setupExperimentSubmission(" + id + ") failure", e);
            throw new ResourceNotFoundException("Submission with id=" + id + " doesn't exist");
        } catch (AccessControlException e) {
            log.warn("setupExperimentSubmission(" + id + ") failure", e);
            throw new NoPermissionException("no permission to update submission: " + id);
        }
    }

    @Override
    public UpdateResult updateExperiment(int id, List<UpdateCommand> commands) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.UPDATE);
            ExperimentConfig experiment = submission.getExperimentConfig();
            UpdateResult result = new ExperimentUpdatePerformer(experiment).run(commands);
            submission.setExperimentConfig(experiment);
            return result;
        } catch (RecordNotFoundException e) {
            log.warn("updateExperiment(" + id + ") failure", e);
            throw new ResourceNotFoundException("Submission with id=" + id + " doesn't exist");
        } catch (AccessControlException e) {
            log.warn("updateExperiment(" + id + ") failure", e);
            throw new NoPermissionException("no permission to update submission: " + id);
        } catch (DataSerializationException e) {
            log.warn("updateExperiment(" + id + ") failure", e);
            throw new UnexpectedException("update experiment failure", e);
        }
    }

}
