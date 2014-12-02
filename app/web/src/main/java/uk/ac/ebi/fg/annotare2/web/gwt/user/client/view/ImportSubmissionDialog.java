/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
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

    interface Binder extends UiBinder<Widget, ImportSubmissionDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    private Presenter presenter;

    public ImportSubmissionDialog() {
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
        final PopupPanel w = new WaitingPopup();
        w.center();

        presenter.onImportSubmit(
                new ReportingAsyncCallback<ValidationResult>(ReportingAsyncCallback.FailureMessage.GENERIC_FAILURE) {
                    @Override
                    public void onFailure(Throwable caught) {
                        w.hide();
                        showValidationResult(new ValidationResult(caught));
                    }

                    @Override
                    public void onSuccess(ValidationResult result) {
                        w.hide();
                        showValidationResult(result);
                    }
                }
        );
    }

    @UiHandler("backButton")
    void onBackButton(ClickEvent event) {
        deckPanel.showWidget(0);
    }

    @UiHandler("closeButton")
    void onCloseClick(ClickEvent event) {
        hide();
        presenter.onImportCancelled();
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

        void onImportSubmit(AsyncCallback<ValidationResult> callback);
    }
}
