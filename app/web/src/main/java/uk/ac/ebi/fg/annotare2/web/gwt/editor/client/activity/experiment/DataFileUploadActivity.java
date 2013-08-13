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
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.sun.java_cup.internal.runtime.virtual_parse_stack;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data.DataFiles;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataFilesUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataFilesUpdateEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.DataFileUploadView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class DataFileUploadActivity extends AbstractActivity implements DataFileUploadView.Presenter {

    private final DataFileUploadView view;
    private final DataFiles dataFiles;

    @Inject
    public DataFileUploadActivity(DataFileUploadView view, DataFiles dataFiles) {
        this.view = view;
        this.dataFiles = dataFiles;
    }

    public Activity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.setPresenter(this);
        loadAsync();
        eventBus.addHandler(DataFilesUpdateEvent.getType(), new DataFilesUpdateEventHandler() {
            @Override
            public void onDataFilesUpdate(List<DataFileRow> rows) {
                setFileRows(rows);
            }
        });
    }

    private void loadAsync() {
        dataFiles.getFilesAsync(new AsyncCallback<List<DataFileRow>>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Server error: can't load file list");
            }

            @Override
            public void onSuccess(List<DataFileRow> rows) {
                setFileRows(rows);
            }
        });
    }

    private void setFileRows(List<DataFileRow> rows) {
        view.setRows(rows);
    }

    @Override
    public void fileUploaded(String name) {
        dataFiles.uploadFile(name);
    }
}
