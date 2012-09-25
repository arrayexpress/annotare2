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

package uk.ac.ebi.fg.annotare2.web.gwt.common.client.place;

import java.util.ArrayList;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class CompositeToken {

    private static final String DELIMITER = ",";

    private ArrayList<String> tokens = new ArrayList<String>();

    public CompositeToken(String token) {
        this.tokens = new ArrayList<String>(asList(token.split(DELIMITER)));
    }

    public CompositeToken() {
    }

    public CompositeToken add(String t) {
        tokens.add(t);
        return this;
    }

    public CompositeToken add(int t) {
        tokens.add(Integer.toString(t));
        return this;
    }

    public CompositeToken add(boolean t) {
        tokens.add(Boolean.toString(t));
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

    public Reader reader() {

        return new Reader() {

            private int i = 0;

            @Override
            public boolean nextBoolean() throws TokenReaderException {
                checkIndex(i);
                String t = tokens.get(i++);
                try {
                    return Boolean.parseBoolean(t);
                } catch (NumberFormatException e) {
                    throw new TokenReaderException("Bad boolean token: '" + t + "'");
                }
            }

            @Override
            public int nextInt() throws TokenReaderException {
                checkIndex(i);
                String t = tokens.get(i++);
                try {
                    return Integer.parseInt(t);
                } catch (NumberFormatException e) {
                    throw new TokenReaderException("Bad integer token: '" + t + "'");
                }
            }

            @Override
            public String nextString() throws TokenReaderException {
                checkIndex(i);
                return tokens.get(i++);
            }

            private void checkIndex(int i) throws TokenReaderException {
                if (i >= tokens.size()) {
                    throw new TokenReaderException("Token index out of bound:" + i + " >= " + tokens.size());
                }
            }
        };
    }

    public static interface Reader {

        int nextInt() throws TokenReaderException;

        String nextString() throws TokenReaderException;

        boolean nextBoolean() throws TokenReaderException;
    }
}
