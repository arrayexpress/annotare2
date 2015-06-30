/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DialogCallback;


/**
 * @author Olga Melnichuk
 */
public class ValidateSubmissionDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, ValidateSubmissionDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

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

    private DialogCallback<Void> callback;

    public ValidateSubmissionDialog() {
        setModal(true);
        setGlassEnabled(true);

        setWidget(Binder.BINDER.createAndBindUi(this));
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
            callback.onOkay(null);
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
        setTitleAndMessage(
                "Submission Successful",
                "The experiment has been successfully submitted to ArrayExpress.<br><br>" +
                        "Our curation team will review your submission and will email you with any questions. " +
                        "Once all the required information is provided we will send you an accession number.<br><br>" +
                        "In the meantime, please contact <a href=\"mailto:annotare@ebi.ac.uk\">annotare@ebi.ac.uk</a> with any questions. " +
                        "Further information can be found at <a href=\"http://www.ebi.ac.uk/fgpt/annotare_help/submit_exp.html\" target=\"_blank\">Annotare help</a>.");
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
}
