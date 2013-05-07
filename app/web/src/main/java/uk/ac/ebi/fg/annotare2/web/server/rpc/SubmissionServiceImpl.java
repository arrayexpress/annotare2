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
import uk.ac.ebi.fg.annotare2.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.om.enums.Permission;
import uk.ac.ebi.fg.annotare2.submissionmodel.Contact;
import uk.ac.ebi.fg.annotare2.submissionmodel.DataSerializationException;
import uk.ac.ebi.fg.annotare2.submissionmodel.Experiment;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ContactsUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ContactsUpdateResult;
import uk.ac.ebi.fg.annotare2.web.server.login.AuthService;
import uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter;
import uk.ac.ebi.fg.annotare2.web.server.services.AccessControlException;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
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
    public ExperimentDetails getExperimentDetails(int id) throws ResourceNotFoundException, NoPermissionException {
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
            submission.setExperiment(createExperiment(settings));
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
    public ExperimentDetails saveExperimentDetails(int id, ExperimentDetails details) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission =
                    submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.UPDATE);
            Experiment exp = submission.getExperiment();
            exp.setTitle(details.getTitle());
            exp.setDescription(details.getDescription());
            exp.setPublicReleaseDate(details.getPublicReleaseDate());
            exp.setExperimentDate(details.getExperimentDate());
            submission.setExperiment(exp);
            submission.setTitle(details.getTitle());
            return details;
        } catch (RecordNotFoundException e) {
            log.warn("saveExperimentDetails(" + id + ") failure", e);
            throw new ResourceNotFoundException("Submission with id=" + id + " doesn't exist");
        } catch (AccessControlException e) {
            log.warn("saveExperimentDetails(" + id + ") failure", e);
            throw new NoPermissionException("no permission to update submission: " + id);
        } catch (DataSerializationException e) {
            log.warn("saveExperimentDetails(" + id + ") failure", e);
            throw new UnexpectedException("data save failed", e);
        }
    }

    @Override
    public ContactsUpdateResult updateContacts(int id, List<ContactsUpdateCommand> commands) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission =
                    submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.UPDATE);

            ContactsUpdateResult result = new ContactsUpdateResult();
            Experiment exp = submission.getExperiment();
            for (ContactsUpdateCommand command : commands) {
                switch (command.getType()) {
                    case UPDATE:
                        result.update(updateContact(command.getContact(), exp));
                        break;
                    case CREATE:
                        result.create(createContact(command.getContact(), exp));
                        break;
                    case REMOVE:
                        result.removeAll(removeContacts(command.getContactIds(), exp));
                        break;
                }
            }
            submission.setExperiment(exp);
            return result;
        } catch (RecordNotFoundException e) {
            log.warn("updateContacts(" + id + ") failure", e);
            throw new ResourceNotFoundException("Submission with id=" + id + " doesn't exist");
        } catch (AccessControlException e) {
            log.warn("updateContacts(" + id + ") failure", e);
            throw new NoPermissionException("no permission to update submission: " + id);
        } catch (DataSerializationException e) {
            log.warn("updateContacts(" + id + ") failure", e);
            throw new UnexpectedException("data save failed", e);
        }
    }

    private ContactDto updateContact(ContactDto dto, Experiment exp) {
        Contact contact = exp.getContact(dto.getId());
        contact.setFirstName(dto.getFirstName());
        contact.setLastName(dto.getLastName());
        contact.setMidInitials(dto.getMidInitials());
        contact.setEmail(dto.getEmail());
        contact.setPhone(dto.getPhone());
        contact.setFax(dto.getFax());
        contact.setAddress(dto.getAddress());
        contact.setAffiliation(dto.getAffiliation());
        contact.setRoles(dto.getRoles());
        return dto;
    }

    private ContactDto createContact(ContactDto dto, Experiment exp) {
        Contact contact = exp.createContact();
        return updateContact(new ContactDto(contact.getId()).update(dto), exp);
    }

    private List<ContactDto> removeContacts(List<Integer> ids, Experiment exp) throws DataSerializationException {
        List<Contact> removed = new ArrayList<Contact>();
        for (Integer id : ids) {
            Contact contact = exp.removeContact(id);
            if (contact != null) {
                removed.add(contact);
            }
        }
        return UIObjectConverter.uiContacts(removed);
    }
}
