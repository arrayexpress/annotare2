package uk.ac.ebi.fg.annotare.prototype.upload.resumable.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.gwt.resumable.client.ResumableFile;
import uk.ac.ebi.fg.gwt.resumable.client.ResumableFileCallback;
import uk.ac.ebi.fg.gwt.resumable.client.ResumableUploader;

import java.util.logging.Logger;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class ResumableUploadSample implements EntryPoint {
    interface ResumableUploadSampleBinder extends UiBinder<Widget, ResumableUploadSample> { //
    }

    private final static Logger logger = Logger.getLogger("ResumableUploadSample");

    @UiField
    Label text;

    ResumableUploader uploader;

    public void onModuleLoad() {
        final ResumableUploadSampleBinder binder = GWT.create(ResumableUploadSampleBinder.class);
        final Widget widget = binder.createAndBindUi(this);

        RootLayoutPanel.get().add(widget);
        uploader = ResumableUploader.newInstance("/resumable/upload");
        uploader.assignBrowse(Document.get().getElementById("upload-button"));
        uploader.assignDrop(Document.get().getElementById("upload-drop"));
        uploader.addCallback(new ResumableFileCallback() {
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
                text.setText("Sent " + (int)(file.getProgress(false) * 100) + "% of " + file.getFileName());

                //if (file.getProgress(false) > 0.5) {
                //    file.cancel();
                //}
            }

            @Override
            public void onFileSuccess(ResumableUploader uploader, ResumableFile file) {
                text.setText("Successfully sent " + file.getFileName());
            }

            @Override
            public void onFileRetry(ResumableUploader uploader, ResumableFile file) {

            }

            @Override
            public void onFileError(ResumableUploader uploader, ResumableFile file, String message) {
                text.setText("Error sending " + file.getFileName() + " - " + message);
            }
        });
    }

//    @UiHandler("cancelBtn")
//    void cancelBtnClicked(ClickEvent event) {
//        uploader.cancel();
//    }
//
//    @UiHandler("pauseBtn")
//    void pauseBtnClicked(ClickEvent event) {
//        uploader.pause();
//    }
//
//    @UiHandler("resumeBtn")
//    void resumeBtnClicked(ClickEvent event) {
//        uploader.upload();
//    }
}
