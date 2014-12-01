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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public class ImportSubmissionDialog extends DialogBox {

    //@UiField
    //DataFilesUploadView dataFilesUploadView;

    @UiField
    DeckLayoutPanel deckPanel;

    interface Binder extends UiBinder<Widget, ImportSubmissionDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    private Presenter presenter;

    private Long submissionId;

    public ImportSubmissionDialog() {
        setModal(true);
        setGlassEnabled(true);
        setText("Import Experiment Submission");

        setWidget(Binder.BINDER.createAndBindUi(this));
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        //dataFilesUploadView.setPresenter(presenter);
    }

    //public void setDataFiles(List<DataFileRow> files) {
    //    dataFilesUploadView.setDataFiles(files);
    //}

    public void setImportStage(int stage) {
        deckPanel.showWidget(stage);
    }

    public void startImport(long submissionId) {
        this.submissionId = submissionId;

        presenter.onImportStarted();
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

    @UiHandler("validateButton")
    void onValidateClick(ClickEvent event) {
        presenter.onImportValidate();
    }

    @UiHandler("submitButton")
    void onSubmitClick(ClickEvent event) {
        presenter.onImportSubmit();
    }

    @UiHandler("closeButton")
    void onCloseClick(ClickEvent event) {
        hide();
        presenter.onImportCancelled();
    }

    public interface Presenter /*extends DataFilesUploadView.Presenter*/ {
        void onImportStarted();

        void onImportCancelled();

        void onImportValidate();

        void onImportSubmit();
    }
}
