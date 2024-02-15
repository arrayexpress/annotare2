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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.services.ae;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.ae.BiostudiesProperties;
import uk.ac.ebi.fg.annotare2.core.components.Messenger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.io.Closeables.close;
import static java.util.concurrent.TimeUnit.HOURS;

/**
 * List of arrays from ArrayExpress.
 *
 * @author Olga Melnichuk
 */
public class ArrayExpressArrayDesignList {

    private static final Logger log = LoggerFactory.getLogger(ArrayExpressArrayDesignList.class);
    private static final String arrayDesignListLocation =
            System.getProperty("java.io.tmpdir") +
            System.getProperty("file.separator") +
            "ArrayExpressArrayDesigns.txt";

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final BiostudiesProperties properties;
    private final Messenger messenger;

    private ArrayDesignList list;

    @Inject
    public ArrayExpressArrayDesignList(BiostudiesProperties properties, Messenger messenger) throws IOException {
        this.properties = properties;
        this.messenger = messenger;
        reload();
    }

    @PostConstruct
    public void startUp() throws Exception {
        final Runnable periodicProcess = new Runnable() {
            @Override
            public void run() {
                try {
                    update();
                    reload();
                } catch (Throwable x) {
                    log.error("AE array design list update process caught an exception:", x);
                    messenger.send("Error in AE array design list update process:", x);
                }
            }

        };
        if (!isNullOrEmpty(properties.getBiostudiesArrayListUrl())) {
            scheduler.scheduleAtFixedRate(periodicProcess, 0, 1, HOURS);
        }
    }

    @PreDestroy
    public void shutDown() throws Exception {
        scheduler.shutdown();
    }


    private ArrayDesignList load() throws IOException {
        ArrayDesignList list = new ArrayDesignList();

        File adFile = new File(arrayDesignListLocation);
        if (adFile.exists() && adFile.canRead()) {
            list.load(Files.newInputStream(adFile.toPath()));
        }
        return list;
    }

    public void update() throws IOException {
        try(Stream<String> lines = Files.lines(Paths.get(properties.getBiostudiesArrayListUrl()), StandardCharsets.UTF_8);
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(arrayDesignListLocation), StandardCharsets.UTF_8)) {
            AtomicInteger lineIndex = new AtomicInteger(0);
            lines.forEachOrdered(l->{
                String[] parts = l.split("\t");
                if(parts.length >= 4){
                    String outputLine = lineIndex.getAndIncrement() + "\t" + parts[0] + "\t" + parts[3];
                    try {
                        writer.write(outputLine);
                        writer.newLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
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
                    if (null != ad && null != ad.getAccession()) {
                        trie.put(ad.getAccession().toLowerCase(), ad);
                        if (null != ad.getName()) {
                            for (String word : ad.getName().toLowerCase().split("\\s+")) {
                                trie.put(word + "___" + ad.getId(), ad);
                            }
                        }
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
            Set<ArrayExpress.ArrayDesign> ads = null;
            if (null == query) {
                ads = ImmutableSet.copyOf(trie.getValuesForKeysStartingWith(""));
            } else {
                for (String word : query.toLowerCase().split("\\s+")) {
                    Set<ArrayExpress.ArrayDesign> matches = ImmutableSet.copyOf(trie.getValuesForClosestKeys(word));
                    if (null != ads) {
                        ads = Sets.intersection(ads, matches);
                    } else {
                        ads = matches;
                    }
                    if (ads.isEmpty()) {
                        return Collections.emptySet();
                    }
                }
            }

            if (null == ads) {
                return Collections.emptySet();
            }

            return Ordering.natural().onResultOf(new Function<ArrayExpress.ArrayDesign, String>() {
                @Override
                public String apply(ArrayExpress.ArrayDesign ad) {
                    if (null == ad || null == ad.getAccession()) {
                        return null;
                    }

                    String pipeline = ad.getAccession().substring(2, 6);
                    String index = ad.getAccession().substring(7).replaceAll("^([0-9]+).*", "$1");

                    return ("GEOD".equalsIgnoreCase(pipeline) ? "ZZZZ" : pipeline) + String.format("%09d", Integer.valueOf(index));
                }
            }).sortedCopy(ads);
        }
    }
}
