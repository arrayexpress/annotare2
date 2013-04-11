package uk.ac.ebi.fg.annotare2.prototypes.rf.client.shared;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.RequestFactory;
import com.google.web.bindery.requestfactory.shared.Service;
import uk.ac.ebi.fg.annotare2.prototypes.rf.server.GuiceServiceLocator;
import uk.ac.ebi.fg.annotare2.prototypes.rf.server.services.SubmissionService;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public interface TestRequestFactory extends RequestFactory {

    @Service(value = SubmissionService.class, locator = GuiceServiceLocator.class)
    public interface SubmissionRequest extends RequestContext {

        Request<List<SubmissionProxy>> findAllSubmissions();

        Request<Void> persist(SubmissionProxy sbm);
    }

    SubmissionRequest submissionRequest();
}
