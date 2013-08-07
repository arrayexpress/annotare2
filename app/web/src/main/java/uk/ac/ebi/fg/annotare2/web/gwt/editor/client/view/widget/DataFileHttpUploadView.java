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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploader;
import gwtupload.client.MultiUploader;

import java.util.HashSet;
import java.util.Set;

import static gwtupload.client.IUploadStatus.Status.SUCCESS;

/**
 * @author Olga Melnichuk
 */
public class DataFileHttpUploadView extends Composite {

    @UiField
    FlowPanel panel;

    interface Binder extends UiBinder<Widget, DataFileHttpUploadView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public DataFileHttpUploadView() {
        initWidget(Binder.BINDER.createAndBindUi(this));

        final MultiUploader uploader = new MultiUploader();
        uploader.avoidRepeatFiles(false);
        Set<IUploadStatus.CancelBehavior> cancelBehaviors = new HashSet<IUploadStatus.CancelBehavior>();
        cancelBehaviors.add(IUploadStatus.CancelBehavior.REMOVE_CANCELLED_FROM_LIST);
        cancelBehaviors.add(IUploadStatus.CancelBehavior.STOP_CURRENT);
        cancelBehaviors.add(IUploadStatus.CancelBehavior.REMOVE_REMOTE);

        UploadStatus status = new UploadStatus();
        status.setCancelConfiguration(cancelBehaviors);
        uploader.setStatusWidget(status);
        uploader.setAutoSubmit(true);

        uploader.addOnFinishUploadHandler(new IUploader.OnFinishUploaderHandler() {
            @Override
            public void onFinish(IUploader iuploader) {
                if (iuploader.getStatus() == SUCCESS) {
                    iuploader.getWidget().removeFromParent();
                }
            }
        });
        panel.add(uploader);
    }
}
