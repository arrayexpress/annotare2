package uk.ac.ebi.fg.annotare.prototype.upload.resumable.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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

    public void onModuleLoad() {
        final ResumableUploadSampleBinder binder = GWT.create(ResumableUploadSampleBinder.class);
        final Widget widget = binder.createAndBindUi(this);

        RootLayoutPanel.get().add(widget);
        ResumableUploader u = ResumableUploader.newInstance("/resumable/upload");
        u.assignBrowse(Document.get().getElementById("upload-button"));
        u.assignDrop(Document.get().getElementById("upload-drop"));
        u.addCallback(new ResumableFileCallback() {
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
                text.setText("Sent " + (int)(file.getProgress(false) * 100) + "% of " + file.getFileName() + " (" + uploader.files().length() + " files)");
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

            }
        });
    }

}

