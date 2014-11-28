/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;

import java.util.Date;

import static com.google.gwt.i18n.client.DateTimeFormat.getFormat;
/**
 * @author Olga Melnichuk
 */
public class Utils {

    public static String getEditorUrl(long submissionId) {
        UrlBuilder builder = Window.Location.createUrlBuilder();
        builder.setPath(Window.Location.getPath() + "edit/" + submissionId + "/");
        builder.setHash(null);
        return builder.buildString();
    }

    public static String getPlaceholderUrl() {
        UrlBuilder builder = Window.Location.createUrlBuilder();
        builder.setPath(Window.Location.getPath() + "launcher.jsp");
        builder.setHash(null);
        return builder.buildString();
    }

    public static String formatDate(Date created) {
        return getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(created);
    }
}
