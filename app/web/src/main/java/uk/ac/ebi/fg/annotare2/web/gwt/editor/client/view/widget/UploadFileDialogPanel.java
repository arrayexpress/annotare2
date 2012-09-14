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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gwtupload.client.*;

/**
 * @author Olga Melnichuk
 */
public class UploadFileDialogPanel extends Composite {

    interface Binder extends UiBinder<Widget, UploadFileDialogPanel> {
    }

    @UiField
    SimplePanel content;

    @UiField
    Button okButton;

    @UiField
    Button cancelButton;

    public UploadFileDialogPanel() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));

        final MultiUploader uploader = new MultiUploader();
        uploader.getStatusWidget().setI18Constants(new Constants());
        uploader.setMaximumFiles(1);
        uploader.setAutoSubmit(true);

        IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
            public void onFinish(IUploader uploader) {
                if (uploader.getStatus() == IUploadStatus.Status.SUCCESS) {
                    IUploader.UploadedInfo info = uploader.getServerInfo();
                    Window.alert("File name " + info.name + "\n"
                            + "File content-type " + info.ctype + "\n"
                            + "File size " + info.size + "\n"
                            + "Server message " + info.message);
                    okButton.setEnabled(true);
                }
            }
        };

        uploader.addOnFinishUploadHandler(onFinishUploaderHandler);
        content.setWidget(uploader);
        content.setHeight("100px");
        content.setWidth("250px");

        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                uploader.cancel();
            }
        });

        okButton.setEnabled(false);
    }

    public Button getOkButton() {
        return okButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }


    private static class Constants implements IUploadStatus.UploadStatusConstants {
        @Override
        public String uploadLabelCancel() {
            return " ";
        }

        @Override
        public String uploadStatusCanceled() {
            return "Canceled";
        }

        @Override
        public String uploadStatusCanceling() {
            return "Canceling...";
        }

        @Override
        public String uploadStatusDeleted() {
            return "Deleted";
        }

        @Override
        public String uploadStatusError() {
            return "Error";
        }

        @Override
        public String uploadStatusInProgress() {
            return "In progress";
        }

        @Override
        public String uploadStatusQueued() {
            return "Queued";
        }

        @Override
        public String uploadStatusSubmitting() {
            return "Submitting...";
        }

        @Override
        public String uploadStatusSuccess() {
            return "100%";
        }
    }
}
