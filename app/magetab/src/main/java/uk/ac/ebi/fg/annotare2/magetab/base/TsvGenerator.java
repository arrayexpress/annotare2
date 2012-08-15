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

package uk.ac.ebi.fg.annotare2.magetab.base;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Joiner.on;

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
        int numberOfColumns = table.lastColumnIndex() + 1;

        for (int i = 0; i < table.getRowCount(); i++) {
            List<String> row = new ArrayList<String>();
            for (int j = 0; j < numberOfColumns; j++) {
                Table.Value v = table.getValueAt(i, j);
                row.add(v == null || v.isEmpty() ? "" : v.getValue());
            }
            writer.write(on("\t").join(row) + "\n");
        }
        writer.flush();
    }
}
