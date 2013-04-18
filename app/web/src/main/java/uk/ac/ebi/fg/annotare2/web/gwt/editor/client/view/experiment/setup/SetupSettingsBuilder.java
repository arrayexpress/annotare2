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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup;

import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SetupSetting;

import java.util.HashMap;
import java.util.Map;

import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SetupSetting.EXPERIMENT_TYPE;
import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SetupSetting.NUMBER_OF_HYBRITISATIONS;

/**
 * @author Olga Melnichuk
 */
public class SetupSettingsBuilder {

    private final Map<String, String> settings = new HashMap<String, String>();

    public SetupSettingsBuilder setExperimentType(ExperimentType type) {
        put(EXPERIMENT_TYPE, type.name());
        return this;
    }

    public SetupSettingsBuilder setNumberOfHybritisations(int n) {
        if (0 < n && n < 1000) {
            put(NUMBER_OF_HYBRITISATIONS, Integer.toString(n));
        }
        return this;
    }

    public SetupSettingsBuilder setArrayDesign(String value) {
        if (!isEmpty(value)) {
            put(SetupSetting.ARRAY_DESIGN, value);
        }
        return this;
    }

    public SetupSettingsBuilder setLabel(String value) {
        if (!isEmpty(value)) {
            put(SetupSetting.LABEL, value.trim());
        }
        return this;
    }

    private void put(SetupSetting setupSetting, String value) {
        settings.put(setupSetting.name(), value);
    }

    public Map<String, String> build() {
        return settings;
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }
}
