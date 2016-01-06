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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.magetab.tsv;

import com.google.common.base.Charsets;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.io.Closeables.close;

/**
 * @author Olga Melnichuk
 */
public class TsvParser {

    private Table table;

    private CharacterStats stats;

    private StringBuilder buff;

    private boolean escape;

    private Character prev;

    private List<String> values;

    public Table parse(InputStream in) throws IOException {
        return parse(in, new TsvLineVisitor() {
            @Override
            public boolean accepts(List<String> values) {
                return true;
            }
        });
    }

    public Table parse(InputStream in, TsvLineVisitor visitor) throws IOException {
        reset();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in, Charsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                processLine(line + '\n', visitor);
            }
        } finally {
            close(br, true);
        }
        if (!stats.isReadable()) {
            throw new IOException("The file doesn't look like a text file");
        }
        return table;
    }

    private void reset() {
        stats = new CharacterStats();
        table = new Table();
        buff = new StringBuilder();
        values = new ArrayList<String>();
        escape = false;
        prev = null;
    }

    private void processLine(String line, TsvLineVisitor visitor) {
        for (int i = 0; i < line.length(); i++) {
            processCharacter(line.charAt(i), visitor);
        }
    }

    private void processCharacter(char ch, TsvLineVisitor vistior) {
        switch (ch) {
            case '"':
                if (prev != null && prev == '\\') {
                    replaceLastChar('"');
                } else {
                    escape = !escape;
                }
                break;
            case '\t':
                if (escape) {
                    addChar('\t');
                } else {
                    addValue();
                }
                break;
            case '\n':
                if (escape) {
                    addChar('\n');
                } else {
                    addValue();
                    addNewLine(vistior);
                }
                break;
            default:
                addChar(ch);
        }
        prev = ch;
    }

    private void addChar(char ch) {
        addOrReplaceChar(ch, false);
    }

    private void replaceLastChar(char ch) {
        addOrReplaceChar(ch, true);
    }

    private void addOrReplaceChar(char ch, boolean replaceLast) {
        if (replaceLast) {
            buff.delete(buff.length() - 1, buff.length());
        }
        buff.append(ch);
        stats.addCharacter(ch);
    }

    private void addValue() {
        values.add(buff.toString().trim());
        buff = new StringBuilder();
    }

    private void addNewLine(TsvLineVisitor visitor) {
        if (visitor.accepts(values)) {
            table.addRow(values);
        }
        values = new ArrayList<String>();
    }

    private static class CharacterStats {

        private int total;

        private int recognized;

        public void addCharacter(char ch) {
            if (!Character.isSpaceChar(ch)) {
                recognized += (Character.isLetterOrDigit(ch) ? 1 : 0);
                total++;
            }
        }

        public boolean isReadable() {
            return total == 0 || (1.0 * recognized / total) > 0.7;
        }
    }
}
