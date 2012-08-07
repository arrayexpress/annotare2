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

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UISubmissionDetails;

import static uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.Utils.openSubmissionEditor;

/**
 * @author Olga Melnichuk
 */
public class SubmissionViewImpl extends Composite implements SubmissionView {

    interface Binder extends UiBinder<Widget, SubmissionViewImpl> {
    }

    private Presenter presenter;

    @UiField
    HeadingElement accession;

    @UiField
    DivElement title;

    @UiField
    SpanElement created;

    @UiField
    SpanElement status;

    @UiField
    Button editButton;

    private int submissionId;

    public SubmissionViewImpl() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setSubmission(UISubmissionDetails submission) {
        accession.setInnerText(submission.getAccession());
        title.setInnerText(submission.getTitle());
        created.setInnerText(submission.getCreated().toString());
        status.setInnerText(submission.getStatus().getTitle());
        submissionId = submission.getId();
    }

    @UiHandler("editButton")
    public void onViewEditButtonClick(ClickEvent event) {
        openSubmissionEditor(submissionId);
    }

}
