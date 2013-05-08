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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.configmodel.enums.ExperimentConfigType;

/**
 * @author Olga Melnichuk
 */
public class ExperimentSetupSettings implements IsSerializable {

    private ExperimentConfigType experimentType;
    private int numberOfHybs;
    private String arrayDesign;
    private String label;

    public ExperimentConfigType getExperimentType() {
        return experimentType;
    }

    public String getArrayDesign() {
        return arrayDesign;
    }

    public String getLabel() {
        return label;
    }

    public int getNumberOfHybs() {
        return numberOfHybs;
    }

    public static class Builder {

        private ExperimentSetupSettings settings = new ExperimentSetupSettings();

        public Builder setExperimentType(ExperimentConfigType type) {
            settings.experimentType = type;
            return this;
        }

        public Builder setNumberOfHybritisations(int n) {
            settings.numberOfHybs = n;
            return this;
        }

        public Builder setArrayDesign(String value) {
            settings.arrayDesign = value;
            return this;
        }

        public Builder setLabel(String value) {
            settings.label = value;
            return this;
        }

        public ExperimentSetupSettings build() {
            return settings;
        }
    }
}
