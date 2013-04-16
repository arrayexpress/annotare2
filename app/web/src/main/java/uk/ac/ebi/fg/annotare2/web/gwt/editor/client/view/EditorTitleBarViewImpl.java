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
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ValidateSubmissionDialog;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.WaitingPopup;

/**
 * @author Olga Melnichuk
 */
public class EditorTitleBarViewImpl extends Composite implements EditorTitleBarView {

    interface Binder extends UiBinder<HTMLPanel, EditorTitleBarViewImpl> {
    }

    @UiField
    Label accessionLabel;

    @UiField
    Button validateButton;

    @UiField
    Anchor createNewLink;

    private Presenter presenter;

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
        validateButton.setEnabled(type == SubmissionType.EXPERIMENT);
    }

    @UiHandler("validateButton")
    public void onValidateButtonClick(ClickEvent clickEvent) {
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
    public void onCreateLinkClick(ClickEvent clickEvent) {
        if (Window.confirm("Please note that the all data of the submission will be lost. Do you want to continue?")) {
            final WaitingPopup popup = new WaitingPopup("Creating new submission, please wait...");
            popup.setPopupPosition(Window.getClientWidth()/2, Window.getClientHeight()/2);
            presenter.discardSubmissionData(new AsyncCallback<Void>(){
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
}
