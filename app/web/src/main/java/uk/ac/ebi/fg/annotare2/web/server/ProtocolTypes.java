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

package uk.ac.ebi.fg.annotare2.web.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.submission.model.ProtocolTargetType;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.EnumSet.*;
import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.ONE_COLOR_MICROARRAY;
import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.TWO_COLOR_MICROARRAY;

/**
 * @author Olga Melnichuk
 */
public class ProtocolTypes {

    private static final Logger log = LoggerFactory.getLogger(ProtocolTypes.class);

    private static final String FILE = "/ProtocolTypes.json";

    private List<Config> types = newArrayList();

    public ProtocolTypes() {
    }

    public ProtocolTypes(List<Config> types) {
        this.types.addAll(types);
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
            log.error("Can't parse " + FILE, e);
        }
        return new ProtocolTypes();
    }

    public Collection<Config> filter(final ExperimentProfileType expType) {
         return Collections2.filter(types, new Predicate<Config>() {
             @Override
             public boolean apply(@Nullable Config config) {
                 return config != null && config.isUsedIn(expType);
             }
         });
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Config {

        @JsonProperty("label")
        private final String label;

        @JsonProperty("id")
        private final String id;

        @JsonProperty("usage")
        private final Usage usage;

        @JsonProperty("target")
        private final ProtocolTargetType targetType;

        @JsonProperty("definition")
        private final String definition;

        public Config(@JsonProperty("label") String label,
                      @JsonProperty("id") String id,
                      @JsonProperty("usage") Usage usage,
                      @JsonProperty("target") ProtocolTargetType usageType,
                      @JsonProperty("definition") String definition) {
            this.label = label;
            this.id = id;
            this.usage = usage;
            this.targetType = usageType;
            this.definition = definition;
        }

        public String getId() {
            return id;
        }

        public String getDefinition() {
            return definition;
        }

        public ProtocolTargetType getTargetType() {
            return targetType;
        }

        private boolean isUsedIn(ExperimentProfileType expType) {
            return usage.isOkay(expType);
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
