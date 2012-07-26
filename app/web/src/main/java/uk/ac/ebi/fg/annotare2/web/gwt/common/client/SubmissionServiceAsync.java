package uk.ac.ebi.fg.annotare2.web.gwt.common.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UISubmissionDetails;

public interface SubmissionServiceAsync {

    void getSubmission(int id, AsyncCallback<UISubmissionDetails> async);

    void createSubmission(AsyncCallback<UISubmissionDetails> async);
}
