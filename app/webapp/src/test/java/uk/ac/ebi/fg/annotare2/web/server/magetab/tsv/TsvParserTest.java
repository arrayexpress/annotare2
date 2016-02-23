/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.magetab.tsv;

import com.google.common.base.Charsets;
import org.junit.Test;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Row;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author Olga Melnichuk
 */
public class TsvParserTest {

    @Test
    public void test() throws IOException {
        List<List<String>> rows = new ArrayList<List<String>>() {{
            add(asList("1", "2"));
            add(asList("3", "", "4", ""));
            add(Collections.<String>emptyList());
            add(asList(""));
        }};

        StringBuilder sb = new StringBuilder();
        for (List<String> row : rows) {
            sb.append(on("\t").join(row)).append("\n");
        }

        Table table = (new TsvParser()).parse(new ByteArrayInputStream(sb.toString().getBytes(Charsets.UTF_8)));

        assertEquals(rows.size(), table.getHeight());
        for (int i = 0; i < table.getHeight(); i++) {
            List<String> row = rows.get(i);
            if (row.isEmpty()) {
                assertEquals(0, table.getRow(i).getTrimmedSize());
            }
            for (int j = 0; j < row.size(); j++) {
                String value = row.get(j);
                if (isNullOrEmpty(value)) {
                    assertNull(table.getValueAt(i, j));
                } else {
                    assertEquals(value, table.getValueAt(i, j));
                }
            }
        }
    }

    @Test
    public void textVsBinaryTest() {
        try {
            (new TsvParser()).parse(TsvParserTest.class.getResourceAsStream("/E-TABM-1009.idf.txt"));
        } catch (IOException e) {
            fail();
        }

        try {
            (new TsvParser()).parse(TsvParserTest.class.getResourceAsStream("/image.png"));
            fail();
        } catch (IOException e) {
            // OK
        }
    }

    @Test
    public void emptyTextTest() {
        try {
            (new TsvParser()).parse(new ByteArrayInputStream(" ".getBytes()));
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testTabEscape() throws IOException {
        Table table = (new TsvParser()).parse(new ByteArrayInputStream("value\t\"value\tvalue\"".getBytes()));
        assertEquals(2, table.getWidth());
        assertEquals(1, table.getHeight());

        Row r = table.getRow(0);
        assertEquals("value", r.getValue(0));
        assertEquals("value\tvalue", r.getValue(1));
    }

    @Test
    public void testNewLineEscape() throws IOException {
        Table table = (new TsvParser()).parse(new ByteArrayInputStream("value\t\"value\nvalue\"".getBytes()));
        assertEquals(2, table.getWidth());
        assertEquals(1, table.getHeight());

        Row r = table.getRow(0);
        assertEquals("value", r.getValue(0));
        assertEquals("value\nvalue", r.getValue(1));
    }

    @Test
    public void testDoubleQuotesEscape() throws IOException {
        Table table = (new TsvParser()).parse(new ByteArrayInputStream("value\t\"value\\\"value\"".getBytes()));
        assertEquals(2, table.getWidth());
        assertEquals(1, table.getHeight());

        Row r = table.getRow(0);
        assertEquals("value", r.getValue(0));
        assertEquals("value\"value", r.getValue(1));
    }
}
