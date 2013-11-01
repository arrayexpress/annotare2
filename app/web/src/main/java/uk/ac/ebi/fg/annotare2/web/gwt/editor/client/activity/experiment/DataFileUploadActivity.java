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
import com.google.web.bindery.event.shared.HandlerRegistration;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ApplicationProperties;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.FtpFileInfo;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.dataproxy.ApplicationDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.dataproxy.DataFilesProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataFilesUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataFilesUpdateEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.DataFileUploadView;

import java.util.List;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class DataFileUploadActivity extends AbstractActivity implements DataFileUploadView.Presenter {

    private final DataFileUploadView view;
    private final DataFilesProxy dataFilesProxy;
    private final ApplicationDataProxy appData;

    private HandlerRegistration handlerRegistration;

    @Inject
    public DataFileUploadActivity(DataFileUploadView view, DataFilesProxy dataFilesProxy, ApplicationDataProxy appData) {
        this.view = view;
        this.dataFilesProxy = dataFilesProxy;
        this.appData = appData;
    }

    public Activity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        handlerRegistration = eventBus.addHandler(DataFilesUpdateEvent.getType(), new DataFilesUpdateEventHandler() {
            @Override
            public void onDataFilesUpdate() {
                loadAsync();
            }
        });

        panel.setWidget(view);
        view.setPresenter(this);
        loadAsync();
    }

    @Override
    public void onStop() {
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
        }
        super.onStop();
    }

    private void loadAsync() {
        dataFilesProxy.getFilesAsync(new AsyncCallback<List<DataFileRow>>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Server error: can't load file list");
            }

            @Override
            public void onSuccess(List<DataFileRow> rows) {
                view.setRows(rows);
            }
        });
        appData.getApplicationPropertiesAsync(new AsyncCallback<ApplicationProperties>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Server error: can't load application properties");
            }

            @Override
            public void onSuccess(ApplicationProperties result) {
                view.setFtpProperties(result.getFtpUrl(), result.getFtpUsername(), result.getFtpPassword());
            }
        });
    }

    @Override
    public void fileUploaded(String name) {
        dataFilesProxy.uploadFileAsync(name);
    }

    @Override
    public void onFtpRegistrationFormSubmit(List<FtpFileInfo> details, AsyncCallback<Map<Integer, String>> callback) {
        dataFilesProxy.registryFtpFilesAsync(details, callback);
    }

    @Override
    public void removeFile(DataFileRow dataFileRow) {
        dataFilesProxy.removeFile(dataFileRow);
    }
}
