package uk.ac.ebi.fg.annotare.prototype.upload.resumable.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

public class ResumableUpload extends JavaScriptObject {

    protected ResumableUpload() {
    }

    public static final native void init(Element element)/*-{
        var r = new $wnd.Resumable({
            target: '/resumable/upload',
            query: {xxx: 'xxx'},
            method: "multupart"
        });
        r.assignBrowse(element);
        r.on('fileAdded', function(file, event){
            r.upload();
            console.debug('fileAdded', event);
        });
        r.on('filesAdded', function(array){
            r.upload();
            console.debug('filesAdded', array);
        });
    }-*/;
}
