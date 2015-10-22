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

package uk.ac.ebi.fg.annotare2.web.gwt.common.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.SelectionChangeEvent;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.utils.AsperaConnect;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;

import java.util.List;
import java.util.logging.Logger;

public class DataFilesUploadViewImpl extends Composite implements DataFilesUploadView, RequiresResize {

    private final static Logger logger = Logger.getLogger("gwt.client.DataFilesUploadViewImpl");

    @UiField
    DataFilesUploadPanel uploadPanel;

    @UiField
    Button asperaUploadBtn;

    @UiField
    Button ftpUploadBtn;

    @UiField
    Button deleteFilesBtn;

    @UiField
    DataFileListPanel fileListPanel;

    private final FTPUploadDialog ftpUploadDialog;

    private Presenter presenter;

    private String asperaUrl;

    interface Binder extends UiBinder<Widget, DataFilesUploadViewImpl> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public DataFilesUploadViewImpl() {
        initWidget(Binder.BINDER.createAndBindUi(this));
        ftpUploadDialog = new FTPUploadDialog();

        if (!AsperaConnect.isInstalled()) {
            asperaUploadBtn.setVisible(false);
        }

        deleteFilesBtn.setEnabled(false);
        fileListPanel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                deleteFilesBtn.setEnabled(!fileListPanel.getSelectedRows().isEmpty());
            }
        });
    }

    @SuppressWarnings("unused")
    @UiHandler("ftpUploadBtn")
    void ftpUploadBtClicked(ClickEvent event) {
        ftpUploadDialog.center();
    }

    @SuppressWarnings("unused")
    @UiHandler("asperaUploadBtn")
    void asperaUploadBtClicked(ClickEvent event) {
        AsperaConnect.addAsperaObject();
        if (AsperaConnect.isEnabled()) {
            AsperaConnect.uploadFilesTo("fasp://aexpress:aexpress1@fasp.ebi.ac.uk/");
        } else {
            NotificationPopupPanel.warning("Unable to communicate with Aspera Connect plug-in. Please ensure the plug-in is installed correctly and enabled on this site.", false, false);
        }
    }

    @SuppressWarnings("unused")
    @UiHandler("deleteFilesBtn")
    void deleteFilesBtnClicked(ClickEvent event) {
        deleteFilesBtn.setEnabled(false);
        final PopupPanel w = new WaitingPopup();
        w.center();

        fileListPanel.deleteSelectedFiles(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                w.hide();
                deleteFilesBtn.setEnabled(true);
            }

            @Override
            public void onSuccess(Void result) {
                w.hide();
                deleteFilesBtn.setEnabled(true);
            }
        });
    }

    @Override
    public void setSubmissionId(long submissionId) {
        fileListPanel.setSubmissionId(submissionId);
    }

    @Override
    public void setDataFiles(List<DataFileRow> rows) {
        fileListPanel.setRows(rows);
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
        fileListPanel.setPresenter(presenter);
        ftpUploadDialog.setPresenter(presenter);
    }

    @Override
    public void setApplicationProperties(ApplicationProperties properties) {
        ftpUploadBtn.setEnabled(properties.isFtpEnabled());
        asperaUploadBtn.setEnabled(properties.isAsperaEnabled());
        asperaUrl = properties.getAsperaUrl();
        ftpUploadDialog.setFtpProperties(
                properties.getFtpUrl(),
                properties.getFtpUsername(),
                properties.getFtpPassword()
        );
    }

    @Override
    public void onResize() {
        if (getWidget() instanceof RequiresResize) {
            ((RequiresResize) getWidget()).onResize();
        }
    }
}

