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

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import uk.ac.ebi.fg.gwt.resumable.client.ResumableCallback;
import uk.ac.ebi.fg.gwt.resumable.client.ResumableFile;
import uk.ac.ebi.fg.gwt.resumable.client.ResumableFileCallback;
import uk.ac.ebi.fg.gwt.resumable.client.ResumableUploader;

import java.util.logging.Logger;

public class UploadProgressPopupPanel extends PopupPanel {

    private final static Logger logger = Logger.getLogger("gwt.client.UploadProgressPopupPanel");

    private final ResumableUploader uploader;
    private final DivElement messageElement;

    public UploadProgressPopupPanel(ResumableUploader uploader) {
        super(false, true);
        this.uploader = uploader;

        uploader.addCallback(new UploaderCallback(this));
        uploader.addFileCallback(new UploaderFileCallback(this));

        setStyleName("gwt-NotificationPopup");
        addStyleName("info");
        PopupPanel.setStyleName(getContainerElement(), "container");
//        iconElement = Document.get().createDivElement();
//        PopupPanel.setStyleName(iconElement, "icon");
        messageElement = Document.get().createDivElement();
        PopupPanel.setStyleName(messageElement, "message");
//        getContainerElement().appendChild(iconElement);
        getContainerElement().appendChild(messageElement);
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
    }

    private void hideProgress() {
        Timer timer = new Timer() {
            @Override
            public void run() {
                hide();
            }
        };
        timer.schedule(5000);
    }

    private void updateMessage(String message) {
        messageElement.setInnerHTML(message);
    }

//    private void scheduleAutoHide() {
//        Timer timer = new Timer() {
//            @Override
//            public void run() {
//                hide();
//            }
//        };
//        timer.schedule(5000);
//    }

//    public static void message(String message, boolean shouldAutoHide) {
//        if (null != instance) {
//            cancel();
//        }
//        instance = new UploadProgressPopupPanel(Type.INFO, shouldAutoHide, false);
//        instance.showMessage(message);
//    }

    //    public static void cancel() {
//        if (null != instance && instance.isAttached()) {
//            instance.hide();
//        }
//    }
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
            panel.hideProgress();
        }

        @Override
        public void onProgress(ResumableUploader uploader) {

        }

        @Override
        public void onError(ResumableUploader uploader, String message, ResumableFile file) {
            panel.updateMessage("Error sending " + file.getFileName());
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
            logger.info("Added file " + file.getFileName() + ", size " + file.getFileSize());
            uploader.upload();
        }

        @Override
        public void onFilesAdded(ResumableUploader uploader, JsArray<ResumableFile> files) {
            uploader.upload();
        }

        @Override
        public void onFileProgress(ResumableUploader uploader, ResumableFile file) {
            panel.updateMessage("Sent " + (int) (file.getProgress(false) * 100) + "% of " + file.getFileName());
        }

        @Override
        public void onFileSuccess(ResumableUploader uploader, ResumableFile file) {
            panel.updateMessage("Successfully sent " + file.getFileName());
        }

        @Override
        public void onFileRetry(ResumableUploader uploader, ResumableFile file) {

        }

        @Override
        public void onFileError(ResumableUploader uploader, ResumableFile file, String message) {
            panel.updateMessage("Error sending " + file.getFileName());
        }
    }
}
