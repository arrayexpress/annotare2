package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.proxy.DataFilesProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentColumnsAndRows;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRowsAndColumns;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpDesignPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.proxy.ExperimentDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.AdditionalFilesView;

import java.util.List;
import java.util.stream.Collectors;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

public class AdditionalFilesActivity extends AbstractActivity {

    public final AdditionalFilesView additionalFilesView;
    private final ExperimentDataProxy expDataService;
    private final DataFilesProxy filesService;
    private EventBus eventBus;

    @Inject
    public AdditionalFilesActivity(AdditionalFilesView additionalFilesView,
                                   ExperimentDataProxy expDataService,
                                   DataFilesProxy filesService){
        this.additionalFilesView = additionalFilesView;
        this.expDataService = expDataService;
        this.filesService = filesService;
    }
    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        this.eventBus = eventBus;
        panel.setWidget(additionalFilesView);

        loadExpDataAsync();
    }

    private void loadFilesAsync() {
        filesService.getFiles(getSubmissionId(), new ReportingAsyncCallback<List<DataFileRow>>() {
            @Override
            public void onSuccess(List<DataFileRow> result) {
                additionalFilesView.setDataFiles(result);
                additionalFilesView.loadData();
            }
        });
    }

    private void loadExpDataAsync() {
        expDataService.getDataAssignmentColumnsAndRowsAsync(
                new ReportingAsyncCallback<DataAssignmentColumnsAndRows>(ReportingAsyncCallback.FailureMessage.UNABLE_TO_LOAD_DATA_ASSIGNMENT) {
                    @Override
                    public void onSuccess(DataAssignmentColumnsAndRows result) {
                        additionalFilesView.setData(result.getColumns(), result.getRows());
                        loadFilesAsync();
                    }
                }
        );
    }

    public AdditionalFilesActivity withPlace(ExpDesignPlace designPlace) {
        return this;
    }
}
