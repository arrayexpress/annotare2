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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.*;

/**
 * @author Olga Melnichuk
 */
public class EditorTitleBarViewImpl extends Composite implements EditorTitleBarView {

    //private static final String CONFIRMATION_MESSAGE = "Please note that the all data of the submission will be lost. Do you want to continue?";

    interface Binder extends UiBinder<HTMLPanel, EditorTitleBarViewImpl> {
    }

    @UiField
    Label accessionLabel;

    @UiField
    Button helpButton;

    @UiField
    Button feedbackButton;

    @UiField
    Button validateButton;

    @UiField
    Button submitButton;

    @UiField
    AutoSaveLabel autoSaveLabel;

    //@UiField
    //Anchor importLink;

    //@UiField
    //Anchor exportLink;

    private Presenter presenter;

    private final FeedbackDialog feedbackDialog;
    private final WaitingPopup waitingPopup;

    public EditorTitleBarViewImpl() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));
        feedbackDialog = new FeedbackDialog();
        waitingPopup = new WaitingPopup();
    }

    @Override
    public void setTitle(SubmissionType type, String accession) {
        accessionLabel.setText(type.getTitle() + ": " + accession);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        feedbackDialog.setPresenter(presenter);
    }

    @Override
    public void setSubmissionType(SubmissionType type) {
        boolean isExperimentSubmission = type.isExperimentSubmission();
        validateButton.setVisible(isExperimentSubmission);
        //exportLink.setVisible(isExperimentSubmission);
        //importLink.setVisible(!isExperimentSubmission);
    }

    @Override
    public void autoSaveStarted() {
        autoSaveLabel.show("Saving...");
        autoSaveLabel.getElement().getStyle().setColor("inherit");
    }

    @Override
    public void autoSaveStopped(String errorMessage) {
        if (errorMessage == null) {
            autoSaveLabel.hide();
        } else {
            autoSaveLabel.show(errorMessage);
            autoSaveLabel.getElement().getStyle().setColor("red");
        }
    }

    @Override
    public void criticalUpdateStarted() {
        if (!waitingPopup.isShowing()) {
            waitingPopup.center();
        }
    }

    @Override
    public void criticalUpdateStopped() {
        if (waitingPopup.isShowing()) {
            waitingPopup.hide();
        }
    }

    @UiHandler("helpButton")
    void onHelpButtonClick(ClickEvent event) {
        Window.open("http://www.ebi.ac.uk/fgpt/annotare_help/", "_blank", "");
    }

    @UiHandler("feedbackButton")
    void onFeedbackButtonClick(ClickEvent event) {
        feedbackDialog.center();
    }

    @UiHandler("validateButton")
    void onValidateButtonClick(ClickEvent event) {
        final ValidateSubmissionDialog dialog = new ValidateSubmissionDialog();
        dialog.showSubmissionProgressMessage(null);

        presenter.validateSubmission(new ValidationHandler() {

            @Override
            public void onFailure() {
                dialog.showValidationFailureMessage(null);
            }

            @Override
            public void onSuccess(ValidationResult result) {
                if (result.getErrors().size() > 0 || result.getFailures().size() > 0) {
                    dialog.showValidationFailureMessage(null);
                } else {
                    dialog.hide();
                }
            }
        });
    }

    @UiHandler("submitButton")
    void onSubmitButtonClick(ClickEvent event) {
        final ValidateSubmissionDialog dialog = new ValidateSubmissionDialog();
        presenter.validateSubmission(new ValidationHandler() {

            @Override
            public void onFailure() {
                dialog.showValidationFailureMessage(null);
            }

            @Override
            public void onSuccess(ValidationResult result) {
                if (result.getErrors().size() > 0 || result.getFailures().size() > 0) {
                    dialog.showValidationFailureMessage(null);
                } else {
                    dialog.showSubmissionProgressMessage(null);
                    presenter.submitSubmission(new SubmissionHandler() {

                        @Override
                        public void onFailure() {
                            dialog.showSubmissionFailureMessage(null);
                        }

                        @Override
                        public void onSuccess() {
                            dialog.showSubmissionSuccessMessage(new DialogCallback<Void>() {
                                @Override
                                public void onOkay(Void aVoid) {
                                    if (null != dialog.getFeedbackScore() || !dialog.getFeedbackMessage().isEmpty()) {
                                        presenter.postFeedback(dialog.getFeedbackScore(), dialog.getFeedbackMessage());
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    //@UiHandler("importLink")
    //void onImportLinkClick(ClickEvent event) {
    //    if (confirm(CONFIRMATION_MESSAGE)) {
    //        ImportFileDialog importFileDialog = new ImportFileDialog("Array Design Import");
    //        importFileDialog.addImportEventHandler(new ImportEventHandler() {
    //            @Override
    //            public void onImport(AsyncCallback<Void> callback) {
    //                presenter.importFile(callback);
    //            }
    //        });
    //        importFileDialog.show();
    //    }
    //}

    //@UiHandler("exportLink")
    //void onExportLinkClick(ClickEvent event) {
    //    if (presenter != null) {
    //        Window.open(presenter.getSubmissionExportUrl(), "export", "");
    //    }
    //}
}
