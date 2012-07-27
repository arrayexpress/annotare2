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

package uk.ac.ebi.fg.annotare2.magetab.parser;

import org.junit.Test;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.fg.annotare2.magetab.idf.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static com.google.common.base.Joiner.on;
import static com.google.common.io.Closeables.closeQuietly;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * @author Olga Melnichuk
 */
public class IdfParserTest {

    private static final String IDF_FILE = "/E-TABM-1009.idf.txt";

    private static enum IdfField {
        Title("Investigation Title") {
            @Override
            String generate(Investigation inv) {
                return join(asList(inv.getTitle()));
            }
        },
        Description("Experiment Description") {
            @Override
            String generate(Investigation inv) {
                return join(asList(inv.getDescription()));
            }
        },
        Accession("Investigation Accession") {
            @Override
            String generate(Investigation inv) {
                return join(asList(inv.getAccession().getAccession()));
            }
        },
        PersonFistName("Person First Name") {
            @Override
            String generate(Investigation inv) {
                List<String> names = new ArrayList<String>();
                for (Person p : inv.getContacts()) {
                    names.add(p.getFirstName());
                }
                return join(names);
            }
        },
        PersonLastName("Person Last Name") {
            @Override
            String generate(Investigation inv) {
                List<String> names = new ArrayList<String>();
                for (Person p : inv.getContacts()) {
                    names.add(p.getLastName());
                }
                return join(names);
            }
        },
        ProtocolName("Protocol Name") {
            @Override
            String generate(Investigation inv) {
                List<String> names = new ArrayList<String>();
                for (Protocol p : inv.getProtocols()) {
                    names.add(p.getName());
                }
                return join(names);
            }
        },
        ProtocolType("Protocol Type") {
            @Override
            String generate(Investigation inv) {
                List<String> names = new ArrayList<String>();
                for (Protocol p : inv.getProtocols()) {
                    names.add(p.getType().getName());
                }
                return join(names);
            }
        },
        ProtocolParameters("Protocol Parameters") {
            @Override
            String generate(Investigation inv) {
                List<String> names = new ArrayList<String>();
                for (Protocol p : inv.getProtocols()) {
                    if (!p.getParameters().isEmpty()) {
                        names.add(on(";").join(p.getParameters()));
                    }
                }
                return join(names);
            }
        },
        PublicationTitle("Publication Title") {
            @Override
            String generate(Investigation inv) {
                List<String> list = new ArrayList<String>();
                for (Publication p : inv.getPublications()) {
                    list.add(p.getTitle());
                }
                return join(list);
            }
        },
        QualityControlType("Quality Control Type") {
            @Override
            String generate(Investigation inv) {
                List<String> list = new ArrayList<String>();
                for (QualityControl p : inv.getQualityControls()) {
                    list.add(p.getType().getName());
                }
                return join(list);
            }
        },
        SdrfFile("SDRF File") {
            @Override
            String generate(Investigation inv) {
                return join(inv.getSdrfFiles());
            }
        };

        private String title;

        private IdfField(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        private static String join(Collection<String> values) {
            return on("\t").join(values);
        }

        abstract String generate(Investigation inv);
    }

    @Test
    public void testIdfParser() throws ParseException, MageTabParseException, IOException {
        IdfParser parser = new IdfParser();
        Investigation idf = parser.parse(IdfParserTest.class.getResourceAsStream(IDF_FILE));
        assertDataEquals(idf, IDF_FILE);
    }

    private void assertDataEquals(Investigation inv, String fileName) throws IOException {
        Map<String, String> lines = parse(fileName);
        for (IdfField field : IdfField.values()) {
            String line = lines.get(field.getTitle());
            if (line != null) {
                //TODO because of limpopo parser we have values which we should not have
                // namely Comment[ArrayExpressAccession] becomes investigationAccession
                assertEquals(line, field.generate(inv));
            }
        }
    }

    private Map<String, String> parse(String fileName) throws IOException {
        Map<String, String> lines = new LinkedHashMap<String, String>();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(IdfParserTest.class.getResourceAsStream(fileName)));
            String line;
            while ((line = reader.readLine()) != null) {
                int idx = line.indexOf('\t');
                if (idx < 0) {
                    continue;
                }
                String key = line.substring(0, idx);
                String value = line.substring(idx + 1);
                lines.put(key, on("\t").join(trim(value.split("\\t"))));
            }
        } finally {
            closeQuietly(reader);
        }
        return lines;
    }

    private static List<String> trim(String[] parts) {
        List<String> list = new ArrayList<String>();
        for (String part : parts) {
            part = part.trim();
            if (!part.isEmpty()) {
                list.add(part);
            }
        }
        return list;
    }

}
