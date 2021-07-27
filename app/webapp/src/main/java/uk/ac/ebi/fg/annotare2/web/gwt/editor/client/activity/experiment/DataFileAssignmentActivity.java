package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.event.DataFilesUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.event.DataFilesUpdateEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.proxy.DataFilesProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentColumnsAndRows;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CriticalUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CriticalUpdateEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataFileRenamedEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataFileRenamedEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpDesignPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.proxy.ExperimentDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.DataAssignmentView;

import java.util.List;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;
import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CriticalUpdateEvent.criticalUpdateFinished;


/**
 * Created by haideri on 10/04/2017.
 */
public class DataFileAssignmentActivity extends AbstractActivity implements DataAssignmentView.Presenter{

    public final DataAssignmentView dataAssignmentView;
    private final ExperimentDataProxy expDataService;
    private final DataFilesProxy filesService;
    private EventBus eventBus;

    private HandlerRegistration criticalUpdateHandler;
    private HandlerRegistration dataUpdateHandler;
    private HandlerRegistration dataFileRenamedHandler;

    @Inject
    public DataFileAssignmentActivity(DataAssignmentView dataAssignmentView,
                                   ExperimentDataProxy expDataService,
                                   DataFilesProxy filesService){
        this.dataAssignmentView = dataAssignmentView;
        this.expDataService = expDataService;
        this.filesService = filesService;
    }
    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        this.eventBus = eventBus;
        dataAssignmentView.setPresenter(this);
        panel.setWidget(dataAssignmentView);

        criticalUpdateHandler = eventBus.addHandler(CriticalUpdateEvent.getType(), new CriticalUpdateEventHandler() {
            @Override
            public void criticalUpdateStarted(CriticalUpdateEvent event) {
            }

            @Override
            public void criticalUpdateFinished(CriticalUpdateEvent event) {
                loadExpDataAsync();
            }
        });

        dataFileRenamedHandler = eventBus.addHandler(DataFileRenamedEvent.getType(), new DataFileRenamedEventHandler() {
            @Override
            public void onRename(DataFileRenamedEvent event) {
                loadExpDataAsync();
            }
        });

        dataUpdateHandler = eventBus.addHandler(DataFilesUpdateEvent.getType(), new DataFilesUpdateEventHandler() {
            @Override
            public void onDataFilesUpdate() {
                loadFilesAsync();
            }
        });

        //loadExpDataAsync();
        loadFilesAsync();
    }

    @Override
    public void onStop() {
        criticalUpdateHandler.removeHandler();
        dataFileRenamedHandler.removeHandler();
        dataUpdateHandler.removeHandler();
        super.onStop();
    }

    private void loadFilesAsync() {
        filesService.getFiles(getSubmissionId(), new ReportingAsyncCallback<List<DataFileRow>>() {
            @Override
            public void onSuccess(List<DataFileRow> result) {
                dataAssignmentView.setDataFiles(result);
                dataAssignmentView.loadData();
            }
        });
    }

    private void loadExpDataAsync() {
        expDataService.getDataAssignmentColumnsAndRowsAsync(
                new ReportingAsyncCallback<DataAssignmentColumnsAndRows>(ReportingAsyncCallback.FailureMessage.UNABLE_TO_LOAD_DATA_ASSIGNMENT) {
                    @Override
                    public void onSuccess(DataAssignmentColumnsAndRows result) {
                        dataAssignmentView.setData(result.getColumns(), result.getRows());
                        loadFilesAsync();
                    }
                }
        );
    }

    public DataFileAssignmentActivity withPlace(ExpDesignPlace designPlace) {
        return this;
    }

    @Override
    public void refresh() {
        //eventBus.fireEvent(criticalUpdateFinished());
    }

}
