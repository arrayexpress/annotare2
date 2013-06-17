package uk.ac.ebi.fg.annotare2.web.gwt.common.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.ArrayDesignDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.*;

import java.util.List;

public interface SubmissionServiceAsync {

    void getSubmission(int id, AsyncCallback<SubmissionDetails> async);

    void createExperimentSubmission(AsyncCallback<Integer> async);

    void createArrayDesignSubmission(AsyncCallback<Integer> async);

    void setupExperimentSubmission(int id, ExperimentSetupSettings settings, AsyncCallback<Void> async);

    void discardSubmissionData(int id, AsyncCallback<Void> async);

    void getExperimentSettings(int id, AsyncCallback<ExperimentSettings> async);

    void getSamples(int id, AsyncCallback<SampleRowsAndColumns> async);

    void getExperimentDetails(int id, AsyncCallback<ExperimentDetailsDto> async);

    void getContacts(int id, AsyncCallback<List<ContactDto>> async);

    void getPublications(int id, AsyncCallback<List<PublicationDto>> async);

    void getIdfTable(int submissionId, AsyncCallback<Table> async);

    void getSdrfTable(int submissionId, AsyncCallback<Table> async);

    void getArrayDesignDetails(int id, AsyncCallback<ArrayDesignDetailsDto> async);

    void updateArrayDesign(int id, List<ArrayDesignUpdateCommand> commands, AsyncCallback<ArrayDesignUpdateResult> async);

    void loadExperiment(int id, AsyncCallback<ExperimentProfile> async);

    void updateExperiment(int id, List<ExperimentUpdateCommand> commands, AsyncCallback<ExperimentProfile> async);
}
