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

package uk.ac.ebi.fg.annotare2.web.server.services;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.server.services.utils.EfoSubTree;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
public class FileBasedProtocolFilter implements ProtocolFilter {

    private static final Logger log = LoggerFactory.getLogger(FileBasedProtocolFilter.class);

    private static final String FILE = "/ProtocolFilter.json";

    private final List<FilterRecord> records;

    public FileBasedProtocolFilter(Collection<FilterRecord> records) {
        this.records  = newArrayList(records);
    }

    @Override
    public EfoSubTree filter(ExperimentProfileType type, EfoSubTree protocols) {
        //TODO
        return null;
    }

    public static ProtocolFilter create() {
        InputStream in = FileBasedProtocolFilter.class.getResourceAsStream(FILE);
        if (in == null) {
            log.debug(FILE + " was not found");
            return null;
        }

        try {
            List<FilterRecord> records = (new ObjectMapper()).readValue(in,
                    new TypeReference<List<FilterRecord>>() {
                    });
            return new FileBasedProtocolFilter(records);
        } catch (IOException e) {
            log.error("Can't parse " + FILE, e);
            return null;
        }
    }

    private static class FilterRecord {

        @JsonProperty("label")
        private final String label;

        @JsonProperty("id")
        private final String id;

        @JsonProperty("type")
        private final RecordType type;

        @JsonProperty("usage")
        private final RecordUsage usage;

        private FilterRecord( @JsonProperty("label") String label,
                              @JsonProperty("id") String id,
                              @JsonProperty("type") RecordType type,
                              @JsonProperty("usage") RecordUsage usage) {
            this.label = label;
            this.id = id;
            this.type = type;
            this.usage = usage;
        }

        private String getId() {
            return id;
        }
    }

    private static enum RecordType {
        PROTOCOL, PROTOCOL_TYPE, NONE
    }

    private static enum RecordUsage {
        MICRO_ARRAY, SEQUENCING, ANY, NONE
    }
}
