package uk.ac.ebi.fg.annotare2.web.gwt.common.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.ArrayDesignDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ExperimentUpdateCommand;

import java.util.List;

public interface SubmissionServiceAsync {

    void getSubmissionDetails(long id, AsyncCallback<SubmissionDetails> async);

    void setupExperiment(long id, ExperimentSetupSettings settings, AsyncCallback<Void> async);

    void assignSubmissionToMe(long id, AsyncCallback<Void> async);

    void assignSubmissionToCreator(long id, AsyncCallback<Void> async);

    void validateSubmission(long id, AsyncCallback<ValidationResult> async);

    void submitSubmission(long id, AsyncCallback<Void> async);

    void getIdfTable(long id, AsyncCallback<Table> async);

    void getSdrfTable(long id, AsyncCallback<Table> async);

    void getGeneratedSamplesPreview(long id, int numOfSamples, String namingPattern, int startingNumber, AsyncCallback<String> async);

    void getArrayDesignDetails(long id, AsyncCallback<ArrayDesignDetailsDto> async);

    void updateArrayDesign(long id, List<ArrayDesignUpdateCommand> commands, AsyncCallback<ArrayDesignUpdateResult> async);

    void loadExperiment(long id, AsyncCallback<ExperimentProfile> async);

    void updateExperiment(long id, List<ExperimentUpdateCommand> commands, AsyncCallback<ExperimentProfile> async);

    void deleteSubmission(long id, AsyncCallback<Void> async);

    void sendMessage(long id, String subject, String message, AsyncCallback<Void> async);

    void postFeedback(long id, Byte score, String comment, AsyncCallback<Void> async);

    void getSubmissionCountForCurrentUser(AsyncCallback<Integer> async);
}
