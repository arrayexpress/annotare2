package uk.ac.ebi.fg.annotare2.web.server.services;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.fg.annotare2.core.components.EfoSearch;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.integration.ExtendedAnnotareProperties;
import uk.ac.ebi.fg.annotare2.magetabcheck.MageTabChecker;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.UnknownExperimentTypeException;
import uk.ac.ebi.fg.annotare2.submission.model.ValidationResponse;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;
import uk.ac.ebi.fg.annotare2.web.server.services.files.DataFileConnector;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;

public class SubmissionValidatorIT {

    SubmissionValidator submissionValidator;
    private ExtendedAnnotareProperties properties;

    @Before
    public void setUp(){
        properties = createMock(ExtendedAnnotareProperties.class);
        submissionValidator = createMockBuilder(SubmissionValidator.class)
                .withConstructor(createMock(MageTabChecker.class),
                        createMock(DataFileManagerImpl.class),
                        createMock(DataFileConnector.class),
                        createMock(EfoSearch.class),
                        properties)
                .createMock();
    }

    @Test
    public void validateSubmissionShouldReturnNotNullResponse() throws IOException, NoSuchFieldException, IllegalAccessException,
            UnknownExperimentTypeException, DataSerializationException, ParseException {
        expect(properties.getSubmissionValidatorURL()).andReturn("http://hx-rke-wp-webadmin-25-worker-1.caas.ebi.ac.uk:31811/validate_annotare_json");
        replay(properties);
        Collection<ValidationResponse> result = submissionValidator.validate(getSubmission());
        assertNotNull(result);
        assertNotEquals(0, result.size());
    }

    @Test(expected = IOException.class)
    public void validateSubmissionShouldThrowExceptionForInvalidURL() throws IOException, NoSuchFieldException, IllegalAccessException,
            UnknownExperimentTypeException, DataSerializationException, ParseException {
        expect(properties.getSubmissionValidatorURL()).andReturn("");
        replay(properties);
        Collection<ValidationResponse> result = submissionValidator.validate(getSubmission());
    }
    private Submission getSubmission() throws NoSuchFieldException, IllegalAccessException, IOException {
        Submission submission = new ExperimentSubmission();
        Field field = submission.getClass().getDeclaredField("experimentString");
        field.setAccessible(true);
        field.set(submission, IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("TestExperiment.json"))));
        return submission;
    }
}