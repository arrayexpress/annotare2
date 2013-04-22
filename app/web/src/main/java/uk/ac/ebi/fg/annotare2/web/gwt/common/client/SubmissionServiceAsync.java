package uk.ac.ebi.fg.annotare2.web.gwt.common.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;

public interface SubmissionServiceAsync {

    void getSubmission(int id, AsyncCallback<SubmissionDetails> async);

    void createExperimentSubmission(AsyncCallback<Integer> async);

    void createArrayDesignSubmission(AsyncCallback<Integer> async);

    void setupExperimentSubmission(int id, ExperimentSetupSettings settings, AsyncCallback<Void> async);

    void discardSubmissionData(int id, AsyncCallback<Void> async);

    void getExperimentSubmissionSettings(int id, AsyncCallback<ExperimentSettings> async);
}
