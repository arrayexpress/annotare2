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

package uk.ac.ebi.fg.annotare2.web.server.rpc.transform;

import com.google.common.collect.Lists;
import uk.ac.ebi.fg.annotare2.db.model.User;
import uk.ac.ebi.fg.annotare2.submission.model.Contact;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExtractAttributesRow;
import uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater;

import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Maps.newHashMap;
import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.*;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater.experimentUpdater;

/**
 * @author Olga Melnichuk
 */
public class ExperimentBuilderFactory {

    private enum Builder {
        ONE_COLOR_EXPERIMENT_BUILDER(ONE_COLOR_MICROARRAY) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(ONE_COLOR_MICROARRAY);
                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("transcription profiling by array");
                return exp;
            }
        },
        PLANT_ONE_COLOR_EXPERIMENT_BUILDER(PLANT_ONE_COLOR_MICROARRAY) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(PLANT_ONE_COLOR_MICROARRAY);
                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("transcription profiling by array");
                return exp;
            }
        },
        HUMAN_ONE_COLOR_EXPERIMENT_BUILDER(HUMAN_ONE_COLOR_MICROARRAY) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(HUMAN_ONE_COLOR_MICROARRAY);
                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("transcription profiling by array");
                return exp;
            }
        },
        ANIMAL_ONE_COLOR_EXPERIMENT_BUILDER(ANIMAL_ONE_COLOR_MICROARRAY) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(ANIMAL_ONE_COLOR_MICROARRAY);
                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("transcription profiling by array");
                return exp;
            }
        },
        CELL_LINE_ONE_COLOR_EXPERIMENT_BUILDER(CELL_LINE_ONE_COLOR_MICROARRAY) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(CELL_LINE_ONE_COLOR_MICROARRAY);
                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("transcription profiling by array");
                return exp;
            }
        },
        TWO_COLOR_EXPERIMENT_BUILDER(TWO_COLOR_MICROARRAY) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(TWO_COLOR_MICROARRAY);
                exp.addLabel("Cy3");
                exp.addLabel("Cy5");

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("transcription profiling by array");
                return exp;
            }
        },
        PLANT_TWO_COLOR_EXPERIMENT_BUILDER(PLANT_TWO_COLOR_MICROARRAY) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(PLANT_TWO_COLOR_MICROARRAY);
                exp.addLabel("Cy3");
                exp.addLabel("Cy5");

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("transcription profiling by array");
                return exp;
            }
        },
        HUMAN_TWO_COLOR_EXPERIMENT_BUILDER(HUMAN_TWO_COLOR_MICROARRAY) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(HUMAN_TWO_COLOR_MICROARRAY);
                exp.addLabel("Cy3");
                exp.addLabel("Cy5");

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("transcription profiling by array");
                return exp;
            }
        },
        ANIMAL_TWO_COLOR_EXPERIMENT_BUILDER(ANIMAL_TWO_COLOR_MICROARRAY) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(ANIMAL_TWO_COLOR_MICROARRAY);
                exp.addLabel("Cy3");
                exp.addLabel("Cy5");

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("transcription profiling by array");
                return exp;
            }
        },
        CELL_LINE_TWO_COLOR_EXPERIMENT_BUILDER(CELL_LINE_TWO_COLOR_MICROARRAY) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(CELL_LINE_TWO_COLOR_MICROARRAY);
                exp.addLabel("Cy3");
                exp.addLabel("Cy5");

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("transcription profiling by array");
                return exp;
            }
        },
        SEQUENCING_EXPERIMENT_BUILDER(SEQUENCING) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(SEQUENCING);

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("RNA-seq of coding RNA");
                return exp;
            }
        },
        PLANT_SEQUENCING_EXPERIMENT_BUILDER(PLANT_SEQUENCING) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(PLANT_SEQUENCING);

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("RNA-seq of coding RNA");
                return exp;
            }
        },
        HUMAN_SEQUENCING_EXPERIMENT_BUILDER(HUMAN_SEQUENCING) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(HUMAN_SEQUENCING);

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("RNA-seq of coding RNA");
                return exp;
            }
        },
        ANIMAL_SEQUENCING_EXPERIMENT_BUILDER(ANIMAL_SEQUENCING) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(ANIMAL_SEQUENCING);

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("RNA-seq of coding RNA");
                return exp;
            }
        },
        CELL_LINE_SEQUENCING_EXPERIMENT_BUILDER(CELL_LINE_SEQUENCING) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(CELL_LINE_SEQUENCING);

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("RNA-seq of coding RNA");
                return exp;
            }
        },
        SINGLE_CELL_SEQUENCING_EXPERIMENT_BUILDER(SINGLE_CELL_SEQUENCING) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(SINGLE_CELL_SEQUENCING);

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("RNA-seq of coding RNA from single cells");
                return exp;
            }
        },
        SINGLE_CELL_HUMAN_SEQUENCING_EXPERIMENT_BUILDER(SINGLE_CELL_HUMAN_SEQUENCING) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(SINGLE_CELL_HUMAN_SEQUENCING);

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("RNA-seq of coding RNA from single cells");
                return exp;
            }
        },
        SINGLE_CELL_PLANT_SEQUENCING_EXPERIMENT_BUILDER(SINGLE_CELL_PLANT_SEQUENCING) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(SINGLE_CELL_PLANT_SEQUENCING);

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("RNA-seq of coding RNA from single cells");
                return exp;
            }
        },
        SINGLE_CELL_CELL_LINE_SEQUENCING_EXPERIMENT_BUILDER(SINGLE_CELL_CELL_LINE_SEQUENCING) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(SINGLE_CELL_CELL_LINE_SEQUENCING);

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("RNA-seq of coding RNA from single cells");
                return exp;
            }
        },
        SINGLE_CELL_ANIMAL_SEQUENCING_EXPERIMENT_BUILDER(SINGLE_CELL_ANIMAL_SEQUENCING) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExperimentProfile(SINGLE_CELL_ANIMAL_SEQUENCING);

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("RNA-seq of coding RNA from single cells");
                return exp;
            }
        };

        public static Map<ExperimentProfileType, Builder> map = newHashMap();

        static {
            for (Builder b : Builder.values()) {
                map.put(b.type, b);
            }
        }

        private ExperimentProfileType type;

        Builder(ExperimentProfileType type) {
            this.type = type;
        }

        abstract ExperimentProfile setupExperiment(ExperimentSetupSettings settings);

        private static Builder find(ExperimentSetupSettings settings) {
            return map.get(settings.getExperimentType());
        }

        private static String getFirstName(String fullName) {
            String firstName = "";

            if (!isNullOrEmpty(fullName)) {
                String[] names = fullName.split("\\s+");
                if (!isUpperCase(fullName)) {
                    boolean haveUpperCaseNamesFound = false;
                    for (String name : names) {
                        if (isUpperCase(name)) {
                            haveUpperCaseNamesFound = true;
                            break;
                        }
                    }
                    if (haveUpperCaseNamesFound) {
                        for (String name : names) {
                            if (!isUpperCase(name)) {
                                firstName = firstName + (firstName.isEmpty() ? "" : " ") + name;
                            }
                        }
                        return firstName;
                    }
                }
                if (names.length > 1) {
                    for (int i = 0; i < names.length - 1; i++) {
                        firstName = firstName + (firstName.isEmpty() ? "" : " ") + names[i];
                    }
                } else {
                    firstName = names[0];
                }
            }
            return firstName;
        }

        private static String getLastName(String fullName) {
            String lastName = "";

            if (!isNullOrEmpty(fullName)) {

                String[] names = fullName.split("\\s+");
                if (!isUpperCase(fullName)) {
                    boolean haveUpperCaseNamesFound = false;
                    for (String name : names) {
                        if (isUpperCase(name)) {
                            haveUpperCaseNamesFound = true;
                            break;
                        }
                    }
                    if (haveUpperCaseNamesFound) {
                        for (String name : names) {
                            if (isUpperCase(name)) {
                                lastName = lastName + (lastName.isEmpty() ? "" : " ") + name;
                            }
                        }
                        return lastName;
                    }
                }
                if (names.length > 1) {
                    lastName = names[names.length - 1];
                }
            }
            return lastName;
        }

        private static boolean isUpperCase(String text) {
            return text.equals(text.toUpperCase());
        }

        public static ExperimentProfile build(ExperimentSetupSettings settings, User creator) {
            Builder b = find(settings);
            if (b == null) {
                throw new IllegalStateException("Unable to build an experiment with null type");
            }
            ExperimentProfile experimentProfile = b.setupExperiment(settings);

            if (null != creator) {
                Contact submitter = experimentProfile.createContact();
                submitter.setEmail(creator.getEmail());
                submitter.setLastName(getLastName(creator.getName()));
                submitter.setFirstName(getFirstName(creator.getName()));
                submitter.setRoles(Lists.newArrayList("submitter"));
            }

            return experimentProfile;
        }
    }

    public static ExperimentProfile createExperimentProfile(ExperimentSetupSettings settings, User creator) {
        return Builder.build(settings, creator);
    }
}
