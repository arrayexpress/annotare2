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

/**
 * @author Olga Melnichuk
 */
public class TokenReader {

    private String token;

    private String delimiter;

    private int start = -1;

    private int end;

    public TokenReader(String token) {
        this(token, TokenBuilder.DELIMITER);
    }

    public TokenReader(String token, String delimiter) {
        this.token = token;
        this.delimiter = delimiter;
    }

    public boolean nextBoolean() throws TokenReaderException {
        String t = nextPart();
        boolean b = Boolean.parseBoolean(t);
        if (b) {
            return true;
        }
        if ("false".equalsIgnoreCase(t)) {
            return false;
        }
        swap();
        throw new TokenReaderException("Unrecognized boolean value: '" + t + "'");
    }

    public int nextInt() throws TokenReaderException {
        String t = nextPart();
        try {
            return Integer.parseInt(t);
        } catch (NumberFormatException e) {
            swap();
            throw new TokenReaderException("Unrecognized integer value: '" + t + "'");
        }
    }

    public String nextString() throws TokenReaderException {
        return nextPart();
    }

    private String nextPart() throws TokenReaderException {
        if (token == null || start >= token.length()) {
            throw new TokenReaderException("Premature end of token");
        }
        end = token.indexOf(delimiter, start + 1);
        String part;
        if (end >= 0) {
            part = token.substring(start + 1, end);
        } else {
            part = token.substring(start + 1);
            end = token.length();
        }
        swap();
        return part;
    }

    private void swap() {
        int t = start;
        start = end;
        end = t;
    }
}
