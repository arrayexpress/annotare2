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

import uk.ac.ebi.fg.annotare2.submissionmodel.Experiment;
import uk.ac.ebi.fg.annotare2.submissionmodel.Extract;
import uk.ac.ebi.fg.annotare2.submissionmodel.Sample;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentType;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.ExperimentSetting.allSettingsAsMap;

/**
 * @author Olga Melnichuk
 */
public class ExperimentFactory {

    private static enum Builder {
        ONE_COLOR_EXPERIMENT_BUILDER(ExperimentType.ONE_COLOR_MICROARRAY) {
            @Override
            Experiment setupExperiment(Experiment experiment, ExperimentSetupSettings settings) {
                int n = settings.getNumberOfHybs();
                for (int i = 0; i < n; i++) {
                    Sample sample = experiment.createSample();
                    Extract extract = experiment.createExtract();
                    sample.addExtract(extract);
                }
                return experiment;
            }
        },
        TWO_COLOR_EXPERIMENT_BUILDER(ExperimentType.TWO_COLOR_MICROARRAY) {
            @Override
            Experiment setupExperiment(Experiment experiment, ExperimentSetupSettings settings) {
                return experiment;
            }
        },
        SEQUENCING_EXPERIMENT_BUILDER(ExperimentType.SEQUENCING) {
            @Override
            Experiment setupExperiment(Experiment experiment, ExperimentSetupSettings settings) {
                return experiment;
            }
        };

        public static Map<ExperimentType, Builder> map = newHashMap();

        static {
            for (Builder b : Builder.values()) {
                map.put(b.type, b);
            }
        }

        private ExperimentType type;

        private Builder(ExperimentType type) {
            this.type = type;
        }

        abstract Experiment setupExperiment(Experiment experiment, ExperimentSetupSettings settings);

        private static Builder find(ExperimentSetupSettings settings) {
            return map.get(settings.getExperimentType());
        }

        public static Experiment build(ExperimentSetupSettings settings) {
            Builder b = find(settings);
            if (b == null) {
                throw new IllegalStateException("Can't build an experiment with null type");
            }
            Experiment experiment = new Experiment(allSettingsAsMap(settings));
            return b.setupExperiment(experiment, settings);
        }
    }

    public static Experiment createExperiment(ExperimentSetupSettings settings) {
        return Builder.build(settings);
    }


}
