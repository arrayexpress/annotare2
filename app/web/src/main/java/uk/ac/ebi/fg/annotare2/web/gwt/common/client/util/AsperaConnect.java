/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package uk.ac.ebi.fg.annotare2.web.gwt.common.client.util;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ObjectElement;

public class AsperaConnect {
    private final static String ASPERA_OBJECT_ID = "aspera-web";

    public static void addAsperaObject() {
        if (null == Document.get().getElementById(ASPERA_OBJECT_ID)) {
            ObjectElement asperaObject = Document.get().createObjectElement();
            asperaObject.setId(ASPERA_OBJECT_ID);
            asperaObject.setType("application/x-aspera-web");
            asperaObject.setWidth("0px");
            asperaObject.setHeight("0px");
            Document.get().getBody().appendChild(asperaObject);
        }
    }


    public static native boolean isInstalled() /*-{
        if ($wnd.navigator.userAgent.indexOf('MSIE') != -1) {
            try {
                var activex = new ActiveXObject('Aspera.AsperaWebCtrl.1');
                return true;
            }
            catch (error) {
                return false;
            }
        }
        else {
            for (var i = 0; i < $wnd.navigator.plugins.length; i++) {
                if ("Aspera Web" == $wnd.navigator.plugins[i].name.substring(0,10)) {
                    return true;
                }
            }
        }

        return false;
    }-*/;

    public static native boolean isEnabled() /*-{
        var plugin = $doc.getElementById("aspera-web");
        return null != plugin && plugin.runOpenFileDialog;
    }-*/;

    public static native void uploadFilesTo(String url) /*-{
        function unescapePathSet(string) {
            var pathArray = new Array();
            var path = "";
            var idx = 0;

            idx = string.search(/[^\\]\:/);
            while (idx != -1) {
                path = string.slice(0, idx + 1);
                path = path.replace(/\\\\/g, "\\");
                path = path.replace(/\\\:/g, "\:");
                pathArray.push(path);

                string = string.slice(idx + 2);
                idx = string.search(/[^\\]\:/);
            }

            string = string.replace(/\\\\/g, "\\");
            string = string.replace(/\\\:/g, "\:");
            pathArray.push(string);

            return pathArray;
            }

        var plugin = $doc.getElementById("aspera-web");
        var pathSet = plugin.runOpenFileDialog(false, true);
        if (0 != pathSet.length) {
            pathSet = unescapePathSet(pathSet);
            for (var i = 0; i < pathSet.length; i++) {
                plugin.addToUploadList(pathSet[i]);
            }
            plugin.startListUploadToURL(url);
            plugin.clearUploadList();
        }
    }-*/;
}
