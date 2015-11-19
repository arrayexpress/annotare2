package uk.ac.ebi.fg.annotare.prototype.upload.resumable.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * Created by kolais on 19/11/2015.
 */
public class ResumableUpload extends JavaScriptObject {

    protected ResumableUpload() {
    }

    public static final native void init(Element element)/*-{
        var r = new $wnd.Resumable({
            target: '/upload',
            query: {xxx: 'xxx'}
        });
        r.assignBrowse(element);
    }-*/;
}
