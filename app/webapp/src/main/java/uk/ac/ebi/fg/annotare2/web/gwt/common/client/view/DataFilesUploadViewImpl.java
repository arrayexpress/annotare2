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

package uk.ac.ebi.fg.annotare2.web.gwt.common.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.SelectionChangeEvent;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.utils.AsperaConnect;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.utils.Urls;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ApplicationProperties;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.gwt.resumable.client.ResumableUploader;

import java.util.List;
//import java.util.logging.Logger;

public class DataFilesUploadViewImpl extends Composite implements DataFilesUploadView, RequiresResize {

//    private final static Logger logger = Logger.getLogger("gwt.client.DataFilesUploadViewImpl");

    @UiField
    Button uploadBtn;

    @UiField
    Button ftpUploadBtn;


    @UiField
    Button asperaUploadBtn;

    @UiField
    Button deleteFilesBtn;

    @UiField
    DataFileListPanel fileListPanel;

    private final FTPUploadDialog ftpUploadDialog;
    private final UploadProgressPopupPanel progressPanel;

    private Presenter presenter;

    private String asperaUrl;

    interface Binder extends UiBinder<Widget, DataFilesUploadViewImpl> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public DataFilesUploadViewImpl() {
        initWidget(Binder.BINDER.createAndBindUi(this));

        JSONObject uploaderOptions = new JSONObject();
        uploaderOptions.put("simultaneousUploads", new JSONNumber(1));
        uploaderOptions.put("method", new JSONString("octet"));

        ResumableUploader uploader = ResumableUploader.newInstance(Urls.getContextUrl() + "upload", uploaderOptions);
        uploader.assignBrowse(uploadBtn.getElement());
        uploader.assignDrop(fileListPanel.getElement());

        progressPanel = new UploadProgressPopupPanel(uploader);
        ftpUploadDialog = new FTPUploadDialog();

        fileListPanel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                deleteFilesBtn.setEnabled(!fileListPanel.getSelectedRows().isEmpty());
            }
        });

//        fileListPanel.addDomHandler(new DragEnterHandler() {
//            @Override
//            public void onDragEnter(DragEnterEvent event) {
//                fileListPanel.addStyleName("drop-active");
//            }
//        }, DragEnterEvent.getType());
//
//        fileListPanel.addDomHandler(new DragLeaveHandler() {
//            @Override
//            public void onDragLeave(DragLeaveEvent event) {
//                fileListPanel.removeStyleName("drop-active");
//            }
//        }, DragLeaveEvent.getType());
//
//        fileListPanel.addDomHandler(new DropHandler() {
//            @Override
//            public void onDrop(DropEvent event) {
//                fileListPanel.removeStyleName("drop-active");
//            }
//        }, DropEvent.getType());
    }

    @SuppressWarnings("unused")
    @UiHandler("ftpUploadBtn")
    void ftpUploadBtClicked(ClickEvent event) {
        presenter.initSubmissionFtpDirectory(new ReportingAsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ftpUploadDialog.setSubmissionDirectory(result);
                ftpUploadDialog.center();
            }
        });
    }

    @SuppressWarnings("unused")
    @UiHandler("asperaUploadBtn")
    void asperaUploadBtClicked(ClickEvent event) {
        if (AsperaConnect.isInstalled()) {
            AsperaConnect.addAsperaObject();
            if (AsperaConnect.isEnabled()) {
                presenter.initSubmissionFtpDirectory(new ReportingAsyncCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        AsperaConnect.uploadFilesTo(asperaUrl + result + "/");
                    }
                });
                return;
            }
        }
        NotificationPopupPanel.warning("Unable to communicate with Aspera Connect plug-in. Please ensure the plug-in is installed correctly and enabled on this site.", false, false);
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
//        if (ExperimentProfileType.SEQUENCING == type) {
//            uploadPanel.hide();
//        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        progressPanel.setPresenter(presenter);
        fileListPanel.setPresenter(presenter);
        ftpUploadDialog.setPresenter(presenter);
    }

    @Override
    public void setApplicationProperties(ApplicationProperties properties) {
        if (properties.isFtpEnabled()) {
            ftpUploadBtn.setEnabled(true);
            ftpUploadBtn.setVisible(true);
            ftpUploadDialog.setApplicationProperties(properties);
        }
        if (properties.isAsperaEnabled()) {
            asperaUploadBtn.setEnabled(true);
            asperaUploadBtn.setVisible(true);
            asperaUrl = properties.getAsperaUrl();
        }
    }

    @Override
    public void onResize() {
        if (getWidget() instanceof RequiresResize) {
            ((RequiresResize) getWidget()).onResize();
        }
    }
}
