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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import gwtupload.client.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.HttpFileInfo;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static gwtupload.client.IUploadStatus.Status.SUCCESS;

/**
 * @author Olga Melnichuk
 */
public class DataFilesUploadPanel extends Composite {

    private Presenter presenter;

    class OnFinishUploaderHandler implements IUploader.OnFinishUploaderHandler {
        @Override
        public void onFinish(IUploader uploader) {
            if (uploader.getStatus() == SUCCESS) {
                uploader.asWidget().removeFromParent();
                if (presenter != null) {
                    IFileInput input = uploader.getFileInput();
                    List<String> fileNames = input.getFilenames();
                    List<IUploader.UploadedInfo> uploaded = uploader.getServerMessage().getUploadedInfos();
                    if (uploaded.size() == fileNames.size()) {
                        final List<HttpFileInfo> filesInfo = new ArrayList<HttpFileInfo>(uploaded.size());
                        for (int i = 0; i < uploaded.size(); ++i) {
                            filesInfo.add(new HttpFileInfo(uploaded.get(i).getField(), removeFakePath(fileNames.get(i))));
                        }
                        presenter.filesUploaded(filesInfo, new AsyncCallback<Map<Integer, String>>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                Window.alert("There was a problem uploading file(s)");
                            }

                            @Override
                            public void onSuccess(Map<Integer, String> result) {
                                if (!result.isEmpty()) {
                                    String message = "Some files were not uploaded:\n\n";
                                    for (int i = 0; i < filesInfo.size(); ++i) {
                                        if (result.containsKey(i)) {
                                            message += filesInfo.get(i).getFileName() + " - " + result.get(i) + "\n";
                                        }
                                    }
                                    Window.alert(message);
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    interface UploaderConstants extends IUploader.UploaderConstants {

        @Constants.DefaultStringValue("Direct Upload...")
        String uploaderBrowse();
    }

    public static final UploaderConstants I18N_CONSTANTS = GWT.create(UploaderConstants.class);

    public DataFilesUploadPanel() {
        FlowPanel panel = new FlowPanel();
        initWidget(panel);

        IUploadStatus status = new BaseUploadStatus();
        status.setCancelConfiguration(
                EnumSet.of(
                        IUploadStatus.CancelBehavior.STOP_CURRENT
                        , IUploadStatus.CancelBehavior.REMOVE_INVALID
                        , IUploadStatus.CancelBehavior.REMOVE_CANCELLED_FROM_LIST
                )
        );
        final MultiUploader uploader = new MultiUploader(IFileInput.FileInputType.BUTTON);
        uploader.setStatusWidget(status);
        uploader.setStyleName("customUpload");
        uploader.setI18Constants(I18N_CONSTANTS);
        uploader.avoidRepeatFiles(false);
        uploader.addOnFinishUploadHandler(new OnFinishUploaderHandler());
        panel.add(uploader);
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public interface Presenter {
        void filesUploaded(List<HttpFileInfo> filesInfo, AsyncCallback<Map<Integer, String>> callback);
    }

    private String removeFakePath(String fileName) {
        return fileName.replaceAll("^.*\\\\[Ff][Aa][Kk][Ee][Pp][Aa][Tt][Hh]\\\\(.+)$", "$1");
    }
}
