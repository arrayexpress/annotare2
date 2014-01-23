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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.dataproxy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.FtpFileInfo;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataFilesUpdateEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class DataFilesProxy {

    private final SubmissionServiceAsync submissionServiceAsync;
    private final EventBus eventBus;
    private List<DataFileRow> fileRows;

    private final Updater updater;

    @Inject
    public DataFilesProxy(EventBus eventBus,
                          SubmissionServiceAsync submissionServiceAsync) {
        this.submissionServiceAsync = submissionServiceAsync;
        this.eventBus = eventBus;

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
        boolean fireEvent = fileRows != null && !fileRows.equals(newFileRows);
        fileRows = new ArrayList<DataFileRow>(newFileRows);
        if (fireEvent) {
            eventBus.fireEvent(new DataFilesUpdateEvent());
        }
    }

    public void uploadFileAsync(String name) {
        submissionServiceAsync.uploadDataFile(getSubmissionId(), name, new AsyncCallbackWrapper<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Server error: can't finish file uploading");
            }

            @Override
            public void onSuccess(Void result) {
                updater.update();
            }
        }.wrap());
    }

    public void registerFtpFilesAsync(List<FtpFileInfo> details, final AsyncCallback<Map<Integer, String>> callback) {
        submissionServiceAsync.registerFtpFiles(getSubmissionId(), details, new AsyncCallbackWrapper<Map<Integer, String>>() {
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

    public void removeFile(final DataFileRow dataFile) {
        submissionServiceAsync.deleteDataFile(getSubmissionId(), dataFile.getId(), new AsyncCallbackWrapper<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Server error: can't remove file " + dataFile.getName());
            }

            @Override
            public void onSuccess(Void result) {
                updater.update();
            }
        }.wrap());
    }
}
