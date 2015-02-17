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

package uk.ac.ebi.fg.annotare2.web.server.magetab;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Olga Melnichuk
 */
public class MageTabUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatDate(Date date) {
        return date == null ? "" : DATE_FORMAT.format(date);
    }
    
    // fix date that's entered in a different time zone (by aligning it to a closer midnight GMT
    // will work incorrectly for those living in GMT-12 and GMT+13,+14
    public static Date fixDate(Date date) {
        if (null == date) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        if (0 == hours) {
            return date;
        } else if (hours < 12) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            return cal.getTime();
        } else {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.add(Calendar.DATE, 1);
            return cal.getTime();
        }
    }
}
