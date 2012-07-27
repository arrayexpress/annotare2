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

package uk.ac.ebi.fg.annotare2.magetab.parser.table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import static com.google.common.io.Closeables.closeQuietly;
import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
class TsvParser {

    Table parse(InputStream in) throws IOException {
        final Table table = new Table();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in));
            String line;
            int lc = -1;
            while ((line = br.readLine()) != null) {
                lc++;
                table.addRow(lc, parseRow(line));
            }
        } finally {
            closeQuietly(br);
        }
        return table;
    }

    private Collection<String> parseRow(String line) {
        return asList(line.trim().split("\t"));
    }
}
