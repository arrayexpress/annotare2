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

package uk.ac.ebi.fg.annotare2.core.data;

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
import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.*;

/**
 * @author Olga Melnichuk
 */
public class ProtocolTypes {

    private static final Logger log = LoggerFactory.getLogger(ProtocolTypes.class);

    private static final String FILE = "/ProtocolTypes.json";

    private Map<String, Config> types = new HashMap<>();

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
        private final List<Usage> usage;

        @JsonProperty("subject")
        private final ProtocolSubjectType subjectType;

        @JsonProperty("precedence")
        private final Integer precedence;

        @JsonProperty("definition")
        private final String definition;

        private boolean flag;

        public Config(@JsonProperty("label") String label,
                      @JsonProperty("id") String id,
                      @JsonProperty("usage") ArrayList<Usage> usage,
                      @JsonProperty("subject") ProtocolSubjectType subjectType,
                      @JsonProperty("precedence") Integer precedence,
                      @JsonProperty("definition") String definition) {
            this.label = checkNotNull(label);
            this.id = checkNotNull(id);
            this.usage = checkNotNull(usage);
            this.subjectType = checkNotNull(subjectType);
            this.precedence = checkNotNull(precedence);
            this.definition = checkNotNull(definition);
            this.flag = false;
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
            for (Usage use: usage
                 ) {
                if(flag = use.isOkay(expType))
                {
                    return true;
                }

            }
            return flag;
        }

        private static <T> T checkNotNull(T t) {
            if (t == null) {
                throw new NullPointerException("t == null");
            }
            return t;
        }
    }

    private enum Usage {
        MICRO_ARRAY(of(ONE_COLOR_MICROARRAY, TWO_COLOR_MICROARRAY, HUMAN_ONE_COLOR_MICROARRAY, HUMAN_TWO_COLOR_MICROARRAY, CELL_LINE_ONE_COLOR_MICROARRAY, CELL_LINE_TWO_COLOR_MICROARRAY, ANIMAL_ONE_COLOR_MICROARRAY, ANIMAL_TWO_COLOR_MICROARRAY)),
        SEQUENCING(of(ExperimentProfileType.SEQUENCING, HUMAN_SEQUENCING, CELL_LINE_SEQUENCING, ANIMAL_SEQUENCING, SINGLE_CELL_SEQUENCING, SINGLE_CELL_HUMAN_SEQUENCING, SINGLE_CELL_CELL_LINE_SEQUENCING, SINGLE_CELL_ANIMAL_SEQUENCING, SINGLE_CELL_PLANT_SEQUENCING)),
        PLANT_SEQUENCING(of(ExperimentProfileType.PLANT_SEQUENCING)),
        PLANT_MICRO_ARRAY(of(PLANT_ONE_COLOR_MICROARRAY, PLANT_TWO_COLOR_MICROARRAY)),
        ANY(allOf(ExperimentProfileType.class)),
        NONE(noneOf(ExperimentProfileType.class));

        private final EnumSet<ExperimentProfileType> set;

        Usage(EnumSet<ExperimentProfileType> set) {
            this.set = set;
        }

        private boolean isOkay(ExperimentProfileType expType) {
            return set.contains(expType);
        }
    }
}
