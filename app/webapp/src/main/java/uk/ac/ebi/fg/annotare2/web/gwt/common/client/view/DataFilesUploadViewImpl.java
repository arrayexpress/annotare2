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

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.SelectionChangeEvent;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.utils.AsperaConnect;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.utils.Urls;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ApplicationProperties;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.UploadedFileInfo;
import uk.ac.ebi.fg.gwt.resumable.client.ResumableCallback;
import uk.ac.ebi.fg.gwt.resumable.client.ResumableFile;
import uk.ac.ebi.fg.gwt.resumable.client.ResumableFileCallback;
import uk.ac.ebi.fg.gwt.resumable.client.ResumableUploader;

import java.util.List;
import java.util.logging.Logger;

public class DataFilesUploadViewImpl extends Composite implements DataFilesUploadView, RequiresResize {

    private final static Logger logger = Logger.getLogger("gwt.client.DataFilesUploadViewImpl");

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

    @UiField
    VerticalPanel progressPanel;

    private final FTPUploadDialog ftpUploadDialog;

    // private UploadProgressPopupPanel progressPanel = null;

    private final DivElement messageElement;
    private final Element progressBarElement;
    private final DivElement errorElement;

    private Duration startTime;
    private Duration fileStartTime;
    private double avgSpeed;

    private Presenter presenter;

    private String asperaUrl;

    private ExperimentProfileType experimentProfileType;

    interface Binder extends UiBinder<Widget, DataFilesUploadViewImpl> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public DataFilesUploadViewImpl() {
        initWidget(Binder.BINDER.createAndBindUi(this));

        ftpUploadDialog = new FTPUploadDialog();

        fileListPanel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                deleteFilesBtn.setEnabled(!fileListPanel.getSelectedRows().isEmpty());
            }
        });

        progressPanel.setVisible(false);

        progressPanel.setStyleName("gwt-ProgressPopup");
        progressPanel.addStyleName("info");

        messageElement = Document.get().createDivElement();
        progressPanel.setStyleName(messageElement, "message");


        progressPanel.getElement().appendChild(messageElement);

        DivElement progressWrapper = Document.get().createDivElement();

        progressBarElement = Document.get().createElement("progress");
        progressPanel.setStyleName(progressBarElement, "progressbar");
        progressBarElement.setAttribute("value", "0");
        progressBarElement.setAttribute("max", "100");

        progressWrapper.appendChild(progressBarElement);

        progressPanel.getElement().appendChild(progressWrapper);

        errorElement = Document.get().createDivElement();
        progressPanel.setStyleName(errorElement, "error");

        progressPanel.getElement().appendChild(errorElement);

    }

    @Override
    public boolean isDuplicateFile(String fileName) {
        return fileListPanel.isDuplicated(fileName);
    }

    private void showProgress() {

        progressPanel.setVisible(true);
        updateError("");
        updateMessage("");
        startTime = new Duration();
        resetProgress();
    }

    private void hideProgress() {
        Timer timer = new Timer() {
            @Override
            public void run() {
                progressPanel.setVisible(false);
            }
        };
        timer.schedule(2000);

    }

    private void updateMessage(String message) {
        messageElement.setInnerHTML(message);
    }

    private void resetProgress() {
        avgSpeed = 0;
        fileStartTime = new Duration();

        progressBarElement.setAttribute("value", "0");
        errorElement.setInnerHTML("");

    }

    private void updateProgress(float fileProgress, long fileSize, float allProgress) {
        long sent = Math.round(fileSize * fileProgress);
        avgSpeed = sent * 1000 / fileStartTime.elapsedMillis();
        int eta = Math.round(((1 - allProgress) * startTime.elapsedMillis()) / (allProgress * 1000));

        progressBarElement.setAttribute("value", String.valueOf(Math.round(fileProgress * 100)));
        errorElement.setInnerHTML(formatSpeed(avgSpeed) + ", " + formatTime(eta) + " ETA");
    }

    private String formatSpeed(double speed) {
        int unit = 1024;
        if (speed < unit) return NumberFormat.getFormat("#.0 B/s").format(speed);
        int exp = (int) (Math.log(speed) / Math.log(unit));
        String unitName = "kMGTPE".charAt(exp - 1) + "B/s";
        return NumberFormat.getFormat("#.0 " + unitName).format(speed / Math.pow(unit, exp));
    }

    private String formatTime(int time) {
        int unit = 60;
        String prefix = "";
        if (time > (unit * unit)) {
            prefix = NumberFormat.getFormat("00:").format(time / (unit * unit));
            time = time % (unit * unit);
        }
        return prefix + NumberFormat.getFormat("00:").format(time / unit) +
                NumberFormat.getFormat("00").format(time % unit);
    }

    private void updateError(String error) {
        errorElement.setInnerHTML(error);
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

        experimentProfileType = type;
        JSONObject uploaderOptions = new JSONObject();
        uploaderOptions.put("simultaneousUploads", new JSONNumber(1));
        uploaderOptions.put("method", new JSONString("octet"));

        ResumableUploader uploader = ResumableUploader.newInstance(Urls.getContextUrl() + "upload", uploaderOptions);
        uploader.assignBrowse(uploadBtn.getElement());
        uploader.assignDrop(fileListPanel.getElement());

        uploader.addCallback(new UploaderCallback(progressPanel));
        uploader.addFileCallback(new UploaderFileCallback(progressPanel));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
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

    private class UploaderCallback implements ResumableCallback {
        private final VerticalPanel panel;

        public UploaderCallback(VerticalPanel panel) {
            this.panel = panel;
        }

        @Override
        public void onUploadStart(ResumableUploader uploader) {
            showProgress();
        }

        @Override
        public void onComplete(ResumableUploader uploader) {
            updateMessage("Successfully transferred all files");
            hideProgress();
        }

        @Override
        public void onProgress(ResumableUploader uploader) {
        }

        @Override
        public void onError(ResumableUploader uploader, String message, ResumableFile file) {
            updateMessage("Error transferring " + file.getFileName());
        }

        @Override
        public void onPause() {
        }

        @Override
        public void beforeCancel() {
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onChunkingStart(ResumableFile file) {
        }

        @Override
        public void onChunkingProgress(ResumableFile file, String ratio) {
        }

        @Override
        public void onChunkingComplete(ResumableFile file) {
        }
    }

    class UploaderFileCallback implements ResumableFileCallback {

        private final VerticalPanel panel;

        public UploaderFileCallback(VerticalPanel panel) {
            this.panel = panel;
        }

        @Override
        public void onFileAdded(ResumableUploader uploader, ResumableFile file) {
//            logger.info("Added file " + file.getFileName() + ", size " + file.getSize());
//            uploader.upload();
        }

        @Override
        public void onFilesAdded(ResumableUploader uploader, JsArray<ResumableFile> files) {
            boolean shouldUpload = true;
            StringBuilder sb = new StringBuilder();
            sb.append("The file(s) already exist.<br/>To re-upload, please delete and upload again.<br/><br/>"); //<br/> added here because Notification panel display this as HTML so simple new line character won't work.

            for (int i = 0; i < files.length(); ++i) {
                ResumableFile file = files.get(i);
                if (!isDuplicateFile(file.getFileName())) {
                    logger.info("Batch added file " + file.getFileName() + ", size " + file.getSize());
                } else {
                    sb.append(" - ").append(file.getFileName()).append("<br/>");
                    shouldUpload = false;
                    uploader.removeFile(file);
                }
            }

            if (shouldUpload) {
                uploader.upload();
            } else {
                NotificationPopupPanel.error(sb.toString(), true, false);
            }
        }

        @Override
        public void onFileProgress(ResumableUploader uploader, ResumableFile file) {
            updateMessage("Transferring " + file.getFileName());
            updateProgress(file.getProgress(false), file.getSize(), uploader.progress());
        }

        @Override
        public void onFileSuccess(ResumableUploader uploader, final ResumableFile file) {
            updateMessage("Successfully transferred " + file.getFileName());
            resetProgress();
            Scheduler.get().scheduleDeferred(
                    new DataFilesUploadViewImpl.SendUploadedFileInfoCommand(
                            new UploadedFileInfo(file.getFileName(), file.getSize()),
                            panel
                    )
            );
            uploader.removeFile(file);
        }

        @Override
        public void onFileRetry(ResumableUploader uploader, ResumableFile file) {

        }

        @Override
        public void onFileError(ResumableUploader uploader, ResumableFile file, String message) {
            updateMessage("Error transferring " + file.getFileName());
            resetProgress();
        }
    }

    class SendUploadedFileInfoCommand implements Scheduler.ScheduledCommand {

        private final UploadedFileInfo fileInfo;
        private final VerticalPanel panel;

        public SendUploadedFileInfoCommand(UploadedFileInfo fileInfo, VerticalPanel panel) {
            this.fileInfo = fileInfo;
            this.panel = panel;
        }

        @Override
        public void execute() {
            presenter.uploadFile(fileInfo,
                    new AsyncCallback<Void>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            updateError(caught.getMessage());
                        }
                        @Override
                        public void onSuccess(Void result) {
                        }
                    });
        }
    }

}


