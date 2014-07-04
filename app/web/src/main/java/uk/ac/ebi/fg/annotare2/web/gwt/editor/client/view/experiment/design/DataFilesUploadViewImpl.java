/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DataFileListPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DataFilesUploadPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.FTPUploadDialog;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class DataFilesUploadViewImpl extends Composite implements DataFilesUploadView {

    @UiField
    DataFileListPanel filesList;

    @UiField
    DataFilesUploadPanel uploadPanel;

    @UiField
    Button ftpUploadBtn;

    private Presenter presenter;

    interface Binder extends UiBinder<Widget, DataFilesUploadViewImpl> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public DataFilesUploadViewImpl() {
        initWidget(Binder.BINDER.createAndBindUi(this));

        ftpUploadBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new FTPUploadDialog(presenter);
                //        new DialogCallback<List<SampleColumn>>() {
                //            @Override
                //            public void onOkay(List<SampleColumn> columns) {
                //                updateColumns(columns);
                //            }
                //        });
            }
        });
    }

    @Override
    public void setDataFiles(List<DataFileRow> rows) {
        filesList.setRows(rows);
    }

    @Override
    public void setExperimentType(ExperimentProfileType type) {
        if (ExperimentProfileType.SEQUENCING == type) {
            uploadPanel.hide();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

        uploadPanel.setPresenter(presenter);
        filesList.setPresenter(presenter);
    }

    @Override
    public void setFtpProperties(String url, String username, String password) {
        //dataFileFtpUploadView.setFtpProperties(url, username, password);
    }
}
