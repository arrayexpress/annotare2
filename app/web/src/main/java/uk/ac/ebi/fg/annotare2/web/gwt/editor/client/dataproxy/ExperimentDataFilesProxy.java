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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.dataproxy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.NotificationPopupPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.event.DataFilesUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.Updater;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.HttpFileInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class ExperimentDataFilesProxy {

    private final SubmissionServiceAsync submissionServiceAsync;
    private final EventBus eventBus;
    private final ExperimentDataProxy expDataProxy;

    private List<DataFileRow> fileRows;

    private final Updater updater;

    @Inject
    public ExperimentDataFilesProxy(EventBus eventBus,
                                    SubmissionServiceAsync submissionServiceAsync,
                                    ExperimentDataProxy expDataProxy) {
        this.submissionServiceAsync = submissionServiceAsync;
        this.eventBus = eventBus;
        this.expDataProxy = expDataProxy;

        updater = new Updater(5000) {
            public void onAsyncUpdate(final AsyncCallback<Boolean> callback) {
                GWT.log("Updating data file list...");

                load(new AsyncCallback<List<DataFileRow>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(List<DataFileRow> result) {
                        for (DataFileRow row : result) {
                            if (!row.getStatus().isFinal()) {
                                callback.onSuccess(true);
                                break;
                            }
                        }
                        callback.onSuccess(false);
                    }
                });
            }
        };
    }

    public void getFilesAsync(AsyncCallback<List<DataFileRow>> callback) {
        if (fileRows != null) {
            callback.onSuccess(new ArrayList<DataFileRow>(fileRows));
            return;
        }
        load(callback);
    }

    private void load(final AsyncCallback<List<DataFileRow>> callback) {
        submissionServiceAsync.loadDataFiles(getSubmissionId(), new AsyncCallbackWrapper<List<DataFileRow>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(List<DataFileRow> result) {
                update(result);
                callback.onSuccess(result);
            }
        }.wrap());
    }

    private void update(List<DataFileRow> newFileRows) {
        boolean fireEvent = null == fileRows || !fileRows.equals(newFileRows);
        fileRows = new ArrayList<DataFileRow>(newFileRows);
        if (fireEvent) {
            eventBus.fireEvent(new DataFilesUpdateEvent());
        }
    }

    public void registerHttpFilesAsync(List<HttpFileInfo> filesInfo, final AsyncCallback<Map<Integer, String>> callback) {
        submissionServiceAsync.registerHttpFiles(getSubmissionId(), filesInfo, new AsyncCallbackWrapper<Map<Integer, String>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Map<Integer, String> result) {
                updater.update();
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void registerFtpFilesAsync(List<String> details, final AsyncCallback<String> callback) {
        submissionServiceAsync.registerFtpFiles(getSubmissionId(), details, new AsyncCallbackWrapper<String>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(String result) {
                updater.update();
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void renameFile(final DataFileRow dataFile, final String newFileName) {
        submissionServiceAsync.renameDataFile(getSubmissionId(), dataFile.getId(), newFileName,new AsyncCallbackWrapper<ExperimentProfile>() {
            @Override
            public void onFailure(Throwable caught) {
                NotificationPopupPanel.error("Unable to rename file '" + dataFile.getName() + "'", true);
            }

            @Override
            public void onSuccess(ExperimentProfile experiment) {
                expDataProxy.setUpdatedExperiment(experiment);
                updater.update();
            }
        }.wrap());
    }

    public void removeFiles(final List<Long> dataFiles, final AsyncCallback<Void> callback) {
        submissionServiceAsync.deleteDataFiles(getSubmissionId(), dataFiles, new AsyncCallbackWrapper<ExperimentProfile>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ExperimentProfile experiment) {
                expDataProxy.setUpdatedExperiment(experiment);
                updater.update();
                callback.onSuccess(null);
            }
        }.wrap());
    }
}
