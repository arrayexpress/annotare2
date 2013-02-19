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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author Olga Melnichuk
 */
public class JseTextFormatterTest {

    private static Logger log = LoggerFactory.getLogger(JseTextFormatterTest.class);

    private static TextFormatter tf = new JseTextFormatter();

    @Test
    public void testParseEmptyDate() {
        try {
            assertNull(tf.parseDate(null));
            assertNull(tf.parseDate(""));
        } catch (Exception e) {
            log.error("Failed to parse empty date", e);
            fail();
        }
    }

    @Test
    public void testFormatEmptyDate() {
        try {
            assertNull(tf.formatDate(null));
        } catch (Exception e) {
            log.error("Failed to format empty date", e);
            fail();
        }
    }

    @Test
    public void testDateParseAndFormat() {

        try {
            tf.parseDate(tf.formatDate(new Date()));
        } catch (Exception e) {
            log.error("Failed to parse previously formatted date", e);
            fail();
        }
    }

}
