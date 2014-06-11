/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.server.services.ae;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.google.common.io.Closeables.close;

/**
 * List of arrays from ArrayExpress.
 *
 * @author Olga Melnichuk
 */
public class ArrayExpressArrayDesignList {

    private static final Logger log = LoggerFactory.getLogger(ArrayExpressArrayDesignList.class);

    private final ConcurrentRadixTree<ArrayExpress.ArrayDesign> trie;

    public ArrayExpressArrayDesignList() {
        trie = new ConcurrentRadixTree<ArrayExpress.ArrayDesign>(new DefaultCharArrayNodeFactory());
    }

    private ArrayExpressArrayDesignList load() throws IOException {
        InputStream in = getClass().getResourceAsStream("/ArrayExpressArrayDesigns-16042013.txt");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                ArrayExpress.ArrayDesign ad = createArrayDesign(line);
                if (ad != null) {
                    trie.put(ad.getName().toLowerCase(), ad);
                    trie.put(ad.getDesription().toLowerCase(), ad);
                }
            }
            return this;
        } finally {
            close(in, true);
        }
    }

    private ArrayExpress.ArrayDesign createArrayDesign(String line) {
        String[] parts = line.split("\t");
        if (parts.length != 3) {
            return null;
        }
        try {
            return new ArrayExpress.ArrayDesign(Integer.parseInt(parts[0]), parts[1], parts[2]);
        } catch (NumberFormatException e) {
            log.error("Unable to parse array design: [" + line + "]", e);
            return null;
        }
    }

    public Iterable<ArrayExpress.ArrayDesign> getArrayDesigns(String query) {
        return trie.getValuesForClosestKeys(query.toLowerCase());
    }

    public static ArrayExpressArrayDesignList create() {
        ArrayExpressArrayDesignList list = new ArrayExpressArrayDesignList();
        try {
            return list.load();
        } catch (IOException e) {
            log.error("Unable to load AE array design list", e);
        }
        return list;
    }
}
