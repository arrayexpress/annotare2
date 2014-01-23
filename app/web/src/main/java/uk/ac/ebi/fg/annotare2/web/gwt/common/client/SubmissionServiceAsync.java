package uk.ac.ebi.fg.annotare2.web.gwt.common.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.ArrayDesignDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.FtpFileInfo;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ExperimentUpdateCommand;

import java.util.List;
import java.util.Map;

public interface SubmissionServiceAsync {

    void getSubmission(long id, AsyncCallback<SubmissionDetails> async);

    void createExperiment(AsyncCallback<Long> async);

    void createArrayDesign(AsyncCallback<Long> async);

    void setupExperiment(long id, ExperimentSetupSettings settings, AsyncCallback<Void> async);

    void submitSubmission(long id, AsyncCallback<Void> async);

    void getIdfTable(long id, AsyncCallback<Table> async);

    void getSdrfTable(long id, AsyncCallback<Table> async);

    void getArrayDesignDetails(long id, AsyncCallback<ArrayDesignDetailsDto> async);

    void updateArrayDesign(long id, List<ArrayDesignUpdateCommand> commands, AsyncCallback<ArrayDesignUpdateResult> async);

    void loadExperiment(long id, AsyncCallback<ExperimentProfile> async);

    void updateExperiment(long id, List<ExperimentUpdateCommand> commands, AsyncCallback<ExperimentProfile> async);

    void loadDataFiles(long id, AsyncCallback<List<DataFileRow>> async);

    void uploadDataFile(long id, String fileName, AsyncCallback<Void> async);

    void registerFtpFiles(long id, List<FtpFileInfo> details, AsyncCallback<Map<Integer, String>> async);

    void deleteDataFile(long id, long fileId, AsyncCallback<Void> async);

    void deleteSubmission(long id, AsyncCallback<Void> async);
}
