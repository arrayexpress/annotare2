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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;

/**
 * @author Olga Melnichuk
 */
public class EditorUtils {

    private static final RegExp SUBMISSION_ID = RegExp.compile("\\/([0-9]+)\\/$");

    public static Integer getSubmissionId() {
        String path = Window.Location.getPath();
        MatchResult res = SUBMISSION_ID.exec(path);
        if (res == null) {
            return null;
        }
        return Integer.parseInt(res.getGroup(1));
    }

    public static DateTimeFormat dateTimeFormat() {
        return DateTimeFormat.getFormat("yyyy-MM-dd");
    }

    public static String dateTimeFormatPlaceholder() {
        return "YYYY-MM-DD";
    }
}
