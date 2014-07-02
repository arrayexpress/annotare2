/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ApplicationProperties;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.dataproxy.ApplicationDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.dataproxy.DataFilesProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.dataproxy.ExperimentDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CriticalUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CriticalUpdateEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataFilesUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataFilesUpdateEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.DataUploadAndAssignmentView;

import java.util.List;
import java.util.Map;

public class DataUploadAndAssignmentActivity extends AbstractActivity implements DataUploadAndAssignmentView.Presenter {

    private final DataUploadAndAssignmentView view;
    private final ApplicationDataProxy appData;
    private final ExperimentDataProxy expData;
    private final DataFilesProxy dataFiles;
    private HandlerRegistration criticalUpdateHandler;
    private HandlerRegistration dataUpdateHandler;

    @Inject
    public DataUploadAndAssignmentActivity(DataUploadAndAssignmentView view,
                                           ApplicationDataProxy appData,
                                           ExperimentDataProxy expData,
                                           DataFilesProxy dataFiles) {
        this.view = view;
        this.appData = appData;
        this.expData = expData;
        this.dataFiles = dataFiles;
    }

    public Activity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        panel.setWidget(view);

        criticalUpdateHandler = eventBus.addHandler(CriticalUpdateEvent.getType(), new CriticalUpdateEventHandler() {
            @Override
            public void criticalUpdateStarted(CriticalUpdateEvent event) {
            }

            @Override
            public void criticalUpdateFinished(CriticalUpdateEvent event) {
                loadDataAsync();
            }
        });
        this.dataUpdateHandler = eventBus.addHandler(DataFilesUpdateEvent.getType(), new DataFilesUpdateEventHandler() {
            @Override
            public void onDataFilesUpdate() {
                loadFilesAsync();
            }
        });

        loadDataAsync();
        loadFilesAsync();
    }

    @Override
    public void onStop() {
        criticalUpdateHandler.removeHandler();
        dataUpdateHandler.removeHandler();
        super.onStop();
    }

    private void loadDataAsync() {
        appData.getApplicationPropertiesAsync(new AsyncCallback<ApplicationProperties>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Unable to load application properties");
            }

            @Override
            public void onSuccess(ApplicationProperties result) {
                view.getUploadView().setFtpProperties(result.getFtpUrl(), result.getFtpUsername(), result.getFtpPassword());
            }
        });
        expData.getExperimentProfileTypeAsync(new AsyncCallback<ExperimentProfileType>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Unable to load submission type");
            }

            @Override
            public void onSuccess(ExperimentProfileType result) {
                view.getUploadView().setExperimentType(result);
                view.getAssignmentView().setExperimentType(result);
            }
        });
        expData.getDataAssignmentColumnsAndRowsAsync(new AsyncCallback<DataAssignmentColumnsAndRows>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Unable to load data assignment rows");
            }

            @Override
            public void onSuccess(DataAssignmentColumnsAndRows result) {
                view.getAssignmentView().setData(result.getColumns(), result.getRows());
            }
        });
    }

    private void loadFilesAsync() {
        dataFiles.getFilesAsync(new AsyncCallback<List<DataFileRow>>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Unable to load a list of data files");
            }

            @Override
            public void onSuccess(List<DataFileRow> result) {
                view.getUploadView().setDataFiles(result);
                view.getAssignmentView().setDataFiles(result);
            }
        });
    }

    @Override
    public void createColumn(FileType type) {
        expData.createDataAssignmentColumn(type);
    }

    @Override
    public void removeColumns(List<Integer> indices) {
        expData.removeDataAssignmentColumns(indices);
    }

    @Override
    public void updateColumn(DataAssignmentColumn column) {
        expData.updateDataAssignmentColumn(column);
    }

    @Override
    public void filesUploaded(List<HttpFileInfo> filesInfo, AsyncCallback<Map<Integer, String>> callback) {
        dataFiles.registerHttpFilesAsync(filesInfo, callback);
    }

    @Override
    public void onFtpRegistrationFormSubmit(List<FtpFileInfo> filesInfo, AsyncCallback<Map<Integer, String>> callback) {
        dataFiles.registerFtpFilesAsync(filesInfo, callback);
    }

    @Override
    public void removeFile(DataFileRow dataFileRow) {
        dataFiles.removeFile(dataFileRow);
    }

}
