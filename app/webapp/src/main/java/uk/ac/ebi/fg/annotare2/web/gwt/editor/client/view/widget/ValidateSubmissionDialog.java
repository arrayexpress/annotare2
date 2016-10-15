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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DialogCallback;


/**
 * @author Olga Melnichuk
 */
public class ValidateSubmissionDialog extends DialogBox {

    @UiField
    HTML html;
    @UiField
    HTMLPanel feedbackPanel;
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
    TextArea message;
    @UiField
    Button cancelButton;
    @UiField
    Button okButton;
    @UiField
    RadioButton rbJournal;
    @UiField
    RadioButton rbSearch;
    @UiField
    RadioButton rbArrayExpress;
    @UiField
    RadioButton rbWordOfMouth;
    @UiField
    RadioButton rbOther;
    @UiField
    TextBox tbOther;
    @UiField
    HTMLPanel pnlReferrer;

    private DialogCallback<Void> callback;
    private ExperimentProfileType experimentProfileType;

    public ValidateSubmissionDialog(ExperimentProfileType experimentProfileType, boolean askForReferrer) {
        setModal(true);
        setGlassEnabled(true);
        setWidget(Binder.BINDER.createAndBindUi(this));
        this.pnlReferrer.setVisible(askForReferrer);
        this.experimentProfileType = experimentProfileType;
        this.tbOther.getElement().setPropertyString("placeholder","Please specify");
        this.tbOther.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent keyDownEvent) {
                rbOther.setValue(true);
            }
        });
    }

    @UiHandler("cancelButton")
    void cancelButtonClicked(ClickEvent event) {
        hide();
        if (null != callback) {
            callback.onCancel();
        }
    }

    @UiHandler("okButton")
    void okButtonClicked(ClickEvent event) {
        hide();
        if (null != callback) {
            callback.onOk(null);
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
        return message.getValue().trim();
    }

    public String getReferrer() {
        if (rbJournal.getValue()) return "Journal";
        if (rbArrayExpress.getValue()) return "ArrayExpress";
        if (rbWordOfMouth.getValue()) return "Word of Mouth";
        if (rbSearch.getValue()) return "Search Engine";
        return tbOther.getValue();
    }

    private void setTitleAndMessage(String title, String message) {
        setText(title);
        html.setHTML(message);
        center();
    }

    public void showValidationProgressMessage(DialogCallback<Void> callback) {
        this.callback = callback;
        setTitleAndMessage(
                "Validating...",
                "Please wait while the submission is being validated"
        );
    }

    public void showValidationFailureMessage(DialogCallback<Void> callback) {
        this.callback = callback;
        setTitleAndMessage(
                "Validation Failed",
                "Validation has failed. " +
                        "Please see the validation log for more information. " +
                        "Errors must be fixed before submission."
        );
    }

    public void showValidationFailureWarning(DialogCallback<Void> callback) {
        this.callback = callback;
        cancelButton.setVisible(true);
        setTitleAndMessage(
                "Validation Failed",
                "Validation has failed. " +
                        "As a curator you can press <b>OK</b> if you wish to submit," +
                        " or <b>Cancel</b> if you wish to review validation logs and amend the submission."
        );
    }

    public void showSubmissionProgressMessage(DialogCallback<Void> callback) {
        this.callback = callback;
        setTitleAndMessage(
                "Submitting...",
                "Please wait as the submission is being processed."
        );
    }

    public void showSubmissionFailureMessage(DialogCallback<Void> callback) {
        this.callback = callback;
        setTitleAndMessage(
                "Submission Failed",
                "There was a problem submitting the experiment to ArrayExpress. " +
                        "Please try again or contact us at <a href=\"mailto:annotare@ebi.ac.uk\">annotare@ebi.ac.uk</a>.");
    }

    public void showSubmissionSuccessMessage(DialogCallback<Void> callback, boolean shouldShowFeedback) {
        this.callback = callback;
        feedbackPanel.setVisible(shouldShowFeedback);
        cancelButton.setVisible(shouldShowFeedback);
        if (this.experimentProfileType != null && this.experimentProfileType.isSequencing()) {
            setTitleAndMessage(
                    "Submission Successful",
                    "Thanks for submitting!.<br/><br/>" +
                            "You'll receive a stable accession number shortly for this submission." +
                            "The accession can be  <a href=\"http://www.ebi.ac.uk/arrayexpress/help/FAQ.html#cite\" " +
                            "target=\"_blank\">cited</a> " +
                            "in your manuscript, but is not valid until a curator has checked the raw data files, " +
                            "reviewed your submission and loaded it " +
                            "into the ArrayExpress database. <br/><br/>" +
                            "We will start checking the content of your raw data files as soon as possible. Sometimes " +
                            "this can take a few days, due to the sheer volume of data; please bear with us. If we " +
                            "detect problems with the files, we will provide information on how to fix the problems, " +
                            "and invite you to resubmit with valid files.");
        } else {
            setTitleAndMessage(
                    "Submission Successful",
                    "Thanks for submitting!.<br/><br/>" +
                            "You'll receive a stable accession number shortly for this submission." +
                            "The accession can be  <a href=\"http://www.ebi.ac.uk/arrayexpress/help/FAQ.html#cite\" " +
                            "target=\"_blank\">cited</a> " +
                            "in your manuscript, but is not valid until a curator has reviewed your submission and loaded it " +
                            "into the ArrayExpress database. Curators will contact you if they have questions about your " +
                            "submission.");
        }
    }

    @Override
    protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
        super.onPreviewNativeEvent(event);
        if (Event.ONKEYDOWN == event.getTypeInt()) {
            if (KeyCodes.KEY_ESCAPE == event.getNativeEvent().getKeyCode()) {
                cancelButtonClicked(null);
            }
        }
    }

    interface Binder extends UiBinder<Widget, ValidateSubmissionDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

}
