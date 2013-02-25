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

import com.google.common.annotations.GwtCompatible;

import java.util.Date;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public interface DateFormat {

    public static String DATE_FORMAT = "yyyy-MM-dd";

    Date parse(String str);

    String format(Date date);

}