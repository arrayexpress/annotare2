package uk.ac.ebi.fg.annotare2.prototypes.rf.client.shared;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;
import uk.ac.ebi.fg.annotare2.prototypes.rf.model.Sample;

/**
 * @author Olga Melnichuk
 */
@ProxyFor(Sample.class)
public interface SampleProxy extends ValueProxy {

    String getName();

    void setName(String name);
}
