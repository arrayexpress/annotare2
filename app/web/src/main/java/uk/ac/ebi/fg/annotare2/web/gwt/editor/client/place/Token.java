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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place;

import java.util.ArrayList;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class Token {

    private static final String DELIMITER = ",";

    private ArrayList<String> tokens = new ArrayList<String>();

    public Token(String token) {
        this.tokens = new ArrayList<String>(asList(token.split(DELIMITER)));
    }

    public Token() {
    }

    public Token add(String t) {
        tokens.add(t);
        return this;
    }

    public Token add(int t) {
        tokens.add(Integer.toString(t));
        return this;
    }

    public String asString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String s : tokens) {
            sb.append(sb);
            if (++i < tokens.size()) {
                sb.append(DELIMITER);
            }
        }
        return sb.toString();
    }

    public TokenReader reader() {

        return new TokenReader() {

            private int i = 0;

            public int nextInt() throws TokenReaderException {
                checkIndexBounds(i);
                String t = tokens.get(i++);
                try {
                    return Integer.parseInt(t);
                } catch (NumberFormatException e) {
                    throw new TokenReaderException("Bad integer token: '" + t + "'");
                }
            }

            public String nextString() throws TokenReaderException {
                checkIndexBounds(i);
                return tokens.get(i++);
            }

            private void checkIndexBounds(int i) throws TokenReaderException {
                if (i >= tokens.size()) {
                    throw new TokenReaderException("Index out of bound exception; >= " + tokens.size());
                }
            }
        };
    }

    public static interface TokenReader {

        int nextInt() throws TokenReaderException;

        String nextString() throws TokenReaderException;
    }
}
