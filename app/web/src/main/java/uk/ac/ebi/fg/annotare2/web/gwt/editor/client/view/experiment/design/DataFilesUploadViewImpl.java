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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DataFileListPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DataFilesUploadPanel;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class DataFilesUploadViewImpl extends Composite implements DataFilesUploadView {

    //@UiField
    //TabLayoutPanel tabLayoutPanel;

    @UiField
    DataFileListPanel filesList;

    @UiField
    DataFilesUploadPanel uploadPanel;

    //@UiField
    //DataFileFtpUploadView dataFileFtpUploadView;

    interface Binder extends UiBinder<Widget, DataFilesUploadViewImpl> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public DataFilesUploadViewImpl() {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    @Override
    public void setDataFiles(List<DataFileRow> rows) {
        filesList.setRows(rows);
    }

    @Override
    public void setExperimentType(ExperimentProfileType type) {
        //if (ExperimentProfileType.SEQUENCING == type
        //        && 3 == tabLayoutPanel.getWidgetCount()) {
        //    tabLayoutPanel.remove(1);
        //}
    }

    @Override
    public void setPresenter(Presenter presenter) {
        uploadPanel.setPresenter(presenter);
        //dataFileFtpUploadView.setPresenter(presenter);
        filesList.setPresenter(presenter);
    }

    @Override
    public void setFtpProperties(String url, String username, String password) {
        //dataFileFtpUploadView.setFtpProperties(url, username, password);
    }
}
