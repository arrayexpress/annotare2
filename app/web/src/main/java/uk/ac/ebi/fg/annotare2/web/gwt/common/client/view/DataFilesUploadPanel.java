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

package uk.ac.ebi.fg.annotare2.web.gwt.common.client.view;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.UploadedFileInfo;

import java.util.List;
import java.util.Map;



public class DataFilesUploadPanel extends Composite {

    private Presenter presenter;

//    private static class CustomModalUploadStatus extends ModalUploadStatus {
//        @Override
//        public IUploadStatus newInstance() {
//            IUploadStatus ret = new CustomModalUploadStatus();
//            ret.setCancelConfiguration(cancelCfg);
//            return ret;
//        }
//    }

//    private class OnFinishUploaderHandler implements IUploader.OnFinishUploaderHandler {
//        @Override
//        public void onFinish(IUploader uploader) {
//            ServerWatchdog.resume();
//
//            final IUploadStatus status = uploader.getStatusWidget();
//            new Timer() {
//                public void run() {
//                    status.setVisible(false);
//                }
//            }.schedule(2000);
//
//            if (uploader.getStatus() == SUCCESS) {
//                if (presenter != null) {
//                    IFileInput input = uploader.getFileInput();
//                    List<String> fileNames = input.getFilenames();
//                    List<IUploader.UploadedInfo> uploaded = uploader.getServerMessage().getUploadedInfos();
//                    if (uploaded.size() == fileNames.size()) {
//                        final List<HttpFileInfo> filesInfo = new ArrayList<>(uploaded.size());
//                        for (int i = 0; i < uploaded.size(); ++i) {
//                            filesInfo.add(new HttpFileInfo(uploaded.get(i).getField(), removeFakePath(fileNames.get(i))));
//                        }
//                        presenter.uploadFiles(filesInfo,
//                                new ReportingAsyncCallback<Map<Integer, String>>(FailureMessage.UNABLE_TO_UPLOAD_FILES) {
//                                    @Override
//                                    public void onSuccess(Map<Integer, String> result) {
//                                        if (!result.isEmpty()) {
//                                            String message = "Some files were not uploaded:<br><br>";
//                                            for (int i = 0; i < filesInfo.size(); ++i) {
//                                                if (result.containsKey(i)) {
//                                                    message += filesInfo.get(i).getFileName() + " - " + result.get(i) + "<br>";
//                                                }
//                                            }
//                                            NotificationPopupPanel.error(message, false, false);
//                                        }
//                                    }
//                                });
//                    }
//                }
//            }
//        }
//    }

//    interface UploaderConstants extends IUploader.UploaderConstants {
//
//        @Constants.DefaultStringValue("Upload Files...")
//        String uploaderBrowse();
//    }

//    public static final UploaderConstants I18N_CONSTANTS = GWT.create(UploaderConstants.class);

    public DataFilesUploadPanel() {
        FlowPanel panel = new FlowPanel();
        initWidget(panel);

//        IUploadStatus status = new CustomModalUploadStatus();
//        status.setCancelConfiguration(
//                EnumSet.of(
//                        IUploadStatus.CancelBehavior.STOP_CURRENT
//                )
//        );
//        SingleUploader uploader = new SingleUploader(IFileInput.FileInputType.BUTTON);
//        uploader.setMultipleSelection(true);
//        uploader.setAutoSubmit(true);
//        uploader.setStatusWidget(status);
//        uploader.setStyleName("customUpload");
//        uploader.setI18Constants(I18N_CONSTANTS);
//        uploader.avoidRepeatFiles(false);
//        uploader.addOnStartUploadHandler(new IUploader.OnStartUploaderHandler() {
//            @Override
//            public void onStart(IUploader iUploader) {
//                ServerWatchdog.pause();
//            }
//        });
//        uploader.addOnFinishUploadHandler(new OnFinishUploaderHandler());
//        panel.add(uploader);
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void hide() {
        getWidget().setVisible(false);
    }

    public interface Presenter {
        void uploadFiles(List<UploadedFileInfo> filesInfo, AsyncCallback<Map<Integer, String>> callback);
    }

    private String removeFakePath(String fileName) {
        return fileName.replaceAll("^.*\\\\[Ff][Aa][Kk][Ee][Pp][Aa][Tt][Hh]\\\\(.+)$", "$1");
    }
}
