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

package uk.ac.ebi.fg.annotare2.web.server.magetab.adf;

import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;
import uk.ac.ebi.fg.annotare2.web.server.magetab.tsv.TsvLineVisitor;
import uk.ac.ebi.fg.annotare2.web.server.magetab.tsv.TsvParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class AdfParser {
    private static final Set<String> adfTitles = new HashSet<String>();

    static {
        adfTitles.addAll(asList(
                "Block Column",
                "Block Row",
                "Column",
                "Row"
        ));
    }

    public Table parseHeader(InputStream in) throws IOException {
        return (new TsvParser()).parse(in, new TsvLineVisitor() {

            private boolean inHeader = true;

            @Override
            public boolean accepts(List<String> values) {
                if (inHeader) {
                    for (String v : values) {
                        if (adfTitles.contains(v)) {
                            inHeader = false;
                            break;
                        }
                    }
                }
                return inHeader;
            }
        });
    }

    public Table parseBody(InputStream in) throws IOException {
        return (new TsvParser()).parse(in, new TsvLineVisitor() {
            private boolean inBody = false;

            @Override
            public boolean accepts(List<String> values) {
                if (!inBody) {
                    for (String v : values) {
                        if (adfTitles.contains(v)) {
                            inBody = true;
                            break;
                        }
                    }
                }
                return inBody;
            }
        });
    }
}
