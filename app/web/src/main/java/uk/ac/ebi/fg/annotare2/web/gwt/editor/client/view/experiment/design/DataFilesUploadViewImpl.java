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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gwtupload.client.IUploadStatus;
import gwtupload.client.Utils;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DataFileListPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DataFilesUploadPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.FTPUploadDialog;

import java.util.List;
import java.util.Set;

/**
 * @author Olga Melnichuk
 */
public class DataFilesUploadViewImpl extends Composite implements DataFilesUploadView {

    @UiField
    HorizontalPanel controlPanel;

    @UiField
    FlowPanel statusPanel;

    @UiField
    DataFileListPanel fileListPanel;

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
        //uploadPanel.setStatusWidget(new UploadStatus(statusPanel, controlPanel));

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
    }

    @Override
    public void setFtpProperties(String url, String username, String password) {
        //dataFileFtpUploadView.setFtpProperties(url, username, password);
    }

    public static class UploadStatus implements IUploadStatus {

        private IUploadStatus.Status status = Status.UNINITIALIZED;

        private final Panel statusPanel;
        private final Panel controlPanel;
        private final Widget progressBar;

        public UploadStatus(Panel statusPanel, Panel controlPanel) {
            this.statusPanel = statusPanel;
            this.controlPanel = controlPanel;

            this.progressBar = new SimplePanel();
            this.progressBar.setWidth("0%");

            SimplePanel progressContainer = new SimplePanel();
            progressContainer.setStyleName("wgt-progress");
            progressContainer.add(progressBar);

            this.statusPanel.add(progressContainer);
        }

        @Override
        public HandlerRegistration addCancelHandler(UploadCancelHandler handler) {
            return null;
        }

        public Status getStatus() {
            return status;
        }

        @Deprecated
        @Override
        final public Widget getWidget() {
            return asWidget();
        }

        @Override
        public Widget asWidget() {
            return statusPanel;
        }

        @Override
        public IUploadStatus newInstance() {
            return this;
        }

        @Override
        public void setCancelConfiguration(Set<CancelBehavior> config) {
            //cancelCfg = config;
        }

        @Override
        public void setError(String msg) {
            setStatus(Status.ERROR);
            Window.alert(msg.replaceAll("\\\\n", "\\n"));
        }

        @Override
        public void setFileNames(List<String> names) {
            //fileNames = names;
            //fileNameLabel.setHTML(Utils.convertCollectionToString(names, "<br/>"));
            //if (prg instanceof HasText) {
            //    ((HasText) prg).setText(Utils.convertCollectionToString(names, ","));
            //}
        }

        @Override
        public void setI18Constants(UploadStatusConstants strs) {
        }

        @Override
        public void setProgress(long done, long total) {
            int percent = Utils.getPercent(done, total);
            progressBar.setWidth(percent + "%");
        }

        @Override
        public void setStatus(Status stat) {
            /*
            String statusName = stat.toString().toLowerCase();
            statusLabel.removeStyleDependentName(statusName);
            statusLabel.addStyleDependentName(statusName);
            switch (stat) {
                case CHANGED: case QUEUED:
                    updateStatusPanel(false, i18nStrs.uploadStatusQueued());
                    break;
                case SUBMITING:
                    updateStatusPanel(false, i18nStrs.uploadStatusSubmitting());
                    break;
                case INPROGRESS:
                    updateStatusPanel(true, i18nStrs.uploadStatusInProgress());
                    if (!cancelCfg.contains(CancelBehavior.STOP_CURRENT)) {
                        cancelLabel.setVisible(false);
                    }
                    break;
                case SUCCESS: case REPEATED:
                    updateStatusPanel(false, i18nStrs.uploadStatusSuccess());
                    if (!cancelCfg.contains(CancelBehavior.REMOVE_REMOTE)) {
                        cancelLabel.setVisible(false);
                    }
                    break;
                case INVALID:
                    if (cancelCfg.contains(CancelBehavior.REMOVE_INVALID)) {
                        asWidget().removeFromParent();
                    }
                    break;
                case CANCELING:
                    updateStatusPanel(false, i18nStrs.uploadStatusCanceling());
                    break;
                case CANCELED:
                    updateStatusPanel(false, i18nStrs.uploadStatusCanceled());
                    if (cancelCfg.contains(CancelBehavior.REMOVE_CANCELLED_FROM_LIST)) {
                        asWidget().removeFromParent();
                    }
                    break;
                case ERROR:
                    updateStatusPanel(false, i18nStrs.uploadStatusError());
                    break;
                case DELETED:
                    updateStatusPanel(false, i18nStrs.uploadStatusDeleted());
                    asWidget().removeFromParent();
                    break;
            }
            if (status != stat && onUploadStatusChangedHandler != null) {
                status = stat;
                onUploadStatusChangedHandler.onStatusChanged(this);
            }
            */
            status = stat;
        }

        @Override
        public void setStatusChangedHandler(final UploadStatusChangedHandler handler) {
            //onUploadStatusChangedHandler = handler;
        }

        @Override
        public void setVisible(boolean b) {
            controlPanel.setVisible(!b);
            statusPanel.setVisible(b);
        }
    }
}
