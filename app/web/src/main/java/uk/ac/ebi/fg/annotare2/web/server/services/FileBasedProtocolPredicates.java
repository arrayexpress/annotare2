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

import com.google.common.base.Predicate;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.server.services.utils.EfoGraph;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.EnumSet.*;
import static uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfileType.ONE_COLOR_MICROARRAY;
import static uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfileType.TWO_COLOR_MICROARRAY;

/**
 * @author Olga Melnichuk
 */
@Deprecated
public class FileBasedProtocolPredicates implements ProtocolPredicates {

    private static final Logger log = LoggerFactory.getLogger(FileBasedProtocolPredicates.class);

    private static final String FILE = "/ProtocolFilter.json";

    private final Map<String, FilterRecord> recordMap;

    public FileBasedProtocolPredicates(Collection<FilterRecord> records) {
        recordMap = newHashMap();
        for (FilterRecord record : records) {
            recordMap.put(record.getId(), record);
        }
    }

    @Override
    public Predicate<EfoGraph.Node> createProtocolTypePredicate(final ExperimentProfileType expType) {
        return new Predicate<EfoGraph.Node>() {
            @Override
            public boolean apply(@Nullable EfoGraph.Node node) {
                FilterRecord record = recordMap.get(node.getId());
                return record == null || (record.isProtocolTYpe() && record.isUsedIn(expType));
            }
        };
    }

    @Override
    public Predicate<EfoGraph.Node> createProtocolPredicate(final ExperimentProfileType expType) {
        return new Predicate<EfoGraph.Node>() {
            @Override
            public boolean apply(@Nullable EfoGraph.Node node) {
                FilterRecord record = recordMap.get(node.getId());
                return record != null && record.isProtocol() && record.isUsedIn(expType);
            }
        };
    }

    public static ProtocolPredicates create() {
        InputStream in = FileBasedProtocolPredicates.class.getResourceAsStream(FILE);
        try {
            if (in != null) {
                List<FilterRecord> records =
                        new ObjectMapper().readValue(
                                in,
                                new TypeReference<List<FilterRecord>>() {
                                });
                return new FileBasedProtocolPredicates(records);
            } else {
                log.error(FILE + " was not found");
            }
        } catch (IOException e) {
            log.error("Can't parse " + FILE, e);
        }
        return ALWAYS_FALSE_PREDICATES;
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

        private FilterRecord(@JsonProperty("label") String label,
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

        private boolean isProtocol() {
            return type == RecordType.PROTOCOL;
        }

        private boolean isUsedIn(ExperimentProfileType expType) {
            return usage.isOkay(expType);
        }

        public boolean isProtocolTYpe() {
            return type == RecordType.PROTOCOL_TYPE;
        }
    }

    private static enum RecordType {
        PROTOCOL, PROTOCOL_TYPE, NONE
    }

    private static enum RecordUsage {
        MICRO_ARRAY(of(ONE_COLOR_MICROARRAY, TWO_COLOR_MICROARRAY)),
        SEQUENCING(of(ExperimentProfileType.SEQUENCING)),
        ANY(allOf(ExperimentProfileType.class)),
        NONE(noneOf(ExperimentProfileType.class));

        private final EnumSet<ExperimentProfileType> set;

        private RecordUsage(EnumSet<ExperimentProfileType> set) {
            this.set = set;
        }

        private boolean isOkay(ExperimentProfileType expType) {
            return set.contains(expType);
        }
    }
}
