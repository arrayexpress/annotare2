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

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.dataproxy;

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

public class DataFilesProxy {

    private final SubmissionServiceAsync submissionServiceAsync;
    private final EventBus eventBus;

    private Long submissionId;
    private List<DataFileRow> fileRows;

    private final Updater updater;

    @Inject
    public DataFilesProxy(EventBus eventBus, SubmissionServiceAsync submissionServiceAsync) {
        this.submissionServiceAsync = submissionServiceAsync;
        this.eventBus = eventBus;
        updater = new DataFilesUpdater();
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
        fileRows = null;
    }

    public void getFilesAsync(AsyncCallback<List<DataFileRow>> callback) {
        if (fileRows != null) {
            callback.onSuccess(new ArrayList<DataFileRow>(fileRows));
            return;
        }
        load(callback);
    }

    public void registerHttpFilesAsync(List<HttpFileInfo> filesInfo, final AsyncCallback<Map<Integer, String>> callback) {
        if (null != submissionId) {
            submissionServiceAsync.registerHttpFiles(submissionId, filesInfo, new AsyncCallbackWrapper<Map<Integer, String>>() {
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
    }

    public void registerFtpFilesAsync(List<String> details, final AsyncCallback<String> callback) {
        if (null != submissionId) {
            submissionServiceAsync.registerFtpFiles(submissionId, details, new AsyncCallbackWrapper<String>() {
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
    }

    public void renameFile(final DataFileRow dataFile, final String newFileName) {
        if (null != submissionId) {
            submissionServiceAsync.renameDataFile(submissionId, dataFile.getId(), newFileName, new AsyncCallbackWrapper<ExperimentProfile>() {
                @Override
                public void onFailure(Throwable caught) {
                    NotificationPopupPanel.error("Unable to rename file '" + dataFile.getName() + "'", true);
                }

                @Override
                public void onSuccess(ExperimentProfile experiment) {
                    updater.update();
                }
            }.wrap());
        }
    }

    public void removeFiles(final List<Long> dataFiles, final AsyncCallback<Void> callback) {
        if (null != submissionId) {
            submissionServiceAsync.deleteDataFiles(submissionId, dataFiles, new AsyncCallbackWrapper<ExperimentProfile>() {
                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                }

                @Override
                public void onSuccess(ExperimentProfile experiment) {
                    updater.update();
                    callback.onSuccess(null);
                }
            }.wrap());
        }
    }

    private void load(final AsyncCallback<List<DataFileRow>> callback) {
        if (null != submissionId) {
            submissionServiceAsync.loadDataFiles(submissionId, new AsyncCallbackWrapper<List<DataFileRow>>() {
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
    }

    private void update(List<DataFileRow> newFileRows) {
        boolean fireEvent = null == fileRows || !fileRows.equals(newFileRows);
        fileRows = new ArrayList<DataFileRow>(newFileRows);
        if (fireEvent) {
            eventBus.fireEvent(new DataFilesUpdateEvent());
        }
    }

    private class DataFilesUpdater extends Updater {

        public DataFilesUpdater() {
            super(5000);
        }

        public void onAsyncUpdate(final AsyncCallback<Boolean> callback) {
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
    }
}
