package uk.ac.ebi.fg.annotare2.web.gwt.common.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.UpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.UpdateResult;

import java.util.List;

public interface SubmissionServiceAsync {

    void getSubmission(int id, AsyncCallback<SubmissionDetails> async);

    void createExperimentSubmission(AsyncCallback<Integer> async);

    void createArrayDesignSubmission(AsyncCallback<Integer> async);

    void setupExperimentSubmission(int id, ExperimentSetupSettings settings, AsyncCallback<Void> async);

    void discardSubmissionData(int id, AsyncCallback<Void> async);

    void getExperimentSettings(int id, AsyncCallback<ExperimentSettings> async);

    void getSamples(int id, AsyncCallback<SampleRowsAndColumns> async);

    void getExperimentDetails(int id, AsyncCallback<DetailsDto> async);

    void getContacts(int id, AsyncCallback<List<ContactDto>> async);

    void getPublications(int id, AsyncCallback<List<PublicationDto>> async);

    void getIdfTable(int submissionId, AsyncCallback<Table> async);

    void getSdrfTable(int submissionId, AsyncCallback<Table> async);

    void updateExperiment(int id, List<UpdateCommand> commands, AsyncCallback<UpdateResult> async);
}
