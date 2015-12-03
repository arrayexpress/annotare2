package uk.ac.ebi.fg.annotare.prototype.upload.resumable.client;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

public class ResumableUpload extends JavaScriptObject {

    protected ResumableUpload() {}

    public static final native void init(String url) /*-{
        this.r = new $wnd.Resumable({
            target: url,
            query: {xxx: 'xxx'},
            method: "multupart"
        });
    }-*/;

    public static final native void assignBrowse(Element element)/*-{
        if (undefined !== this.r) {
            this.r.assignBrowse(element);
        } else {
            console.error('resumable.assignBrowse: please init the library first');
        }
    }-*/;

    public static final native void upload() /*-{
        if (undefined !== this.r) {
            this.r.upload();
        } else {
            console.error('resumable.upload: please init the library first');
        }
    }-*/;

    public static final native void on(String eventType, Callback callback) /*-{
        if (undefined !== this.r) {
            this.r.on(eventType, function() {
                callback;
            });
        } else {
            console.error('resumable.on: please init the library first');
        }
    }-*/;
}

/*
        this.r.on('fileAdded', function(file, event){
            r.upload();
            console.debug('fileAdded', event);
        });
        this.r.on('filesAdded', function(array){
            r.upload();
            console.debug('filesAdded', array);
        });
 */