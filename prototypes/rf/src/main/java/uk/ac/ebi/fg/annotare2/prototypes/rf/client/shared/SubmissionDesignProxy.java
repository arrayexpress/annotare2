package uk.ac.ebi.fg.annotare2.prototypes.rf.client.shared;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;
import uk.ac.ebi.fg.annotare2.prototypes.rf.model.SubmissionDesign;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
@ProxyFor(SubmissionDesign.class)
public interface SubmissionDesignProxy extends ValueProxy {

    List<SampleProxy> getSamples();

    List<ResultFileProxy> getFiles();
}

