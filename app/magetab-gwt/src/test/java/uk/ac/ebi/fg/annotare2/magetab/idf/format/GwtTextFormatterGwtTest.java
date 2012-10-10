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

import com.google.gwt.junit.client.GWTTestCase;

import java.util.Date;


/**
 * @author Olga Melnichuk
 */
public class GwtTextFormatterGwtTest extends GWTTestCase {

    public void testParseDate() {
        TextFormatter tf = new GwtTextFormatter();
        try {
            assertNull(tf.parseDate(null));
            assertNull(tf.parseDate(""));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testFormatEmptyDate() {
        TextFormatter tf = new GwtTextFormatter();
        try {
            assertNull(tf.formatDate(null));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testDateParseAndFormat() {
        TextFormatter tf = new GwtTextFormatter();
        try {
            tf.parseDate(tf.formatDate(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Override
    public String getModuleName() {
        return "uk.ac.ebi.fg.annotare2.magetab.Magetab";
    }
}
