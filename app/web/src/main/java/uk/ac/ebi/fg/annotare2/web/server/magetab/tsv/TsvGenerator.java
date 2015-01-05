/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Joiner.on;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class TsvGenerator {

    private final Table table;

    public TsvGenerator(Table table) {
        this.table = table;
    }

    public void generate(OutputStream out) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        int numberOfColumns = table.getTrimmedWidth();

        for (int i = 0; i < table.getHeight(); i++) {
            List<String> row = new ArrayList<String>();
            for (int j = 0; j < numberOfColumns; j++) {
                String v = table.getValueAt(i, j);
                row.add(isNullOrEmpty(v) ? "" : v);
            }
            writer.write(on("\t").join(row) + "\n");
        }
        writer.flush();
    }

    public String generateString() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new TsvGenerator(table).generate(out);
        return out.toString(UTF_8.name());
    }
}
