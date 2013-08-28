/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.view;

import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;

/**
 * @author Olga Melnichuk
 */
public class Utils {

    public static void openSubmissionEditor(long submissionId) {
        /*String url = GWT.getHostPageBaseURL() + "editor/";
        if (!GWT.isProdMode()) {
            url += "?gwt.codesvr=" + Window.Location.getParameter("gwt.codesvr");
        }*/
        Window.open(editorUrl(submissionId), "_blank", "");
    }

    public static String editorUrl(long submissionId) {
        UrlBuilder builder = Window.Location.createUrlBuilder();
        builder.setPath(Window.Location.getPath() + "edit/" + submissionId + "/");
        builder.setHash(null);
        return builder.buildString();
    }

    public static String launcherUrl() {
        UrlBuilder builder = Window.Location.createUrlBuilder();
        builder.setPath(Window.Location.getPath() + "launcher.jsp");
        builder.setHash(null);
        return builder.buildString();
    }
}
