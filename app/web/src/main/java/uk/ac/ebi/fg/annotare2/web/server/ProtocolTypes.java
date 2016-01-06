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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Ordering;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.submission.model.ProtocolSubjectType;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.util.EnumSet.*;
import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.ONE_COLOR_MICROARRAY;
import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.TWO_COLOR_MICROARRAY;

/**
 * @author Olga Melnichuk
 */
public class ProtocolTypes {

    private static final Logger log = LoggerFactory.getLogger(ProtocolTypes.class);

    private static final String FILE = "/ProtocolTypes.json";

    private Map<String, Config> types = new HashMap<String, Config>();

    public ProtocolTypes() {
    }

    public ProtocolTypes(List<Config> types) {
        for (Config type : types) {
            this.types.put(type.getId(), type);
        }
    }

    public static ProtocolTypes create() {
        InputStream in = ProtocolTypes.class.getResourceAsStream(FILE);
        try {
            if (in != null) {
                List<Config> records =
                        new ObjectMapper().readValue(
                                in,
                                new TypeReference<List<Config>>() {
                                });
                return new ProtocolTypes(records);
            } else {
                log.error(FILE + " was not found");
            }
        } catch (IOException e) {
            log.error("Unable to parse " + FILE, e);
        }
        return new ProtocolTypes();
    }

    public Collection<Config> filter(final ExperimentProfileType expType) {
        return Ordering.natural().onResultOf(
                new Function<Config, Integer>() {
                    @Override
                    public Integer apply(Config protocol) {
                        return protocol.getPrecedence();
                    }
                }).sortedCopy(
                Collections2.filter(types.values(), new Predicate<Config>() {
                    @Override
                    public boolean apply(@Nullable Config config) {
                        return config != null && config.isUsedIn(expType);
                    }
                })
        );
    }

    public Integer getPrecedence(String id) {
        if (types.containsKey(id)) {
            return types.get(id).getPrecedence();
        }
        return null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Config {

        @JsonProperty("label")
        private final String label;

        @JsonProperty("id")
        private final String id;

        @JsonProperty("usage")
        private final Usage usage;

        @JsonProperty("subject")
        private final ProtocolSubjectType subjectType;

        @JsonProperty("precedence")
        private final Integer precedence;

        @JsonProperty("definition")
        private final String definition;

        public Config(@JsonProperty("label") String label,
                      @JsonProperty("id") String id,
                      @JsonProperty("usage") Usage usage,
                      @JsonProperty("subject") ProtocolSubjectType subjectType,
                      @JsonProperty("precedence") Integer precedence,
                      @JsonProperty("definition") String definition) {
            this.label = checkNotNull(label);
            this.id = checkNotNull(id);
            this.usage = checkNotNull(usage);
            this.subjectType = checkNotNull(subjectType);
            this.precedence = checkNotNull(precedence);
            this.definition = checkNotNull(definition);
        }

        public String getId() {
            return id;
        }

        public String getDefinition() {
            return definition;
        }

        public ProtocolSubjectType getSubjectType() {
            return subjectType;
        }

        public Integer getPrecedence() {
            return precedence;
        }

        private boolean isUsedIn(ExperimentProfileType expType) {
            return usage.isOkay(expType);
        }

        private static <T> T checkNotNull(T t) {
            if (t == null) {
                throw new NullPointerException("t == null");
            }
            return t;
        }
    }

    private static enum Usage {
        MICRO_ARRAY(of(ONE_COLOR_MICROARRAY, TWO_COLOR_MICROARRAY)),
        SEQUENCING(of(ExperimentProfileType.SEQUENCING)),
        ANY(allOf(ExperimentProfileType.class)),
        NONE(noneOf(ExperimentProfileType.class));

        private final EnumSet<ExperimentProfileType> set;

        private Usage(EnumSet<ExperimentProfileType> set) {
            this.set = set;
        }

        private boolean isOkay(ExperimentProfileType expType) {
            return set.contains(expType);
        }
    }
}
