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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.HasImportEventHandlers;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ImportEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ImportEventHandler;

//import gwtupload.client.IUploadStatus;
//import gwtupload.client.IUploader;

/**
 * @author Olga Melnichuk
 */
public class ImportFileDialogContent extends Composite implements
        HasCloseHandlers<ImportFileDialogContent>,
        HasImportEventHandlers {

    private static enum DialogState {
        NOT_CONFIRMED() {
            @Override
            void init(ImportFileDialogContent dialogContent) {
                dialogContent.content.setWidget(
                        new Label("Please note that the current data will be overridden with the new file content."));
                dialogContent.okButton.setText("Continue >>");
            }

            @Override
            void proceed(ImportFileDialogContent dialogContent) {
                dialogContent.gotoState(CONFIRMED);
            }
        },
        CONFIRMED() {
            @Override
            void init(final ImportFileDialogContent dialogContent) {
                final Button okButton = dialogContent.okButton;
                okButton.setText("Import");
                okButton.setEnabled(false);

                UploadSingleFilePanel uploadFilePanel = new UploadSingleFilePanel();
//                IUploader.OnCancelUploaderHandler onCancelUploaderHandler = new IUploader.OnCancelUploaderHandler() {
//                    @Override
//                    public void onCancel(IUploader widgets) {
//                        okButton.setEnabled(false);
//                    }
//                };
//
//                IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
//                    @Override
//                    public void onFinish(IUploader uploader) {
//                        if (uploader.getStatus() == IUploadStatus.Status.SUCCESS) {
//                            okButton.setEnabled(true);
//                        }
//                    }
//                };

//                uploadFilePanel.addOnFinishUploadHandler(onFinishUploaderHandler);
//                uploadFilePanel.addOnCancelUploadHandler(onCancelUploaderHandler);

                dialogContent.uploadFilePanel = uploadFilePanel;
                dialogContent.content.setWidget(uploadFilePanel);
            }

            @Override
            void cancel(ImportFileDialogContent dialogContent) {
//                dialogContent.uploadFilePanel.cancel();
            }

            @Override
            void proceed(ImportFileDialogContent dialogContent) {
                dialogContent.gotoState(READY);
            }
        },
        READY() {
            @Override
            void init(final ImportFileDialogContent dialogContent) {
                dialogContent.cancelButton.removeFromParent();
                dialogContent.okButton.setText("Close");
                dialogContent.okButton.setEnabled(false);

                final WaitingPanel waitingPanel = new WaitingPanel("Please wait while the file is importing...");
                dialogContent.content.setWidget(waitingPanel);
                dialogContent.fireImportEvent(new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        waitingPanel.showError(caught);
                        dialogContent.okButton.setEnabled(true);
                        dialogContent.success = false;
                    }

                    @Override
                    public void onSuccess(Void result) {
                        waitingPanel.showSuccess("The file was imported successfully.");
                        dialogContent.okButton.setEnabled(true);
                        dialogContent.success = true;
                    }
                });
            }

            @Override
            void proceed(ImportFileDialogContent dialogContent) {
                dialogContent.fireCloseEvent();
                if (dialogContent.success) {
                    Window.Location.reload();
                }
            }
        };

        abstract void init(ImportFileDialogContent dialogContent);

        void proceed(ImportFileDialogContent dialogContent) {
            // do nothing by default
        }

        void cancel(ImportFileDialogContent dialogContent) {
            // do nothing by default
        }
    }

    private void gotoState(DialogState state) {
        this.state = state;
        state.init(this);
    }

    interface Binder extends UiBinder<Widget, ImportFileDialogContent> {
    }

    @UiField
    SimplePanel content;

    @UiField
    Button okButton;

    @UiField
    Button cancelButton;

    private UploadSingleFilePanel uploadFilePanel;

    private DialogState state = DialogState.NOT_CONFIRMED;

    private boolean success;

    public ImportFileDialogContent() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));

        state.init(this);

        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                state.proceed(ImportFileDialogContent.this);
            }
        });

        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                state.cancel(ImportFileDialogContent.this);
                fireCloseEvent();
            }
        });
    }

    private void fireCloseEvent() {
        CloseEvent.fire(this, this);
    }

    private void fireImportEvent(AsyncCallback<Void> callback) {
        ImportEvent.fire(this, callback);
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<ImportFileDialogContent> handler) {
        return addHandler(handler, CloseEvent.getType());
    }

    @Override
    public HandlerRegistration addImportEventHandler(ImportEventHandler handler) {
        return addHandler(handler, ImportEvent.getType());
    }
}
