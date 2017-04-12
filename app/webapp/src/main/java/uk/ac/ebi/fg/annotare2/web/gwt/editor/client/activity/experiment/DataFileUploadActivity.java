package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment;

import com.google.common.base.Function;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.event.DataFilesUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.event.DataFilesUpdateEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.proxy.DataFilesProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DataFilesUploadView;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ApplicationProperties;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentColumnsAndRows;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.UploadedFileInfo;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CriticalUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CriticalUpdateEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataFileRenamedEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataFileRenamedEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.proxy.ApplicationDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.proxy.ExperimentDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.DataUploadAndAssignmentView;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Collections2.transform;
import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;


/**
 * Created by haideri on 10/04/2017.
 */
public class DataFileUploadActivity extends AbstractActivity implements DataFilesUploadView.Presenter {

    private final DataFilesUploadView view;
    private final ApplicationDataProxy appDataService;
    private final ExperimentDataProxy expDataService;
    private final DataFilesProxy filesService;

    private EventBus eventBus;
    private HandlerRegistration criticalUpdateHandler;
    private HandlerRegistration dataUpdateHandler;
    private HandlerRegistration dataFileRenamedHandler;

    @Inject
    public DataFileUploadActivity(DataFilesUploadView view,
                                           ApplicationDataProxy appDataService,
                                           ExperimentDataProxy expDataService,
                                           DataFilesProxy filesService) {
        this.view = view;
        this.appDataService = appDataService;
        this.expDataService = expDataService;
        this.filesService = filesService;
    }

    public Activity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        this.eventBus = eventBus;

        view.setPresenter(this);
        panel.setWidget(view);

        criticalUpdateHandler = eventBus.addHandler(CriticalUpdateEvent.getType(), new CriticalUpdateEventHandler() {
            @Override
            public void criticalUpdateStarted(CriticalUpdateEvent event) {
            }

            @Override
            public void criticalUpdateFinished(CriticalUpdateEvent event) {
                reloadExpDataAsync();
            }
        });

        dataFileRenamedHandler = eventBus.addHandler(DataFileRenamedEvent.getType(), new DataFileRenamedEventHandler() {
            @Override
            public void onRename(DataFileRenamedEvent event) {
                reloadExpDataAsync();
            }
        });

        dataUpdateHandler = eventBus.addHandler(DataFilesUpdateEvent.getType(), new DataFilesUpdateEventHandler() {
            @Override
            public void onDataFilesUpdate() {
                loadFilesAsync();
            }
        });

        loadAppDataAsync();
        loadExpDataAsync();
        loadFilesAsync();
    }

    @Override
    public void onStop() {
        criticalUpdateHandler.removeHandler();
        dataFileRenamedHandler.removeHandler();
        dataUpdateHandler.removeHandler();
        super.onStop();
    }

    private void loadAppDataAsync() {
        appDataService.getApplicationPropertiesAsync(
                new ReportingAsyncCallback<ApplicationProperties>(FailureMessage.UNABLE_TO_LOAD_APP_PROPERTIES) {
                    @Override
                    public void onSuccess(ApplicationProperties properties) {
                        view.setApplicationProperties(properties);
                    }
                }
        );
    }

    private void loadExpDataAsync() {
        expDataService.getExperimentProfileTypeAsync(
                new ReportingAsyncCallback<ExperimentProfileType>(FailureMessage.UNABLE_TO_LOAD_SUBMISSION_TYPE) {
                    @Override
                    public void onSuccess(ExperimentProfileType result) {
                        view.setExperimentType(result);
                    }
                }
        );
        reloadExpDataAsync();
    }

    private void reloadExpDataAsync() {
        expDataService.getDataAssignmentColumnsAndRowsAsync(
                new ReportingAsyncCallback<DataAssignmentColumnsAndRows>(FailureMessage.UNABLE_TO_LOAD_DATA_ASSIGNMENT) {
                    @Override
                    public void onSuccess(DataAssignmentColumnsAndRows result) {
                        //view.getAssignmentView().setData(result.getColumns(), result.getRows());
                    }
                }
        );
    }


    private void loadFilesAsync() {
        filesService.getFiles(
                getSubmissionId(),
                new ReportingAsyncCallback<List<DataFileRow>>(FailureMessage.UNABLE_TO_LOAD_DATA_FILES_LIST) {
                    @Override
                    public void onSuccess(List<DataFileRow> result) {
                        view.setSubmissionId(getSubmissionId());
                        view.setDataFiles(result);
                        //view.getAssignmentView().setDataFiles(result);
                    }
                }
        );
    }

    @Override
    public void initSubmissionFtpDirectory(AsyncCallback<String> callback) {
        filesService.initSubmissionFtpDirectory(getSubmissionId(), callback);
    }

    /*@Override
    public void registerUploadedFiles(List<UploadedFileInfo> filesInfo, AsyncCallback<List<Boolean>> callback) {
        filesService.registerHttpFiles(getSubmissionId(), filesInfo, callback);
    }*/

    @Override
    public void uploadFile(UploadedFileInfo fileInfo, AsyncCallback<Void> callback) {
        filesService.addHttpFile(getSubmissionId(), fileInfo, callback);
    }

    @Override
    public void uploadFtpFiles(List<String> filesInfo, AsyncCallback<String> callback) {
        filesService.registerFtpFiles(getSubmissionId(), filesInfo, callback);
    }

    @Override
    public void renameFile(DataFileRow dataFile, String newFileName, final AsyncCallback<Void> callback) {
        filesService.renameFile(getSubmissionId(), dataFile, newFileName, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Void aVoid) {
                expDataService.invalidate();
                eventBus.fireEvent(new DataFileRenamedEvent());
                callback.onSuccess(aVoid);
            }
        });
    }

    @Override
    public void removeFiles(Set<DataFileRow> dataFiles, AsyncCallback<Void> callback) {
        filesService.deleteFiles(getSubmissionId(), new ArrayList<Long>(transform(dataFiles, new Function<DataFileRow, Long>() {
            public Long apply(@Nullable DataFileRow input) {
                return null != input ? input.getId() : null;
            }
        })), callback);
    }

}
