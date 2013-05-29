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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionType;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ImportEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.AutoSaveLabel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ImportFileDialog;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ValidateSubmissionDialog;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.WaitingPopup;

/**
 * @author Olga Melnichuk
 */
public class EditorTitleBarViewImpl extends Composite implements EditorTitleBarView {

    public static final String CONFIRMATION_MESSAGE = "Please note that the all data of the submission will be lost. Do you want to continue?";

    interface Binder extends UiBinder<HTMLPanel, EditorTitleBarViewImpl> {
    }

    @UiField
    Label accessionLabel;

    @UiField
    Button validateButton;

    @UiField
    Anchor createNewLink;

    @UiField
    Anchor importLink;

    @UiField
    AutoSaveLabel autoSaveLabel;

    private Presenter presenter;

    private WaitingPopup criticalUpdatePopup;

    public EditorTitleBarViewImpl() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setTitle(SubmissionType type, String accession) {
        accessionLabel.setText(type.getTitle() + ": " + accession);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setSubmissionType(SubmissionType type) {
        boolean isExperimentSubmission = type.isExperimentSubmission();
        validateButton.setVisible(isExperimentSubmission);
        createNewLink.setVisible(isExperimentSubmission);
        importLink.setVisible(!isExperimentSubmission);
    }

    @Override
    public void autoSaveStarted() {
        autoSaveLabel.show("Saving...");
    }

    @Override
    public void autoSaveStopped(Throwable caught) {
        if (caught == null) {
            autoSaveLabel.hide();
        } else {
            autoSaveLabel.show("Can't save changes; unexpected server error");
        }
    }

    @Override
    public void criticalUpdateStarted() {
        if (criticalUpdatePopup == null) {
            criticalUpdatePopup = new WaitingPopup("Sending critical updates; please wait..");
            criticalUpdatePopup.positionAtWindowCenter();
            criticalUpdatePopup.setGlassEnabled(true);
        } else if (!criticalUpdatePopup.isShowing()) {
            criticalUpdatePopup.show();
        }
    }

    @Override
    public void criticalUpdateStopped() {
        if (criticalUpdatePopup != null && criticalUpdatePopup.isShowing()) {
            criticalUpdatePopup.hide();
        }
    }

    @UiHandler("validateButton")
    public void onValidateButtonClick(ClickEvent event) {
        final ValidateSubmissionDialog dialog = new ValidateSubmissionDialog();
        presenter.validateSubmission(new ValidationHandler() {

            @Override
            public void onValidationFinished() {
                dialog.hide();
                //TODO show success/error/failure message ?
            }

        });
    }

    @UiHandler("createNewLink")
    public void onCreateLinkClick(ClickEvent event) {
        if (Window.confirm(CONFIRMATION_MESSAGE)) {
            final WaitingPopup popup = new WaitingPopup("Creating new submission, please wait...");
            popup.positionAtWindowCenter();
            presenter.discardSubmissionData(new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                    popup.showError(caught);
                }

                @Override
                public void onSuccess(Void result) {
                    Window.Location.reload();
                }
            });
        }
    }

    @UiHandler("importLink")
    public void onImportLinkClick(ClickEvent event) {
        if (Window.confirm(CONFIRMATION_MESSAGE)) {
            ImportFileDialog importFileDialog = new ImportFileDialog("Array Design Import");
            importFileDialog.addImportEventHandler(new ImportEventHandler() {
                @Override
                public void onImport(AsyncCallback<Void> callback) {
                    presenter.importFile(callback);
                }
            });
            importFileDialog.show();
        }
    }
}
