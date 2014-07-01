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

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.web.server.services.EmailSender;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.google.common.io.Closeables.close;
import static java.util.concurrent.TimeUnit.HOURS;

/**
 * List of arrays from ArrayExpress.
 *
 * @author Olga Melnichuk
 */
public class ArrayExpressArrayDesignList extends AbstractIdleService {

    private static final Logger log = LoggerFactory.getLogger(ArrayExpressArrayDesignList.class);
    private static final String arrayDesignListLocation =
            System.getProperty("java.io.tmpdir") +
            System.getProperty("file.separator") +
            "ArrayExpressArrayDesigns.txt";

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final ArrayExpressProperties properties;
    private final EmailSender emailer;

    private ArrayDesignList list;

    @Inject
    public ArrayExpressArrayDesignList(ArrayExpressProperties properties, EmailSender emailer) throws IOException {
        this.properties = properties;
        this.emailer = emailer;
        reload();
    }

    @Override
    public void startUp() throws Exception {
        final Runnable periodicProcess = new Runnable() {
            @Override
            public void run() {
                try {
                    update();
                    reload();
                } catch (Throwable x) {
                    log.error("AE array design list update process caught an exception:", x);
                    emailer.sendException("Error in AE array design list update process:", x);
                }
            }

        };
        scheduler.scheduleAtFixedRate(periodicProcess, 0, 1, HOURS);
    }

    @Override
    public void shutDown() throws Exception {
        scheduler.shutdown();
    }


    private ArrayDesignList load() throws IOException {
        ArrayDesignList list = new ArrayDesignList();

        File adFile = new File(arrayDesignListLocation);
        if (adFile.exists() && adFile.canRead()) {
            list.load(new FileInputStream(adFile));
        }

        if (list.isEmpty()) {
            InputStream in = getClass().getResourceAsStream("/ArrayExpressArrayDesigns-01072014.txt");
            list.load(in);
        }
        return list;
    }

    public void update() throws IOException {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            URL adSource = new URL(properties.getArrayExpressArrayListURL());
            is = adSource.openStream();
            if (null != is) {
                ReadableByteChannel rbc = Channels.newChannel(is);
                fos = new FileOutputStream(arrayDesignListLocation);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
        } finally {
            close(is, true);
            close(fos, true);
        }
    }

    public void reload() throws IOException {
        list = load();
    }

    public Iterable<ArrayExpress.ArrayDesign> getArrayDesigns(String query) {
        return list.getArrayDesigns(query);
    }

    private static class ArrayDesignList {
        private final ConcurrentRadixTree<ArrayExpress.ArrayDesign> trie;
        private boolean isEmpty = true;

        public ArrayDesignList() {
            trie = new ConcurrentRadixTree<ArrayExpress.ArrayDesign>(new DefaultCharArrayNodeFactory());
        }

        private void load(InputStream in) throws IOException {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    ArrayExpress.ArrayDesign ad = createArrayDesign(line);
                    if (ad != null) {
                        trie.put(ad.getName().toLowerCase(), ad);
                        trie.put(ad.getDesription().toLowerCase(), ad);
                        isEmpty = false;
                    }
                }
            } finally {
                close(in, true);
            }
        }

        private boolean isEmpty() {
            return isEmpty;
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
    }
}
