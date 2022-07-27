package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.gwt.user.server.rpc.UnexpectedException;
import org.apache.commons.io.IOUtils;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.fg.annotare2.core.AccessControlException;
import uk.ac.ebi.fg.annotare2.core.components.EfoSearch;
import uk.ac.ebi.fg.annotare2.core.components.Messenger;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionFeedbackDao;
import uk.ac.ebi.fg.annotare2.db.dao.UserDao;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.UnknownExperimentTypeException;
import uk.ac.ebi.fg.annotare2.submission.model.ValidationResponse;
import uk.ac.ebi.fg.annotare2.submission.model.ValidationSeverity;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountService;
import uk.ac.ebi.fg.annotare2.web.server.services.DataFileManagerImpl;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManagerImpl;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionValidator;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SubmissionServiceImplTest {
    private SubmissionManagerImpl submissionManagerMock;
    private SubmissionValidator submissionValidatorMock;
    private SubmissionServiceImpl submissionService;

    @Before
    public void setUp() throws Exception {
        submissionManagerMock = createMock(SubmissionManagerImpl.class);
        submissionValidatorMock = createMock(SubmissionValidator.class);
        submissionService = createMockBuilder(SubmissionServiceImpl.class)
                .withConstructor(createMock(AccountService.class),
                        submissionManagerMock,
                        createMock(DataFileManagerImpl.class),
                        submissionValidatorMock,
                        createMock(UserDao.class),
                        createMock(SubmissionFeedbackDao.class),
                        createMock(EfoSearch.class),
                        createMock(Messenger.class))
                .addMockedMethods("getSubmission", "unexpected")
                .createMock();
    }

    @Test
    public void validateSubmissionShouldReturnZeroErrorsAndWarningsForValidSubmission() throws RecordNotFoundException, AccessControlException, NoSuchFieldException,
            IllegalAccessException, UnknownExperimentTypeException, DataSerializationException, IOException,
            ParseException, NoPermissionException, ResourceNotFoundException {
        expect(submissionService.getSubmission(anyInt(),anyObject())).andReturn(getSubmission());
        expect(submissionManagerMock.getSubmission(anyObject(),anyInt(),capture(new Capture<Class<Submission>>()),
                anyObject())).andReturn(getSubmission());
        expect(submissionValidatorMock.validate(anyObject())).andReturn(Collections.EMPTY_LIST);
        replay(submissionService);
        replay(submissionValidatorMock);
        ValidationResult result = submissionService.validateSubmission(10);
        assertEquals(0, result.getErrors().size());
        assertEquals(0, result.getWarnings().size());
    }

    @Test
    public void validateSubmissionShouldReturnErrorsForInValidSubmission() throws RecordNotFoundException, AccessControlException, NoSuchFieldException,
            IllegalAccessException, UnknownExperimentTypeException, DataSerializationException, IOException,
            ParseException, NoPermissionException, ResourceNotFoundException {
        expect(submissionService.getSubmission(anyInt(),anyObject())).andReturn(getSubmission());
        expect(submissionManagerMock.getSubmission(anyObject(),anyInt(),capture(new Capture<Class<Submission>>()),
                anyObject())).andReturn(getSubmission());
        expect(submissionValidatorMock.validate(anyObject())).andReturn(getValidationResponse());
        replay(submissionService);
        replay(submissionValidatorMock);
        ValidationResult result = submissionService.validateSubmission(10);
        assertNotEquals(0, result.getErrors().size());
        assertNotEquals(0, result.getWarnings().size());
    }

    @Test(expected = UnexpectedException.class)
    public void validateSubmissionShouldThrowUnExpectedExceptionWhenValidatorFailsWithException() throws RecordNotFoundException, AccessControlException, NoSuchFieldException,
            IllegalAccessException, UnknownExperimentTypeException, DataSerializationException, IOException,
            ParseException, NoPermissionException, ResourceNotFoundException {
        expect(submissionService.getSubmission(anyInt(),anyObject())).andReturn(getSubmission());
        expect(submissionManagerMock.getSubmission(anyObject(),anyInt(),capture(new Capture<Class<Submission>>()),
                anyObject())).andReturn(getSubmission());
        expect(submissionService.unexpected(anyObject())).andReturn(new UnexpectedException("Unexpected server error",new Throwable()));
        expect(submissionValidatorMock.validate(anyObject())).andThrow(new IOException());
        replay(submissionService);
        replay(submissionValidatorMock);
        submissionService.validateSubmission(10);

    }

    private Collection<ValidationResponse> getValidationResponse() {
        Collection<ValidationResponse> responses = new ArrayList<>();
        ValidationResponse validationError = new ValidationResponse();
        validationError.setCode("C02");
        validationError.setMessage("A contact must have a last name specified. Go to \\\"Contacts\\\", select a contact, and fill in the form.");
        validationError.setSection("CONTACTS");
        validationError.setSeverity(ValidationSeverity.ERROR);
        ValidationResponse validationWarning = new ValidationResponse();
        validationWarning.setCode("PD01");
        validationWarning.setMessage("The experiment should have processed data (this is recommended for compliance with MIAME/MINSEQE guidelines).");
        validationWarning.setSection("DESIGN:FILES");
        validationWarning.setSeverity(ValidationSeverity.WARNING);
        responses.add(validationError);
        responses.add(validationWarning);
        return Collections.unmodifiableCollection(responses);
    }

    private Submission getSubmission() throws NoSuchFieldException, IllegalAccessException, IOException {
        Submission submission = new ExperimentSubmission();
        Field field = submission.getClass().getDeclaredField("experimentString");
        field.setAccessible(true);
        field.set(submission, IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("TestExperiment.json"))));
        return submission;
    }
}