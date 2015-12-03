package uk.ac.ebi.fg.annotare.prototype.upload.resumable.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.logging.Logger;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class ResumableUploadSample implements EntryPoint {
    interface ResumableUploadSampleBinder extends UiBinder<Widget, ResumableUploadSample> { //
    }

    private final static Logger logger = Logger.getLogger("ResumableUploadSample");

    public void onModuleLoad() {
        final ResumableUploadSampleBinder binder = GWT.create(ResumableUploadSampleBinder.class);
        final Widget widget = binder.createAndBindUi(this);

        RootLayoutPanel.get().add(widget);
        ResumableUpload u = ResumableUpload.newInstance("/resumable/upload");
        u.assignBrowse(Document.get().getElementById("upload-button"));
        u.addCallback(new ResumableFileCallback() {
            @Override
            public void onFileAdded(ResumableUpload upload, ResumableFile file) {
                upload.upload();
            }

            @Override
            public void onFilesAdded(ResumableUpload upload, JsArray<ResumableFile> files) {
                upload.upload();
            }

            @Override
            public void onFileProgress(ResumableUpload upload, ResumableFile file) {

            }

            @Override
            public void onFileSuccess(ResumableUpload upload, ResumableFile file) {

            }

            @Override
            public void onFileRetry(ResumableUpload upload, ResumableFile file) {

            }

            @Override
            public void onFileError(ResumableUpload upload, ResumableFile file, String message) {

            }
        });
    }

}

