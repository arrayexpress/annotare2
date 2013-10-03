/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.configmodel.FileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentColumnsAndRows;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data.DataFiles;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data.ExperimentData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CriticalUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CriticalUpdateEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataFilesUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataFilesUpdateEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpDesignPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.DataAssignmentView;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class DataAssignmentActivity extends AbstractActivity implements DataAssignmentView.Presenter {

    private final DataAssignmentView view;
    private final ExperimentData expData;
    private final DataFiles dataFiles;
    private HandlerRegistration criticalUpdateHandler;
    private HandlerRegistration dataUpdateHandler;


    @Inject
    public DataAssignmentActivity(DataAssignmentView view, ExperimentData expData, DataFiles dataFiles) {
        this.view = view;
        this.expData = expData;
        this.dataFiles = dataFiles;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        panel.setWidget(view);
        loadDataAsync();
        loadFilesAsync();
        criticalUpdateHandler = eventBus.addHandler(CriticalUpdateEvent.getType(), new CriticalUpdateEventHandler() {
            @Override
            public void criticalUpdateStarted(CriticalUpdateEvent event) {
            }

            @Override
            public void criticalUpdateFinished(CriticalUpdateEvent event) {
                loadDataAsync();
            }
        });
        dataUpdateHandler = eventBus.addHandler(DataFilesUpdateEvent.getType(), new DataFilesUpdateEventHandler() {
            @Override
            public void onDataFilesUpdate() {
                loadFilesAsync();
            }
        });
    }

    @Override
    public void onStop() {
        criticalUpdateHandler.removeHandler();
        dataUpdateHandler.removeHandler();
        super.onStop();
    }

    public DataAssignmentActivity withPlace(ExpDesignPlace designPlace) {
        return this;
    }

    private void loadDataAsync() {
        expData.getDataAssignmentColumnsAndRowsAsync(new AsyncCallback<DataAssignmentColumnsAndRows>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Can't data assignment rows");
            }

            @Override
            public void onSuccess(DataAssignmentColumnsAndRows result) {
                view.setData(result.getColumns(), result.getRows());
            }
        });
    }

    private void loadFilesAsync() {
        dataFiles.getFilesAsync(new AsyncCallback<List<DataFileRow>>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Can't load list of data files");
            }

            @Override
            public void onSuccess(List<DataFileRow> result) {
                view.setDataFiles(result);
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
}
