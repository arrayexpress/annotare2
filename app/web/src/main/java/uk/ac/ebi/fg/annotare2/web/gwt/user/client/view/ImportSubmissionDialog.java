/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DataFilesUploadView;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.WaitingPopup;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;

import java.util.List;

public class ImportSubmissionDialog extends DialogBox {

    @UiField
    DataFilesUploadView dataFilesUploadView;

    @UiField
    DeckLayoutPanel deckPanel;

    @UiField
    Label validationOutcomeLabel;

    @UiField
    HTML validationLogHtml;

    @UiField
    RadioButton rbScore1;

    @UiField
    RadioButton rbScore2;

    @UiField
    RadioButton rbScore3;

    @UiField
    RadioButton rbScore4;

    @UiField
    RadioButton rbScore5;

    @UiField
    RadioButton rbScore6;

    @UiField
    RadioButton rbScore7;

    @UiField
    RadioButton rbScore8;

    @UiField
    RadioButton rbScore9;

    @UiField
    TextArea feedbackMessage;

    final WaitingPopup waitingPopup;

    interface Binder extends UiBinder<Widget, ImportSubmissionDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    private Presenter presenter;

    public ImportSubmissionDialog() {
        waitingPopup = new WaitingPopup();

        setModal(true);
        setGlassEnabled(true);
        setText("Import Experiment Submission");

        setWidget(Binder.BINDER.createAndBindUi(this));
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        dataFilesUploadView.setPresenter(presenter);
    }

    public void setDataFiles(List<DataFileRow> files) {
        dataFilesUploadView.setDataFiles(files);
    }

    public void startImport() {
        deckPanel.showWidget(0);
        deckPanel.setWidth(Math.floor(Window.getClientWidth() * 0.8) + "px");
        deckPanel.setHeight(Math.floor(Window.getClientHeight() * 0.8) + "px");
        center();
    }

    @UiHandler("cancel1Button")
    void onCancelClick(ClickEvent event) {
        hide();
        presenter.onImportCancelled();
    }

    @UiHandler("cancel2Button")
    void onCancel2Click(ClickEvent event) {
        onCancelClick(event);
    }

    @UiHandler("cancel3Button")
    void onCancel3Click(ClickEvent event) {
        onCancelClick(event);
    }

    @UiHandler("submitButton")
    void onSubmitClick(ClickEvent event) {
        validateAndSubmit();
    }

    @UiHandler("backButton")
    void onBackButton(ClickEvent event) {
        deckPanel.showWidget(0);
    }

    @UiHandler("okButton")
    void onOkButton(ClickEvent event) {
        if (null != getFeedbackScore() || !getFeedbackMessage().isEmpty()) {
            presenter.onPostFeedback(getFeedbackScore(), getFeedbackMessage());
        }
    }

    public Byte getFeedbackScore() {
        if (rbScore1.getValue()) {
            return 1;
        } else if (rbScore2.getValue()) {
            return 2;
        } else if (rbScore3.getValue()) {
            return 3;
        } else if (rbScore4.getValue()) {
            return 4;
        } else if (rbScore5.getValue()) {
            return 5;
        } else if (rbScore6.getValue()) {
            return 6;
        } else if (rbScore7.getValue()) {
            return 7;
        } else if (rbScore8.getValue()) {
            return 8;
        } else if (rbScore9.getValue()) {
            return 9;
        } else {
            return null;
        }
    }

    public String getFeedbackMessage() {
        return feedbackMessage.getValue().trim();
    }

    @Override
    protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
        super.onPreviewNativeEvent(event);
        if (Event.ONKEYDOWN == event.getTypeInt()) {
            if (KeyCodes.KEY_ESCAPE == event.getNativeEvent().getKeyCode()) {
                onCancelClick(null);
            }
        }
    }

    private void validateAndSubmit() {
        waitingPopup.center();

        presenter.onImportValidate(
                new ReportingAsyncCallback<ValidationResult>(ReportingAsyncCallback.FailureMessage.GENERIC_FAILURE) {
                    @Override
                    public void onFailure(Throwable caught) {
                        waitingPopup.hide();
                        showValidationResult(new ValidationResult(caught));
                    }

                    @Override
                    public void onSuccess(ValidationResult result) {
                        if (result.canSubmit()) {
                            submit();
                        } else {
                            waitingPopup.hide();
                            showValidationResult(result);
                        }
                    }
                }
        );
    }

    private void submit() {
        presenter.onImportSubmit(
                new ReportingAsyncCallback<Void>(FailureMessage.GENERIC_FAILURE) {

                    @Override
                    public void onFailure(Throwable caught) {
                        waitingPopup.hide();
                    }

                    @Override
                    public void onSuccess(Void result) {
                        waitingPopup.hide();
                        deckPanel.showWidget(2);
                    }
                });
    }

    private void showValidationResult(ValidationResult result) {
        validationOutcomeLabel.setText("");
        if (result.getFailures().isEmpty()) {
            if (result.getErrors().isEmpty()) {
                validationOutcomeLabel.setText("Validation has been successful.");
            } else {
                validationOutcomeLabel.setText(("Validation failed with " + result.getErrors().size() + " errors:"));
                showValidationLog(result.getErrors());
            }
        } else {
            validationOutcomeLabel.setText("There was a software problem validating this submission.");
        }
        deckPanel.showWidget(1);
    }

    private void showValidationLog(List<String> list) {
        StringBuilder html = new StringBuilder();
        for (String item : list) {
            html.append(item).append("<br/>");
        }
        validationLogHtml.setHTML(html.toString());
    }

    public interface Presenter extends DataFilesUploadView.Presenter {

        void onImportCancelled();

        void onImportValidate(AsyncCallback<ValidationResult> callback);

        void onImportSubmit(AsyncCallback<Void> callback);

        void onPostFeedback(Byte score, String comment);
    }
}
