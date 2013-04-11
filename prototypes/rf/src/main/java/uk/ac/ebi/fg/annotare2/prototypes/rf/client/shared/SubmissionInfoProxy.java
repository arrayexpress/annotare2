package uk.ac.ebi.fg.annotare2.prototypes.rf.client.shared;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;
import uk.ac.ebi.fg.annotare2.prototypes.rf.model.SubmissionInfo;

import java.util.Date;

/**
 * @author Olga Melnichuk
 */
@ProxyFor(SubmissionInfo.class)
public interface SubmissionInfoProxy extends ValueProxy {

    Date getDate();

    void setDate(Date date);

    String getDescription();

    void setDescription(String description);

    String getTitle();

    void setTitle(String title);

}
