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

package uk.ac.ebi.fg.annotare2.web.server.magetab.tsv;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Joiner.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * @author Olga Melnichuk
 */
public class TsvGeneratorTest {

    @Test
    public void test() throws IOException {
        String[] row = new String[]{"1", "2", "", "3"};
        String s = on("\t").join(row) + "\n";
        s += s;

        Table table = new Table();
        table.addRow(asList(row));
        table.addRow(asList(row));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new TsvGenerator(table).generate(out);
        String s1 = out.toString(UTF_8.name());
        assertEquals(s, s1);

        String s2 = new TsvGenerator(table).generateString();
        assertEquals(s, s2);
    }
}
