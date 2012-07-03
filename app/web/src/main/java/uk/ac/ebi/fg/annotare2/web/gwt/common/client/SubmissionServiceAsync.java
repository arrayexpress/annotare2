package uk.ac.ebi.fg.annotare2.web.gwt.common.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UISubmission;

public interface SubmissionServiceAsync {
    void getSubmission(int id, AsyncCallback<UISubmission> async);
}
