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
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.model.*;
import uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater;

import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Maps.newHashMap;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater.experimentUpdater;

/**
 * @author Olga Melnichuk
 */
public class ExperimentBuilderFactory {

    private static ExpProfileType oneColorExperimentType = new OneColorMicroarrayExpProfileType("One-color microarray");
    private static ExpProfileType twoColorExperimentType = new TwoColorMicroarrayExpProfileType("Two-color microarray");
    private static ExpProfileType plantOneColorExperimentType = new PlantOneColorMicroarrayExpProfileType("Plant - One-color microarray");
    private static ExpProfileType plantTwoColorExperimentType = new PlantTwoColorMicroarrayExpProfileType("Plant - Two-color microarray");
    private static ExpProfileType sequencingExperimentType = new SequencingExpProfileType("High-throughput sequencing");
    private static ExpProfileType plantSequencingExperimentType = new PlantSequencingExpProfileType("Plant - High-throughput sequencing");

    private enum Builder {
        ONE_COLOR_EXPERIMENT_BUILDER(oneColorExperimentType) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExpProfile(oneColorExperimentType);
                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("transcription profiling by array");
                return exp;
            }
        },
        PLANT_ONE_COLOR_EXPERIMENT_BUILDER(plantOneColorExperimentType) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExpProfile(plantOneColorExperimentType);
                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("transcription profiling by array");
                return exp;
            }
        },
        TWO_COLOR_EXPERIMENT_BUILDER(twoColorExperimentType) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExpProfile(twoColorExperimentType);
                exp.addLabel("Cy3");
                exp.addLabel("Cy5");

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("transcription profiling by array");
                return exp;
            }
        },
        PLANT_TWO_COLOR_EXPERIMENT_BUILDER(plantTwoColorExperimentType) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExpProfile(plantTwoColorExperimentType);
                exp.addLabel("Cy3");
                exp.addLabel("Cy5");

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                exp.setAeExperimentType("transcription profiling by array");
                return exp;
            }
        },
        SEQUENCING_EXPERIMENT_BUILDER(sequencingExperimentType) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExpProfile(sequencingExperimentType);

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                //updater.updateExtractAttributes(settings.getExtractValues(),settings.getNumberOfHybs());
                exp.setAeExperimentType("RNA-seq of coding RNA");
                return exp;
            }
        },
        PLANT_SEQUENCING_EXPERIMENT_BUILDER(plantSequencingExperimentType) {
            @Override
            ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
                ExperimentProfile exp = new ExpProfile(plantSequencingExperimentType);

                ExperimentUpdater updater = experimentUpdater(exp);
                updater.updateSettings(settings);
                updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
                //updater.updateExtractAttributes(settings.getExtractValues(),settings.getNumberOfHybs());
                exp.setAeExperimentType("RNA-seq of coding RNA");
                return exp;
            }
        };

        public static Map<ExpProfileType, Builder> map = newHashMap();

        static {
            for (Builder b : Builder.values()) {
                map.put(b.type, b);
            }
        }

        private ExpProfileType type;

        Builder(ExpProfileType type) {
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
