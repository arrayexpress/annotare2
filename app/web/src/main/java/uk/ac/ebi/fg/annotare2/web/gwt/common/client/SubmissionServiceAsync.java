package uk.ac.ebi.fg.annotare2.web.gwt.common.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;

public interface SubmissionServiceAsync {
    void getSubmission(int id, AsyncCallback<SubmissionDetails> async);
}
