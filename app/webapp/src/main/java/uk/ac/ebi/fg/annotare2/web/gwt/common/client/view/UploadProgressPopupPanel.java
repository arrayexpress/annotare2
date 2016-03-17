/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.common.client.view;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.UploadedFileInfo;
import uk.ac.ebi.fg.gwt.resumable.client.ResumableCallback;
import uk.ac.ebi.fg.gwt.resumable.client.ResumableFile;
import uk.ac.ebi.fg.gwt.resumable.client.ResumableFileCallback;
import uk.ac.ebi.fg.gwt.resumable.client.ResumableUploader;

import java.util.logging.Logger;

public class UploadProgressPopupPanel extends PopupPanel {

    private final static Logger logger = Logger.getLogger("gwt.client.UploadProgressPopupPanel");

    private final DivElement messageElement;
    private final Element progressBarElement;
    private final DivElement errorElement;

    private Presenter presenter;

    private Duration startTime;
    private double avgSpeed;

    public UploadProgressPopupPanel(ResumableUploader uploader) {
        super(false, true);

        uploader.addCallback(new UploaderCallback(this));
        uploader.addFileCallback(new UploaderFileCallback(this));

        setStyleName("gwt-ProgressPopup");
        addStyleName("info");
        PopupPanel.setStyleName(getContainerElement(), "container");
        messageElement = Document.get().createDivElement();
        PopupPanel.setStyleName(messageElement, "message");
        getContainerElement().appendChild(messageElement);

        DivElement progressWrapper = Document.get().createDivElement();
        PopupPanel.setStyleName(progressWrapper, "progress-wrapper");

        progressBarElement = Document.get().createElement("progress");
        PopupPanel.setStyleName(progressBarElement, "progressbar");
        progressBarElement.setAttribute("value", "0");
        progressBarElement.setAttribute("max", "100");
        progressWrapper.appendChild(progressBarElement);
        getContainerElement().appendChild(progressWrapper);

        errorElement = Document.get().createDivElement();
        PopupPanel.setStyleName(errorElement, "error");
        getContainerElement().appendChild(errorElement);

        setAnimationEnabled(false);
        setGlassEnabled(true);
    }

    private void showProgress() {
        setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                int left = (Window.getClientWidth() - offsetWidth) / 2;
                int top = 0;
                setPopupPosition(left, top);
            }
        });
        updateError("");
        updateMessage("");
        resetProgress();
    }

    private void hideProgress() {
        Timer timer = new Timer() {
            @Override
            public void run() {
                hide();
            }
        };
        timer.schedule(2000);
    }

    private void updateMessage(String message) {
        messageElement.setInnerHTML(message);
    }

    private void resetProgress() {
        avgSpeed = 0;
        startTime = new Duration();

        progressBarElement.setAttribute("value", "0");
        errorElement.setInnerHTML("");

    }

    private void updateProgress(float progress, long size) {
        long sent = Math.round(size * progress);
        avgSpeed = sent * 1000 / startTime.elapsedMillis();

        progressBarElement.setAttribute("value", String.valueOf(Math.round(progress * 100)));
        errorElement.setInnerHTML(formatSpeed(avgSpeed));
    }

    private String formatSpeed(double speed) {
        int unit = 1024;
        if (speed < unit) return NumberFormat.getFormat("#.0 B/s").format(speed);
        int exp = (int)(Math.log(speed) / Math.log(unit));
        String unitName = "kMGTPE".charAt(exp-1) + "B/s";
        return NumberFormat.getFormat("#.0 " + unitName).format(speed / Math.pow(unit, exp));
    }

    private void updateError(String error) {
        errorElement.setInnerHTML(error);
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public interface Presenter {
        void uploadFile(UploadedFileInfo fileInfo, AsyncCallback<Void> callback);
    }


    private class UploaderCallback implements ResumableCallback {
        private final UploadProgressPopupPanel panel;

        public UploaderCallback(UploadProgressPopupPanel panel) {
            this.panel = panel;
        }

        @Override
        public void onUploadStart(ResumableUploader uploader) {
            panel.showProgress();
        }

        @Override
        public void onComplete(ResumableUploader uploader) {
            panel.updateMessage("Successfully transferred all files");
            panel.hideProgress();
        }

        @Override
        public void onProgress(ResumableUploader uploader) {
        }

        @Override
        public void onError(ResumableUploader uploader, String message, ResumableFile file) {
            panel.updateMessage("Error transferring " + file.getFileName());
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

        private final UploadProgressPopupPanel panel;

        public UploaderFileCallback(UploadProgressPopupPanel panel) {
            this.panel = panel;
        }

        @Override
        public void onFileAdded(ResumableUploader uploader, ResumableFile file) {
//            logger.info("Added file " + file.getFileName() + ", size " + file.getSize());
//            uploader.upload();
        }

        @Override
        public void onFilesAdded(ResumableUploader uploader, JsArray<ResumableFile> files) {
            for (int i = 0; i < files.length(); ++i) {
                ResumableFile file = files.get(i);
                logger.info("Batch added file " + file.getFileName() + ", size " + file.getSize());
            }
            uploader.upload();
        }

        @Override
        public void onFileProgress(ResumableUploader uploader, ResumableFile file) {
            panel.updateMessage("Transferring " + file.getFileName());
            panel.updateProgress(file.getProgress(false), file.getSize());
        }

        @Override
        public void onFileSuccess(ResumableUploader uploader, final ResumableFile file) {
            panel.updateMessage("Successfully transferred " + file.getFileName());
            panel.resetProgress();
            Scheduler.get().scheduleDeferred(
                    new SendUploadedFileInfoCommand(
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
            panel.updateMessage("Error transferring " + file.getFileName());
            panel.resetProgress();
        }
    }

    class SendUploadedFileInfoCommand implements Scheduler.ScheduledCommand {

        private final UploadedFileInfo fileInfo;
        private final UploadProgressPopupPanel panel;

        public SendUploadedFileInfoCommand(UploadedFileInfo fileInfo, UploadProgressPopupPanel panel) {
            this.fileInfo = fileInfo;
            this.panel = panel;
        }

        @Override
        public void execute() {
            presenter.uploadFile(fileInfo,
                    new AsyncCallback<Void>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            panel.updateError(caught.getMessage());
                        }
                        @Override
                        public void onSuccess(Void result) {
                            //
                        }
                    });
        }
    }
}
