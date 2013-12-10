/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;


/**
 * @author Olga Melnichuk
 */
public class ValidateSubmissionDialog extends DialogBox {

    private HTML label = new HTML();

    public ValidateSubmissionDialog() {
        setGlassEnabled(true);

        // label styling
        label.setHorizontalAlignment(HasAutoHorizontalAlignment.ALIGN_LEFT);

        // button styling & behaviour
        Button ok = new Button("OK");
        ok.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                ValidateSubmissionDialog.this.hide();
            }
        });

        // panel styling and behaviour
        VerticalPanel panel = new VerticalPanel();
        panel.setSpacing(10);
        panel.setWidth("400px");
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        panel.add(label);
        panel.add(ok);

        setWidget(panel);
        showValidationInProgressMessage();
    }

    private void setTitleAndMessage(String title, String message) {
        setText(title);
        label.setHTML(message);
        center();
    }

    public void showValidationInProgressMessage() {
        setTitleAndMessage("Validating...", "Please wait as the submission is being validated");
    }

    public void showValidationFailureMessage() {
        setTitleAndMessage("Validation failed", "Validation has failed. Please see the validation log for more information");
    }

    public void showSubmissionInProgressMessage() {
        setTitleAndMessage("Submitting...", "Please wait as the submission is being processed");
    }

    public void showSubmissionFailureMessage() {
        setTitleAndMessage("Submission failed", "There was a problem submitting experiment to ArrayExpress. Please try again or contact us at arrayexpress@ebi.ac.uk");
    }

    public void showSubmissionSuccessMessage() {
        setTitleAndMessage(
                "Submission successful",
                "<p>The experiment has been successfully submitted to ArrayExpress using Annotare.</p>" +
                "<p>Our curation team will review your submission and will email you with any questions. " +
                "Once all the required information is provided we will send you an accession number.</p>" +
                "<p>In the meantime, please contact <a href=\"mailto:arrayexpress@ebi.ac.uk\">arrayexpress@ebi.ac.uk</a> with any questions. " +
                 "Further information can be found at <a href=\"http://www.ebi.ac.uk/fgpt/magetab/help/after_submission.html\" target=\"_blank\">http://www.ebi.ac.uk/fgpt/magetab/help/after_submission.html</a></p>");
    }
}
