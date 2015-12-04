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

package uk.ac.ebi.fg.annotare.prototype.upload.resumable.client;

import com.google.gwt.core.client.JavaScriptObject;

public class ResumableFile extends JavaScriptObject {

    protected ResumableFile() {}

    public final native String getFileName() /*-{
        return this.fileName;
    }-*/;

    public final native String getFileSize() /*-{
        return this.size;
    }-*/;

    public final native float getProgress(boolean isRelative) /*-{
        return this.progress(isRelative);
    }-*/;

    public final native void abort() /*-{
        this.abort();
    }-*/;

    public final native void cancel() /*-{
        this.cancel();
    }-*/;

    public final native void retry() /*-{
        this.retry();
    }-*/;

    public final native void bootstrap() /*-{
        this.bootstrap();
    }-*/;
}
