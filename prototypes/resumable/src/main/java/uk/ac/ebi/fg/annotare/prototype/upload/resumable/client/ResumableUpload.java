package uk.ac.ebi.fg.annotare.prototype.upload.resumable.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;

public class ResumableUpload extends JavaScriptObject {

    protected ResumableUpload() {}


    public static ResumableUpload newInstance(String url) {
        return createResumableJso(url);
    }

    private static native ResumableUpload createResumableJso(String url) /*-{
        if (undefined !== $wnd.Resumable) {
            return new $wnd.Resumable({
                target: url,
                query: {xxx: 'xxx'},
                method: "multupart"
            });
        } else {
            console.error('resumable.init: please ensure resumable.js is included');
        }
    }-*/;

    public final native void assignBrowse(Element element)/*-{
        if (undefined !== this.assignBrowse) {
            this.assignBrowse(element);
        } else {
            console.error('resumable.assignBrowse: please obtain an instance through ResumableUpload.newInstance');
        }
    }-*/;

    public final native void assignDrop(Element element)/*-{
        if (undefined !== this.assignDrop) {
            this.assignDrop(element);
        } else {
            console.error('resumable.assignDrop: please obtain an instance through ResumableUpload.newInstance');
        }
    }-*/;

    public final native void upload() /*-{
        if (undefined !== this.upload) {
            this.upload();
        } else {
            console.error('resumable.upload: please obtain an instance through ResumableUpload.newInstance');
        }
    }-*/;

    public final native void addCallback(ResumableFileCallback callback) /*-{
        if (undefined !== this.on) {
            this.on('fileAdded', function(file) {
                callback.@uk.ac.ebi.fg.annotare.prototype.upload.resumable.client.ResumableFileCallback::onFileAdded(*)(this, file)
            });
            this.on('filesAdded', function(files) {
                callback.@uk.ac.ebi.fg.annotare.prototype.upload.resumable.client.ResumableFileCallback::onFilesAdded(*)(this, files);
            });
            this.on('fileProgress', function(file) {
                callback.@uk.ac.ebi.fg.annotare.prototype.upload.resumable.client.ResumableFileCallback::onFileProgress(*)(this, file);
            });
            this.on('fileSuccess', function(file) {
                callback.@uk.ac.ebi.fg.annotare.prototype.upload.resumable.client.ResumableFileCallback::onFileSuccess(*)(this, file);
            });
        } else {
            console.error('resumable.on: please obtain an instance through ResumableUpload.newInstance');
        }
    }-*/;

    public final native JsArray<ResumableFile> files() /*-{
        if (undefined !== this.files) {
            return this.files;
        } else {
            console.error('resumable.files: please obtain an instance through ResumableUpload.newInstance')
        }
    }-*/;
}