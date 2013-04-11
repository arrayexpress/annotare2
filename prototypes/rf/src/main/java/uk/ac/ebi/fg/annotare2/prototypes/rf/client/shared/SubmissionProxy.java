package uk.ac.ebi.fg.annotare2.prototypes.rf.client.shared;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.EntityProxyId;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import uk.ac.ebi.fg.annotare2.prototypes.rf.server.services.SubmissionLocator;
import uk.ac.ebi.fg.annotare2.prototypes.rf.model.Submission;

/**
 * @author Olga Melnichuk
 */
@ProxyFor(value = Submission.class, locator = SubmissionLocator.class)
public interface SubmissionProxy extends EntityProxy {

    SubmissionInfoProxy getInfo();

    SubmissionDesignProxy getDesign();

    EntityProxyId<SubmissionProxy> stableId();

}
