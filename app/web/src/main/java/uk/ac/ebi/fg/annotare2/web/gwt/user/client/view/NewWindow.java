/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.view;

import com.google.gwt.core.client.JavaScriptObject;

public class NewWindow extends JavaScriptObject {
    // All types that extend JavaScriptObject must have a protected,
    // no-args constructor.
    protected NewWindow() {
    }

    public static native NewWindow open(String url, String target, String options) /*-{
        return $wnd.open(url, target, options);
    }-*/;

    public final native void close() /*-{
        this.close();
    }-*/;

    public final native void setUrl(String url) /*-{
        if (this.location) {
            this.location = url;
        }
    }-*/;
}
