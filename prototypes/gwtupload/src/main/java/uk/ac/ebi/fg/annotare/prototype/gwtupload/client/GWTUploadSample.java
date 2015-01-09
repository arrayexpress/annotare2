package uk.ac.ebi.fg.annotare.prototype.gwtupload.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import gwtupload.client.IFileInput;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.MultiUploader;

import java.util.List;
import java.util.logging.Logger;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class GWTUploadSample implements EntryPoint {
    //interface GWTUploadSampleBinder extends UiBinder<Widget, GWTUploadSample> { //
    //}

    private final static Logger logger = Logger.getLogger("GWTUploadSample");


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
                //
                IFileInput input = uploader.getFileInput();
                String fieldName = input.getName();
                List<String> fileNames = input.getFilenames();
                for (int i = 0; i < input.getFilenames().size(); ++i) {
                    logger.info("Input file name [" + fileNames.get(i) + "], field name [" + fieldName + "-" + i + "]");
                }
                List<IUploader.UploadedInfo> infos = uploader.getServerMessage().getUploadedInfos();
                for (IUploader.UploadedInfo info : infos) {
                    logger.info("Uploaded file name [" + info.getFileName() + "], field name [" + info.getField() + "]");
                }
            }
        }
    };
}

