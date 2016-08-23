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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import uk.ac.ebi.fg.annotare2.db.model.enums.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.utils.Urls;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DialogCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.WaitingPopup;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.AutoSaveLabel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ContactUsDialog;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ValidateSubmissionDialog;

/**
 * @author Olga Melnichuk
 */
public class EditorTitleBarViewImpl extends Composite implements EditorTitleBarView {

    private SubmissionDetails submissionDetails;

    interface Binder extends UiBinder<HTMLPanel, EditorTitleBarViewImpl> {
    }

    @UiField
    Label accessionLabel;

    @UiField
    Button contactButton;

    @UiField
    Button editButton;

    @UiField
    Button releaseButton;

    @UiField
    Button exportButton;

    @UiField
    Button validateButton;

    @UiField
    Button submitButton;

    @UiField
    AutoSaveLabel autoSaveLabel;

    private Presenter presenter;

    private boolean shouldAllowInstantFeedback;
    private boolean isCurator;
    private boolean isOwnedByCreator;

    private final ContactUsDialog contactUsDialog;
    private final WaitingPopup waitingPopup;

    public EditorTitleBarViewImpl() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));
        contactUsDialog = new ContactUsDialog();
        waitingPopup = new WaitingPopup();
    }

    public void reloadSubmission() {
        Scheduler.get().scheduleDeferred(new Command() {
            public void execute() {
                Window.Location.reload();
            }
        });
    }

    @Override
    public void setCurator(boolean isCurator) {
        this.isCurator = isCurator;
        if (isCurator) {
            editButton.setVisible(true);
            releaseButton.setVisible(true);
        } else {
            contactButton.setVisible(true);
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        contactUsDialog.setPresenter(presenter);
    }

    @Override
    public void setSubmissionDetails(SubmissionDetails submissionDetails) {
        this.submissionDetails = submissionDetails;
        // set title
        accessionLabel.setText(submissionDetails.getAccession().getText());

        //set owned by creator
        isOwnedByCreator = submissionDetails.isOwnedByCreator();
        editButton.setVisible(editButton.isVisible() && isOwnedByCreator);
        releaseButton.setVisible(releaseButton.isVisible() && !isOwnedByCreator);

        //set status and type
        SubmissionStatus status = submissionDetails.getStatus();
        submitButton.setVisible(status.canSubmit(isCurator));
        validateButton.setVisible(submissionDetails.getType().isExperiment() && status.canSubmit(isCurator) );
        exportButton.setVisible(submissionDetails.getType().isExperiment());
        editButton.setVisible(editButton.isVisible() && status.canAssign());
        releaseButton.setVisible(releaseButton.isVisible() && status.canAssign());
        shouldAllowInstantFeedback = (SubmissionStatus.IN_PROGRESS == status);


    }


    @Override
    public void autoSaveStarted() {
        autoSaveLabel.show("Saving...");
    }

    @Override
    public void autoSaveStopped(String errorMessage) {
        autoSaveLabel.hide();
        if (null != errorMessage) {
            NotificationPopupPanel.error(errorMessage, true, false);
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
        Window.open(Urls.getContextUrl() + "help/", "_blank", "");
    }

    @UiHandler("contactButton")
    void onFeedbackButtonClick(ClickEvent event) {
        contactUsDialog.center();
    }

    @UiHandler("exportButton")
    void onExportLinkClick(ClickEvent event) {
        if (presenter != null) {
            Window.open(presenter.getSubmissionExportUrl(), "_blank", "");
        }
    }

    @UiHandler("editButton")
    void onEditButtonClick(ClickEvent event) {
        presenter.assignSubmissionToMe(new ReportingAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                reloadSubmission();
            }
        });
    }

    @UiHandler("releaseButton")
    void onReleaseButtonClick(ClickEvent event) {
        presenter.assignSubmissionToCreator(new ReportingAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                reloadSubmission();
            }
        });
    }

    @UiHandler("validateButton")
    void onValidateButtonClick(ClickEvent event) {
        final ValidateSubmissionDialog dialog = new ValidateSubmissionDialog();
        dialog.showValidationProgressMessage(null);

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
        dialog.showValidationProgressMessage(null);

        presenter.validateSubmission(new ValidationHandler() {
            @Override
            public void onFailure() {
                onValidationFailure(dialog);
            }

            @Override
            public void onSuccess(ValidationResult result) {
                if (!result.canSubmit()) {
                    onValidationFailure(dialog);
                } else {
                    processSubmission(dialog, shouldAllowInstantFeedback);
                }
            }
        });
    }

    void onValidationFailure(final ValidateSubmissionDialog dialog) {
        if (isCurator) {
            dialog.showValidationFailureWarning(new DialogCallback<Void>() {
                @Override
                public boolean onOk(Void aVoid) {
                    if (isOwnedByCreator) {
                        presenter.assignSubmissionToMe(new ReportingAsyncCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                processSubmission(dialog, false);
                            }
                        });
                    } else {
                        processSubmission(dialog, false);
                    }
                    return true;
                }
            });
        } else {
            dialog.showValidationFailureMessage(null);
        }
    }

    void processSubmission(final ValidateSubmissionDialog dialog, final boolean doFeedback) {
        dialog.showSubmissionProgressMessage(null);
        presenter.submitSubmission(new SubmissionHandler() {
            @Override
            public void onFailure() {
                dialog.showSubmissionFailureMessage(null);
            }

            @Override
            public void onSuccess() {
                submitButton.setEnabled(false);
                dialog.showSubmissionSuccessMessage(new DialogCallback<Void>() {
                    @Override
                    public void onCancel() {
                        reloadSubmission();
                    }
                    @Override
                    public boolean onOk(Void aVoid) {
                        if (null != dialog.getFeedbackScore() || !dialog.getFeedbackMessage().isEmpty()) {
                            presenter.postFeedback(
                                    dialog.getFeedbackScore(),
                                    dialog.getFeedbackMessage(),
                                    new ReportingAsyncCallback<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            reloadSubmission();
                                        }
                            });
                        } else {
                            reloadSubmission();
                        }
                        return true;
                    }
                }, doFeedback);
            }
        });
    }
}
