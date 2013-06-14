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

package uk.ac.ebi.fg.annotare2.web.server.rpc.transform;

import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.configmodel.Sample;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentConfigType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static uk.ac.ebi.fg.annotare2.configmodel.ExperimentConfigType.*;

/**
 * @author Olga Melnichuk
 */
public class ExperimentFactory {

    private static enum Builder {
        ONE_COLOR_EXPERIMENT_BUILDER(ONE_COLOR_MICROARRAY) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(ONE_COLOR_MICROARRAY);
                int n = settings.getNumberOfHybs();
                for (int i = 0; i < n; i++) {
                    Sample sample = exp.createSample();
                    //exp.assignLabel(sample, settings.getLabel());
                }
                return exp;
            }
        },
        TWO_COLOR_EXPERIMENT_BUILDER(TWO_COLOR_MICROARRAY) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                return new ExperimentProfile(TWO_COLOR_MICROARRAY);
            }
        },
        SEQUENCING_EXPERIMENT_BUILDER(SEQUENCING) {
            @Override
            ExperimentProfile setupExperiment( ExperimentSetupSettings settings) {
                return new ExperimentProfile(SEQUENCING);
            }
        };

        public static Map<ExperimentConfigType, Builder> map = newHashMap();

        static {
            for (Builder b : Builder.values()) {
                map.put(b.type, b);
            }
        }

        private ExperimentConfigType type;

        private Builder(ExperimentConfigType type) {
            this.type = type;
        }

        abstract ExperimentProfile setupExperiment(ExperimentSetupSettings settings);

        private static Builder find(ExperimentSetupSettings settings) {
            return map.get(settings.getExperimentType());
        }

        public static ExperimentProfile build(ExperimentSetupSettings settings) {
            Builder b = find(settings);
            if (b == null) {
                throw new IllegalStateException("Can't build an experiment with null type");
            }
            return b.setupExperiment(settings);
        }
    }

    public static ExperimentProfile createExperiment(ExperimentSetupSettings settings) {
        return Builder.build(settings);
    }


}
