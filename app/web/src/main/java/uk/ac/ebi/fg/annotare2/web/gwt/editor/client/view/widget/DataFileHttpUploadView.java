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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import gwtupload.client.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.HttpFileInfo;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static gwtupload.client.IUploadStatus.Status.SUCCESS;

/**
 * @author Olga Melnichuk
 */
public class DataFileHttpUploadView extends Composite {

    @UiField
    FlowPanel panel;

    private Presenter presenter;

    interface Binder extends UiBinder<Widget, DataFileHttpUploadView> {
        Binder BINDER = GWT.create(Binder.class);
    }

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
                        List<HttpFileInfo> filesInfo = new ArrayList<HttpFileInfo>(uploaded.size());
                        for (int i = 0; i < uploaded.size(); ++i) {
                            filesInfo.add(new HttpFileInfo(uploaded.get(i).getField(), fileNames.get(i)));
                        }
                        presenter.filesUploaded(filesInfo);
                    }
                }
            }
        }
    }

    interface UploaderConstants extends IUploader.UploaderConstants {

        @Constants.DefaultStringValue("Select files...")
        String uploaderBrowse();
    }

    public static final UploaderConstants I18N_CONSTANTS = GWT.create(UploaderConstants.class);

    public DataFileHttpUploadView() {
        initWidget(Binder.BINDER.createAndBindUi(this));

        //UploadStatus status = new UploadStatus();
        //status.setCancelConfiguration(cancelBehaviors);

        IUploadStatus status = new BaseUploadStatus();
        status.setCancelConfiguration(
                EnumSet.of(
                        IUploadStatus.CancelBehavior.STOP_CURRENT
                        , IUploadStatus.CancelBehavior.REMOVE_INVALID
                        , IUploadStatus.CancelBehavior.REMOVE_CANCELLED_FROM_LIST
                )
        );
        final MultiUploader uploader = new MultiUploader(IFileInput.FileInputType.BUTTON);
        uploader.setI18Constants(I18N_CONSTANTS);
        uploader.avoidRepeatFiles(true);
        uploader.addOnFinishUploadHandler(new OnFinishUploaderHandler());
        panel.add(uploader);
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public interface Presenter {
        void filesUploaded(List<HttpFileInfo> filesInfo);
    }
}
