/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.renderer.IDFWriter;
import uk.ac.ebi.arrayexpress2.magetab.renderer.adaptor.SDRFGraphWriter;
import uk.ac.ebi.fg.annotare2.core.AccessControlException;
import uk.ac.ebi.fg.annotare2.core.components.EfoSearch;
import uk.ac.ebi.fg.annotare2.core.components.Messenger;
import uk.ac.ebi.fg.annotare2.core.magetab.MageTabGenerator;
import uk.ac.ebi.fg.annotare2.core.transaction.Transactional;
import uk.ac.ebi.fg.annotare2.core.utils.NamingPatternUtil;
import uk.ac.ebi.fg.annotare2.core.utils.ValidatorUtils;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionFeedbackDao;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionStatusHistoryDao;
import uk.ac.ebi.fg.annotare2.db.dao.UserDao;
import uk.ac.ebi.fg.annotare2.db.model.ArrayDesignSubmission;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.SubmissionFeedback;
import uk.ac.ebi.fg.annotare2.db.model.User;
import uk.ac.ebi.fg.annotare2.db.model.enums.DataFileStatus;
import uk.ac.ebi.fg.annotare2.db.model.enums.Permission;
import uk.ac.ebi.fg.annotare2.db.model.enums.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.UnknownExperimentTypeException;
import uk.ac.ebi.fg.annotare2.submission.model.ArrayDesignHeader;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.submission.model.ValidationResponse;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.ArrayDesignDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ExperimentUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountService;
import uk.ac.ebi.fg.annotare2.web.server.services.DataFileManagerImpl;
import uk.ac.ebi.fg.annotare2.web.server.services.MessengerImpl;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManagerImpl;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionValidator;
import uk.ac.ebi.fg.annotare2.web.server.services.utils.tsv.TsvParser;
import uk.org.lidalia.slf4jext.Logger;
import uk.org.lidalia.slf4jext.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static uk.ac.ebi.fg.annotare2.core.magetab.MageTabGenerator.restoreOriginalNameValues;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.ExperimentBuilderFactory.createExperimentProfile;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter.uiArrayDesignDetails;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter.uiSubmissionDetails;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter.uiUser;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater.experimentUpdater;

public class SubmissionServiceImpl extends SubmissionBasedRemoteService implements SubmissionService {

    private static final long serialVersionUID = 6482329782917056447L;

    private static final Logger log = LoggerFactory.getLogger(SubmissionServiceImpl.class);

    private final DataFileManagerImpl dataFileManager;
    private final SubmissionValidator validator;
    private final UserDao userDao;
    private final SubmissionFeedbackDao feedbackDao;
    private final EfoSearch efoSearch;
    private final Messenger messenger;
    private final SubmissionStatusHistoryDao submissionStatusHistoryDao;

    @Inject
    public SubmissionServiceImpl(AccountService accountService,
                                 SubmissionManagerImpl submissionManager,
                                 DataFileManagerImpl dataFileManager,
                                 SubmissionValidator validator,
                                 UserDao userDao,
                                 SubmissionFeedbackDao feedbackDao,
                                 EfoSearch efoSearch,
                                 Messenger messenger,
                                 SubmissionStatusHistoryDao submissionStatusHistoryDao) {
        super(accountService, submissionManager, messenger);
        this.dataFileManager = dataFileManager;
        this.validator = validator;
        this.userDao = userDao;
        this.feedbackDao = feedbackDao;
        this.efoSearch = efoSearch;
        this.messenger = messenger;
        this.submissionStatusHistoryDao = submissionStatusHistoryDao;
    }

    @Transactional
    @Override
    public SubmissionDetails getSubmissionDetails(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission sb = getSubmission(id, Permission.VIEW);
            return uiSubmissionDetails(sb);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
    }

    @Transactional
    @Override
    public ArrayDesignDetailsDto getArrayDesignDetails(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ArrayDesignSubmission sb = getArrayDesignSubmission(id, Permission.VIEW);
            return uiArrayDesignDetails(sb);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    @Transactional
    @Override
    public Table getIdfTable(long id) throws NoPermissionException, ResourceNotFoundException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.VIEW);
            ExperimentProfile exp = submission.getExperimentProfile();
            return asIdfTable(exp);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (ParseException | DataSerializationException | IOException e) {
            throw unexpected(e);
        }
    }

    @Transactional
    @Override
    public Table getSdrfTable(long id) throws NoPermissionException, ResourceNotFoundException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.VIEW);
            ExperimentProfile exp = submission.getExperimentProfile();
            return asSdrfTable(exp);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (ParseException | DataSerializationException | IOException e) {
            throw unexpected(e);
        }
    }

    private Table asIdfTable(ExperimentProfile exp) throws IOException, ParseException {
        MAGETABInvestigation mageTab = (new MageTabGenerator(exp, efoSearch)).generate();
        StringWriter out = new StringWriter();
        new IDFWriter(out).write(mageTab.IDF);
        return new TsvParser().parse(new ByteArrayInputStream(out.toString().getBytes(Charsets.UTF_8)));
    }

    private Table asSdrfTable(ExperimentProfile exp) throws IOException, ParseException {
        MAGETABInvestigation mageTab = (new MageTabGenerator(exp, efoSearch)).generate();
        StringWriter out = new StringWriter();
        new SDRFGraphWriter(out).write(mageTab.SDRFs.values().iterator().next());
        String sdrf = restoreOriginalNameValues(out.toString());
        Table sdrfTable = new TsvParser().parse(new ByteArrayInputStream(sdrf.getBytes(Charsets.UTF_8)));
        sdrfTable.cleanUp();
        return sdrfTable;
    }

    @Transactional
    @Override
    public String getGeneratedSamplesPreview(long id, int numOfSamples, String namingPattern, int startingNumber)
            throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.VIEW);
            ExperimentProfile exp = submission.getExperimentProfile();
            String format = NamingPatternUtil.convert(namingPattern);

            int index = startingNumber, skipCount, lastIndex = 0;
            String name, prevName = "";
            StringBuilder preview = new StringBuilder();

            for (int i = 0; i < numOfSamples; ++i) {
                skipCount = 0;
                do {
                    name = String.format(format, index++);
                    skipCount++;
                } while (null != exp.getSampleByName(name));
                if (0 == i) {
                    preview.append(name);
                    prevName = "";
                } else {
                    if (skipCount > 1) {
                        preview.append(prevName).append(",").append(name);
                        lastIndex = i;
                    } else if (i == numOfSamples - 1) {
                        preview.append((lastIndex + 1 >= i) ? "," : "..");
                        preview.append(name);
                    }
                    prevName = ((lastIndex + 1 >= i) ? "," : "..") + name;
                }
            }

            return preview.toString();
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }


    @Transactional
    @Override
    public ExperimentProfile loadExperiment(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.VIEW);
            return submission.getExperimentProfile();
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public void setupExperiment(final long id, final ExperimentSetupSettings settings, List<OntologyTerm> experimentDesigns)
            throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission =
                    getExperimentSubmission(id, Permission.UPDATE);
            ExperimentProfile experimentProfile = createExperimentProfile(settings, submission.getCreatedBy());
            experimentProfile.setExperimentalDesigns(experimentDesigns);
            submission.setExperimentProfile(experimentProfile);
            save(submission);

            /*//create ticket in RT
            messenger.send(
                    EmailTemplates.NEW_SUBMISSION_TEMPLATE,
                    new ImmutableMap.Builder<String, String>()
                            .put("to.name", submission.getCreatedBy().getName())
                            .put("to.email", submission.getCreatedBy().getEmail())
                            .put("submission.id", String.valueOf(submission.getId()))
                            .build()
                    , submission.getCreatedBy()
                    , submission
            );*/

        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }


    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public ExperimentProfile updateExperiment(final long id, final List<ExperimentUpdateCommand> commands) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.UPDATE);
            ExperimentProfile experiment = submission.getExperimentProfile();
            experimentUpdater(experiment).run(commands);
            submission.setExperimentProfile(experiment);
            submission.setTitle(experiment.getTitle());
            save(submission);
            return experiment;
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public ArrayDesignUpdateResult updateArrayDesign(final long id, final List<ArrayDesignUpdateCommand> commands) throws ResourceNotFoundException, NoPermissionException {
        try {
            ArrayDesignSubmission submission = getArrayDesignSubmission(id, Permission.UPDATE);
            ArrayDesignHeader header = submission.getHeader();
            ArrayDesignUpdateResult result = new ArrayDesignUpdatePerformerImpl(header).run(commands);
            submission.setHeader(header);
            submission.setTitle(header.getName());
            save(submission);
            return result;
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    @Transactional
    @Override
    public void assignSubmissionToMe(final long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = getSubmission(id, Permission.ASSIGN);
            if (SubmissionStatus.IN_PROGRESS == submission.getStatus()) {
                submission.setOwnedBy(getCurrentUser());
                save(submission);
            } else {
                throw new IllegalStateException("Submission has to have IN_PROGRESS state");
            }
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (IllegalStateException e) {
            throw unexpected(e);
        }
    }

    @Transactional
    @Override
    public void assignSubmissionToCreator(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = getSubmission(id, Permission.ASSIGN);
            if (SubmissionStatus.IN_PROGRESS == submission.getStatus()) {
                submission.setOwnedBy(submission.getCreatedBy());
                save(submission);
            } else {
                throw new IllegalStateException("Submission has to have IN_PROGRESS state");
            }
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (IllegalStateException e) {
            throw unexpected(e);
        }
    }

    @Transactional
    @Override
    public ValidationResult validateSubmission(long id) throws ResourceNotFoundException, NoPermissionException {
        List<String> failures = newArrayList();
        List<String> errors = newArrayList();
        List<String> warnings = newArrayList();

        try {
            Submission submission = getSubmission(id, Permission.VIEW);
            Collection<ValidationResponse> results = validator.validate(submission);
            for (ValidationResponse validationResponse : results) {
                if ( ValidatorUtils.isObsoleteError(validationResponse.getCode()) || ValidatorUtils.isObsoleteWarning(validationResponse.getCode())) continue;
                switch (validationResponse.getSeverity()) {
                    case WARNING:
                        warnings.add("WARNING: " + ValidatorUtils.getWarningString(validationResponse));
                        break;
                    case ERROR:
                        errors.add("ERROR: " + ValidatorUtils.getErrorString(validationResponse));
                        break;
                }
            }
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (ParseException | DataSerializationException | UnknownExperimentTypeException | IOException e) {
            throw unexpected(e);
        } catch (IllegalArgumentException e) {
            failures.add(e.getMessage());
        }
        ArrayList<String> errorList = newArrayList(new LinkedHashSet(errors));
        Collections.sort(errorList);
        return new ValidationResult( errorList,
                newArrayList(new LinkedHashSet(warnings)),
                newArrayList(new LinkedHashSet(failures)));
    }

    @Override
    public boolean checkRtServerStatus(int submissionId) throws Exception
    {
        try {
            return messenger.checkRtServerStatus(submissionId);
        }catch(Exception e)
        {
            log.error("RT Server is down!");
            return false;
        }
    }

    @Override
    public boolean isUpdateAllowed(int submissionId) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = getSubmission(submissionId, Permission.VIEW);
            User currentUser = getCurrentUser();
            return uiUser(currentUser).isCurator() || currentUser.isAllowed(submission, Permission.UPDATE);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public void submitSubmission(final long id)
            throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = getSubmission(id, Permission.UPDATE);
            User currentUser = getCurrentUser();
            boolean isCurator = uiUser(currentUser).isCurator();
            boolean isSequencing = false;


                    /*(submission instanceof ExperimentSubmission) ?
                    ((ExperimentSubmission) submission).getExperimentProfile().getType().isSequencing()
                    : false;*/

            SubmissionStatus nextStatus = isSequencing ? SubmissionStatus.AWAITING_FILE_VALIDATION
                    : (SubmissionStatus.IN_PROGRESS == submission.getStatus() ? SubmissionStatus.SUBMITTED
                    : SubmissionStatus.RESUBMITTED);

            if (submission.getStatus().canSubmit(isCurator)) {
                storeAssociatedFiles(submission);
                submission.setSubmitted(new Date());
                submission.setStatus(nextStatus);
                submission.setOwnedBy(userDao.getCuratorUser());
                save(submission);
                submissionStatusHistoryDao.saveStatusHistory(submission);
            }
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (DataSerializationException | URISyntaxException | IOException e) {
            throw unexpected(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public void deleteSubmission(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission sb = getSubmission(id, Permission.UPDATE);
            deleteSubmissionSoftly(sb);
            for (DataFile df : sb.getFiles()) {
                dataFileManager.deleteDataFileSoftly(df);
            }
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (IOException e) {
            throw unexpected(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public void postFeedback(long id, Byte score, String comment) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = getSubmission(id, Permission.VIEW);
            SubmissionFeedback feedback = feedbackDao.create(score, submission);
            feedback.setComment(comment);
            feedbackDao.save(feedback);
            sendFeedbackEmail(submission, score, comment);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public void sendMessage(long id, String subject, String message) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = getSubmission(id, Permission.VIEW);
            sendEmail(submission, subject, message);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
    }

    private void sendFeedbackEmail(Submission submission, Byte score, String comment) {
        try {
            messenger.send(
                    MessengerImpl.SUBMISSION_FEEDBACK_TEMPLATE,
                    new ImmutableMap.Builder<String, String>()
                            .put("submitter.name", submission.getCreatedBy().getName())
                            .put("submitter.email", submission.getCreatedBy().getEmail())
                            .put("submission.id", String.valueOf(submission.getId()))
                            .put("submission.title", submission.getTitle())
                            .put("submission.feedback.score", null != score ? String.valueOf(score) + "/9" : "n/a")
                            .put("submission.feedback.comment", null != comment ? comment : "n/a")
                            .build(),
                    getCurrentUser(),
                    submission
            );
        } catch (RuntimeException x) {
            log.error("Unable to send messenger", x);
        }
    }

    private void sendEmail(Submission submission, String subject, String message) {
        User u = getCurrentUser();
        try {
            messenger.send(
                    MessengerImpl.CONTACT_US_TEMPLATE,
                    new ImmutableMap.Builder<String, String>()
                            .put("from.name", u.getName())
                            .put("from.email", u.getEmail())
                            .put("submission.id", String.valueOf(submission.getId()))
                            .put("message.subject", subject)
                            .put("message.body", message)
                            .build(),
                    u,
                    submission
            );
        } catch (RuntimeException x) {
            log.error("Unable to send messenger", x);
        }
    }

    private void storeAssociatedFiles(Submission submission)
            throws DataSerializationException, URISyntaxException, IOException {

        Set<DataFile> files = dataFileManager.getAssignedFiles(
                submission,
                FileType.RAW_MATRIX_FILE,
                FileType.PROCESSED_FILE,
                FileType.PROCESSED_MATRIX_FILE
        );

        for (DataFile file : files) {
            if (null != file && DataFileStatus.ASSOCIATED == file.getStatus()) {
                dataFileManager.storeAssociatedFile(file);
            }
        }
    }
}
