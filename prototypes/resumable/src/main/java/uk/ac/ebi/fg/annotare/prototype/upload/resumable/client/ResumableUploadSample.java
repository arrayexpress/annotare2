package uk.ac.ebi.fg.annotare.prototype.upload.resumable.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
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
        ResumableUpload.init("/resumable/upload");
        ResumableUpload.assignBrowse(Document.get().getElementById("upload-button"));
    }

}

