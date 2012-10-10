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

package uk.ac.ebi.fg.annotare2.magetab.idf.format;

import com.google.common.annotations.GwtCompatible;
import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class GwtTextFormatter extends TextFormatter {

    private DateTimeFormat dateFormat = DateTimeFormat.getFormat(DATE_FORMAT);

    public static void init() {
        TextFormatter.setDelegate(new GwtTextFormatter());
    }

    @Override
    public Date parseDate(String str) {
        return isNullOrEmpty(str) ? null : dateFormat.parse(str);
    }

    @Override
    public String formatDate(Date date) {
        return date == null ? null : dateFormat.format(date);
    }
}
