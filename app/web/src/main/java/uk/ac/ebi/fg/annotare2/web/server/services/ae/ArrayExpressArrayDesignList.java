/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

import com.google.common.io.Closeables;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import org.apache.log4j.helpers.LogLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * List of arrays from ArrayExpress.
 *
 * @author Olga Melnichuk
 */
public class ArrayExpressArrayDesignList {

    private static final Logger log = LoggerFactory.getLogger(ArrayExpressArrayDesignList.class);

    private final ConcurrentRadixTree<AE.ArrayDesign> trie;

    public ArrayExpressArrayDesignList() {
        trie = new ConcurrentRadixTree<AE.ArrayDesign>(new DefaultCharArrayNodeFactory());
    }

    private ArrayExpressArrayDesignList load() throws IOException {
        InputStream in = ArrayExpressArrayDesignList.class.getResourceAsStream("/ArrayExpressArrayDesigns-16042013.txt");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                AE.ArrayDesign ad = createArrayDesign(line);
                if (ad != null) {
                    trie.put(ad.getName(), ad);
                    trie.put(ad.getDesription(), ad);
                }
            }
            return this;
        } finally {
            Closeables.close(in, true);
        }
    }

    private AE.ArrayDesign createArrayDesign(String line) {
        String[] parts = line.split("\t");
        if (parts.length != 3) {
            return null;
        }
        try {
            return new AE.ArrayDesign(Integer.parseInt(parts[0]), parts[1], parts[2]);
        } catch (NumberFormatException e) {
            log.error("Can't parse Array Design: [" + line + "]", e);
            return null;
        }
    }

    public Iterable<AE.ArrayDesign> getArrayDesigns(String query) {
        return trie.getValuesForClosestKeys(query);
    }

    public static ArrayExpressArrayDesignList create() {
        ArrayExpressArrayDesignList list = new ArrayExpressArrayDesignList();
        try {
            return list.load();
        } catch (IOException e) {
            log.error("Can't load array designs", e);
        }
        return list;
    }
}
