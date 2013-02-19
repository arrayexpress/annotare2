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

package uk.ac.ebi.fg.annotare2.magetab.rowbased.format;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class JseTextFormatter extends TextFormatter {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT);

    public static void init() {
        TextFormatter.setDelegate(new JseTextFormatter());
    }

    @Override
    public Date parseDate(String str) {
        try {
            return isNullOrEmpty(str) ? null : dateFormat.parse(str);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Date is in the wrong format: " + str);
        }
    }

    @Override
    public String formatDate(Date date) {
        return date == null ? null : dateFormat.format(date);
    }
}
