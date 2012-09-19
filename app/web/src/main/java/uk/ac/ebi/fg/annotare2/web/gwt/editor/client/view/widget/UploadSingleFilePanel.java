/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploader;
import gwtupload.client.MultiUploader;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Olga Melnichuk
 */
public class UploadSingleFilePanel extends Composite {

    interface Binder extends UiBinder<Widget, UploadSingleFilePanel> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    SimplePanel placeholder;

    private MultiUploader uploader;

    public UploadSingleFilePanel() {
        initWidget(Binder.BINDER.createAndBindUi(this));

        uploader = new MultiUploader();
        Set<IUploadStatus.CancelBehavior> cancelBehaviors = new HashSet<IUploadStatus.CancelBehavior>();
        cancelBehaviors.add(IUploadStatus.CancelBehavior.REMOVE_CANCELLED_FROM_LIST);
        cancelBehaviors.add(IUploadStatus.CancelBehavior.STOP_CURRENT);
        cancelBehaviors.add(IUploadStatus.CancelBehavior.REMOVE_REMOTE);

        UploadStatus status = new UploadStatus();
        status.setCancelConfiguration(cancelBehaviors);
        uploader.setStatusWidget(status);
        uploader.setMaximumFiles(1);
        uploader.setAutoSubmit(true);
        placeholder.setWidget(uploader);
    }

    public void addOnFinishUploadHandler(IUploader.OnFinishUploaderHandler onFinishUploaderHandler) {
        uploader.addOnFinishUploadHandler(onFinishUploaderHandler);
    }

    public void addOnCancelUploadHandler(IUploader.OnCancelUploaderHandler onCancelUploaderHandler) {
        uploader.addOnCancelUploadHandler(onCancelUploaderHandler);
    }

    public void cancel() {
        uploader.cancel();
    }
}
