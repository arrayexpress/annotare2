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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploader;
import gwtupload.client.MultiUploader;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ImportFileEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ImportFileEventHandler;

/**
 * @author Olga Melnichuk
 */
public class ImportFileDialogContent extends Composite {

    interface Binder extends UiBinder<Widget, ImportFileDialogContent> {
    }

    @UiField
    SimplePanel content;

    @UiField
    Button okButton;

    @UiField
    Button cancelButton;

    private MultiUploader uploader;

    private boolean confirmed;

    private String fileName;

    public ImportFileDialogContent() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));

        if (!confirmed) {
            content.setWidget(
                    new Label("Please, note that the data will be overridden with the file contents."));
            okButton.setText("Continue >>");
        }

        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!confirmed) {
                    doConfirm();
                } else {
                    doImport();
                }
            }
        });

        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doCancel();
            }
        });
    }

    private void doConfirm() {
        confirmed = true;
        okButton.setText("Import");
        okButton.setEnabled(false);

        final MultiUploader uploader = new MultiUploader();
        uploader.setStatusWidget(new UploadStatus());
        uploader.setMaximumFiles(1);
        uploader.setAutoSubmit(true);

        IUploader.OnCancelUploaderHandler onCancelUploaderHandler = new IUploader.OnCancelUploaderHandler() {
            @Override
            public void onCancel(IUploader widgets) {
                okButton.setEnabled(false);
            }
        };

        IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
            @Override
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
        uploader.addOnCancelUploadHandler(onCancelUploaderHandler);

        content.setWidget(uploader);
    }

    private void doCancel() {
        if (uploader != null) {
            uploader.cancel();
        }
        fireEvent(ImportFileEvent.importCancelled());
    }

    private void doImport() {
        fireEvent(ImportFileEvent.importFile(fileName));
    }

    public HandlerRegistration addImportFileHandler(ImportFileEventHandler handler) {
        return addHandler(handler, ImportFileEvent.TYPE);
    }
}
