/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.common.client.proxy;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataFilesServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.event.DataFilesUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.Updater;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.UploadedFileInfo;

import java.util.ArrayList;
import java.util.List;

public class DataFilesProxy {

    private final DataFilesServiceAsync filesService;
    private final EventBus eventBus;

    private List<DataFileRow> fileRows;

    private final DataFilesUpdater updater;

    @Inject
    public DataFilesProxy(EventBus eventBus, DataFilesServiceAsync filesService) {
        this.filesService = filesService;
        this.eventBus = eventBus;
        this.updater = new DataFilesUpdater();
    }

    public void getFiles(long submissionId, AsyncCallback<List<DataFileRow>> callback) {
        if (null == updater.getSubmissionId() || !updater.getSubmissionId().equals(submissionId)) {
            updater.setSubmissionId(submissionId);
            fileRows = null;
        }

        if (fileRows != null) {
            callback.onSuccess(new ArrayList<>(fileRows));
        } else {
            load(submissionId, callback);
        }
    }

    public void initSubmissionFtpDirectory(long submissionId, final AsyncCallback<String> callback) {
        filesService.initSubmissionFtpDirectory(submissionId, new AsyncCallbackWrapper<String>() {
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

    public void registerHttpFiles(long submissionId, List<UploadedFileInfo> filesInfo, final AsyncCallback<List<Boolean>> callback) {
        filesService.registerFilesBeforeUpload(submissionId, filesInfo, new AsyncCallbackWrapper<List<Boolean>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(List<Boolean> result) {
                updater.update();
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void addHttpFile(long submissionId, UploadedFileInfo fileInfo, final AsyncCallback<Void> callback) {
        filesService.addUploadedFile(submissionId, fileInfo, new AsyncCallbackWrapper<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Void result) {
                updater.update();
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void registerFtpFiles(long submissionId, List<String> details, final AsyncCallback<String> callback) {
        filesService.registerFtpFiles(submissionId, details, new AsyncCallbackWrapper<String>() {
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

    public void renameFile(long submissionId, final DataFileRow dataFile, final String newFileName, final AsyncCallback<Void> callback) {
        filesService.renameFile(submissionId, dataFile.getId(), newFileName, new AsyncCallbackWrapper<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Void aVoid) {
                updater.update();
                callback.onSuccess(aVoid);
            }
        }.wrap());
    }

    public void deleteFiles(long submissionId, final List<Long> dataFiles, final AsyncCallback<Void> callback) {
        filesService.deleteFiles(submissionId, dataFiles, new AsyncCallbackWrapper<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Void aVoid) {
                updater.update();
                callback.onSuccess(aVoid);
            }
        }.wrap());
    }

    private void load(long submissionId, final AsyncCallback<List<DataFileRow>> callback) {
        filesService.getFiles(submissionId, new AsyncCallbackWrapper<List<DataFileRow>>() {
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

    private class DataFilesUpdater extends Updater {

        private Long submissionId;

        public DataFilesUpdater() {
            super(5000);
            submissionId = null;
        }

        public Long getSubmissionId() {
            return submissionId;
        }

        public void setSubmissionId(long submissionId) {
            this.submissionId = submissionId;
        }

        public void onAsyncUpdate(final AsyncCallback<Boolean> callback) {
            if (null != submissionId) {
                load(submissionId, new AsyncCallback<List<DataFileRow>>() {
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
}
