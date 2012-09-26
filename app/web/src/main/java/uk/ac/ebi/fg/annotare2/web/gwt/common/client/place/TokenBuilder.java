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
public class TokenBuilder {

    static final String DELIMITER = "/";

    private ArrayList<String> tokens = new ArrayList<String>();

    public TokenBuilder add(String t) {
        tokens.add(t);
        return this;
    }

    public TokenBuilder add(int t) {
        tokens.add(Integer.toString(t));
        return this;
    }

    public TokenBuilder add(boolean t) {
        tokens.add(Boolean.toString(t));
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String s : tokens) {
            sb.append(s);
            if (++i < tokens.size()) {
                sb.append(DELIMITER);
            }
        }
        return sb.toString();
    }
}
