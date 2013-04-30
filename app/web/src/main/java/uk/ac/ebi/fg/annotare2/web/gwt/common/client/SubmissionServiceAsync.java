package uk.ac.ebi.fg.annotare2.web.gwt.common.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;

import java.util.List;

public interface SubmissionServiceAsync {

    void getSubmission(int id, AsyncCallback<SubmissionDetails> async);

    void createExperimentSubmission(AsyncCallback<Integer> async);

    void createArrayDesignSubmission(AsyncCallback<Integer> async);

    void setupExperimentSubmission(int id, ExperimentSetupSettings settings, AsyncCallback<Void> async);

    void discardSubmissionData(int id, AsyncCallback<Void> async);

    void getExperimentSettings(int id, AsyncCallback<ExperimentSettings> async);

    void getSamples(int id, AsyncCallback<List<SampleRow>> async);

    void getExperimentDetails(int id, AsyncCallback<ExperimentDetails> async);

    void saveExperimentDetails(int id, ExperimentDetails details, AsyncCallback<Void> async);
}
