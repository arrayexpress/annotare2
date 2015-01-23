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
import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;
import uk.ac.ebi.fg.annotare2.submission.model.ImportedExperimentProfile;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DataFilesUploadView;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.WaitingPopup;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;

import java.util.Date;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.web.gwt.common.client.utils.DatesTimes.dateTimeFormat;
import static uk.ac.ebi.fg.annotare2.web.gwt.common.client.utils.DatesTimes.dateTimeFormatPlaceholder;

public class ImportSubmissionDialog extends DialogBox {

    @UiField
    DataFilesUploadView dataFilesUploadView;

    @UiField
    DeckLayoutPanel deckPanel;

    @UiField
    TextArea title;

    @UiField
    TextArea description;

    @UiField
    ListBox aeExperimentType;

    @UiField
    DateBox releaseDate;

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

        DateBox.DefaultFormat format = new DateBox.DefaultFormat(dateTimeFormat());

        releaseDate.setFormat(format);
        releaseDate.getElement().setPropertyString("placeholder", dateTimeFormatPlaceholder());
        releaseDate.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>()
        {
            @Override
            public void onShowRange(final ShowRangeEvent<Date> event)
            {
                final Date today = today();
                Date d = zeroTime(event.getStart());
                final long endTime = event.getEnd().getTime();
                while (d.before(today) && d.getTime() <= endTime)
                {
                    releaseDate.getDatePicker().setTransientEnabledOnDates(false, d);
                    d = nextDay(d);
                }
            }
        });
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        dataFilesUploadView.setPresenter(presenter);
    }

    public void setDataFiles(List<DataFileRow> files) {
        dataFilesUploadView.setDataFiles(files);
    }

    public void setAeExperimentTypeOptions(List<String> options) {
        aeExperimentType.clear();
        aeExperimentType.addItem("");
        for (String option : options) {
            aeExperimentType.addItem(option);
        }
    }

    public void startImport() {
        showDeckPanel(Panels.FILE_UPLOAD);
        deckPanel.setWidth(Math.floor(Window.getClientWidth() * 0.7) + "px");
        deckPanel.setHeight(Math.floor(Window.getClientHeight() * 0.7) + "px");
        center();
    }

    @UiHandler("cancel1Button")
    void onCancelClick(ClickEvent event) {
        hide();
        presenter.cancelImport();
    }

    @UiHandler("cancel2Button")
    void onCancel2Click(ClickEvent event) {
        onCancelClick(event);
    }

    @UiHandler("cancel3Button")
    void onCancel3Click(ClickEvent event) {
        onCancelClick(event);
    }

    @UiHandler("startImportButton")
    void onProceedClick(ClickEvent event) {
        getSubmissionProfile();
    }

    @UiHandler("submitButton")
    void onSubmitClick(ClickEvent event) {
        validateAndSubmit();
    }

    @UiHandler("back1Button")
    void onBack1Button(ClickEvent event) {
        showDeckPanel(Panels.FILE_UPLOAD);
    }

    @UiHandler("back2Button")
    void onBack2Button(ClickEvent event) {
        showDeckPanel(Panels.SUBMISSION_DETAILS);
    }

    @UiHandler("okButton")
    void onOkButton(ClickEvent event) {
        if (null != getFeedbackScore() || !getFeedbackMessage().isEmpty()) {
            presenter.postFeedback(getFeedbackScore(), getFeedbackMessage());
        }
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

    private void getSubmissionProfile() {
        waitingPopup.center();

        presenter.getSubmissionProfile(
                new AsyncCallback<ImportedExperimentProfile>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        waitingPopup.hide();
                        NotificationPopupPanel.error(caught.getMessage(), false);
                    }

                    @Override
                    public void onSuccess(ImportedExperimentProfile result) {
                        waitingPopup.hide();
                        populateSubmissionDetails(result);
                        showDeckPanel(Panels.SUBMISSION_DETAILS);
                    }
                }
        );
    }

    private void validateAndSubmit() {
        waitingPopup.center();

        presenter.validateImport(
                new ReportingAsyncCallback<ValidationResult>(FailureMessage.GENERIC_FAILURE) {
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
        presenter.submitImport(
                new ReportingAsyncCallback<Void>(FailureMessage.GENERIC_FAILURE) {

                    @Override
                    public void onFailure(Throwable caught) {
                        waitingPopup.hide();
                    }

                    @Override
                    public void onSuccess(Void result) {
                        waitingPopup.hide();
                        showDeckPanel(Panels.SUBMISSION_CONFIRMATION);
                    }
                });
    }

    private void populateSubmissionDetails(ImportedExperimentProfile details) {
        title.setValue(details.getTitle());
        description.setValue(details.getDescription());
        setAeExperimentType(details.getAeExperimentType());
        releaseDate.setValue(details.getPublicReleaseDate());
    }

    private String getAeExperimentType() {
        int index = aeExperimentType.getSelectedIndex();
        if (index == 0) {
            return null;
        }
        return aeExperimentType.getValue(index);
    }

    private void setAeExperimentType(String type) {
        for (int i = 0; i < aeExperimentType.getItemCount(); i++) {
            String value = aeExperimentType.getValue(i);
            if (value.equals(type)) {
                aeExperimentType.setSelectedIndex(i);
                return;
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
        showDeckPanel(Panels.VALIDATION_ERRORS);
    }

    private void showValidationLog(List<String> list) {
        StringBuilder html = new StringBuilder();
        for (String item : list) {
            html.append(item).append("<br/>");
        }
        validationLogHtml.setHTML(html.toString());
    }

    private Byte getFeedbackScore() {
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

    private String getFeedbackMessage() {
        return feedbackMessage.getValue().trim();
    }

    private static Date today()
    {
        return zeroTime(new Date());
    }

    private static Date zeroTime(final Date date)
    {
        return new Date(date.getYear(),date.getMonth(),date.getDate());
    }

    private static Date nextDay(final Date date)
    {
        CalendarUtil.addDaysToDate(date, 1);
        return date;
    }

    enum Panels {
        FILE_UPLOAD, SUBMISSION_DETAILS, VALIDATION_ERRORS, SUBMISSION_CONFIRMATION
    }

    private void showDeckPanel(Panels panel) {
        deckPanel.showWidget(panel.ordinal());
    }

    public interface Presenter extends DataFilesUploadView.Presenter {

        void cancelImport();

        void getSubmissionProfile(AsyncCallback<ImportedExperimentProfile> callback);

        void validateImport(AsyncCallback<ValidationResult> callback);

        void submitImport(AsyncCallback<Void> callback);

        void postFeedback(Byte score, String comment);
    }
}
