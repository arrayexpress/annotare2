package uk.ac.ebi.fg.annotare.prototype.gwtupload.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import gwtupload.client.IFileInput;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.MultiUploader;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class GWTUploadSample implements EntryPoint {
    interface GWTUploadSampleBinder extends UiBinder<Widget, GWTUploadSample> { //
    }

    public void onModuleLoad() {
        //final GWTUploadSampleBinder binder = GWT.create(GWTUploadSampleBinder.class);
        //final Widget widget = binder.createAndBindUi(this);

        // Create a new uploader panel and attach it to the document
        MultiUploader defaultUploader = new MultiUploader(IFileInput.FileInputType.LABEL);
        //widget.add
        RootPanel.get().add(defaultUploader);

        // Add a finish handler which will load the image once the upload finishes
        defaultUploader.addOnFinishUploadHandler(onFinishUploaderHandler);
    }

    // Load the image in the document and in the case of success attach it to the viewer
    private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
        public void onFinish(IUploader uploader) {
            if (uploader.getStatus() == Status.SUCCESS) {

                // The server sends useful information to the client by default
                IUploader.UploadedInfo info = uploader.getServerInfo();
                System.out.println("File name " + info.name);
                System.out.println("File content-type " + info.ctype);
                System.out.println("File size " + info.size);

                // You can send any customized message and parse it
                System.out.println("Server message " + info.message);
            }
        }
    };
}

