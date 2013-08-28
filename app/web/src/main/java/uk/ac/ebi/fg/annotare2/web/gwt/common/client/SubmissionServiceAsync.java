package uk.ac.ebi.fg.annotare2.web.gwt.common.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.ArrayDesignDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.FtpFileInfo;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ExperimentUpdateCommand;

import java.util.List;
import java.util.Map;

public interface SubmissionServiceAsync {

    void getSubmission(int id, AsyncCallback<SubmissionDetails> async);

    void createExperimentSubmission(AsyncCallback<Integer> async);

    void createArrayDesignSubmission(AsyncCallback<Integer> async);

    void setupExperimentSubmission(int id, ExperimentSetupSettings settings, AsyncCallback<Void> async);

    void discardSubmissionData(int id, AsyncCallback<Void> async);

    void getIdfTable(int submissionId, AsyncCallback<Table> async);

    void getSdrfTable(int submissionId, AsyncCallback<Table> async);

    void getArrayDesignDetails(int id, AsyncCallback<ArrayDesignDetailsDto> async);

    void updateArrayDesign(int id, List<ArrayDesignUpdateCommand> commands, AsyncCallback<ArrayDesignUpdateResult> async);

    void loadExperiment(int id, AsyncCallback<ExperimentProfile> async);

    void updateExperiment(int id, List<ExperimentUpdateCommand> commands, AsyncCallback<ExperimentProfile> async);

    void loadDataFiles(int id, AsyncCallback<List<DataFileRow>> async);

    void uploadDataFile(int id, String fileName, AsyncCallback<Void> async);

    void registryFtpFiles(int id, List<FtpFileInfo> details, AsyncCallback<Map<Integer, String>> async);

    void removeFile(int id, long fileId, AsyncCallback<Void> async);
}
