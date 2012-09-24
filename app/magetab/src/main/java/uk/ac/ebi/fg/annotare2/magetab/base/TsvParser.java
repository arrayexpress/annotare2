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

import com.google.common.base.Charsets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.google.common.io.Closeables.closeQuietly;
import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class TsvParser {

    public Table parse(InputStream in) throws IOException {
        final CharacterStats stats = new CharacterStats();
        final Table table = new Table();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in, Charsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                table.addRow(parseRow(line));
                stats.add(line);
            }
        } finally {
            closeQuietly(br);
        }
        if (!stats.isReadable()) {
            throw new IOException("The file content doesn't look like a text");
        }
        return table;
    }

    private ArrayList<String> parseRow(String line) {
        ArrayList<String> list = new ArrayList<String>();
        list.addAll(asList(line.trim().split("\t")));
        return list;
    }

    private static class CharacterStats {

        private int total;

        private int recognized;

        public void add(String text) {
            text = text.replaceAll("\\s", "");
            total += text.length();
            for (int i = 0; i < text.length(); i++) {
                recognized += recognize(text.charAt(i));
            }
        }

        private int recognize(Character ch) {
            return Character.isLetterOrDigit(ch) ? 1 : 0;
        }

        public boolean isReadable() {
            return (1.0 * recognized / total) > 0.7;
        }
    }
}
