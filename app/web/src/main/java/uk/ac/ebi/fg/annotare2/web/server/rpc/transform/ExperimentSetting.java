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

import uk.ac.ebi.fg.annotare2.configmodel.enums.ExperimentConfigType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public enum ExperimentSetting {
    EXPERIMENT_TYPE {
        @Override
        String getValue(ExperimentSetupSettings settings) {
            ExperimentConfigType type = settings.getExperimentType();
            return (type == null) ? null : type.name();
        }
    },
    NUMBER_OF_HYBRITISATIONS {
        @Override
        String getValue(ExperimentSetupSettings settings) {
            int n = settings.getNumberOfHybs();
            return n > 0 ? Integer.toString(n) : null;
        }
    },
    ARRAY_DESIGN {
        @Override
        String getValue(ExperimentSetupSettings settings) {
            String v = settings.getArrayDesign();
            return isNullOrEmpty(v) ? null : v;
        }
    },
    LABEL {
        @Override
        String getValue(ExperimentSetupSettings settings) {
            String v = settings.getLabel();
            return isNullOrEmpty(v) ? null : v;
        }
    };

    abstract String getValue(ExperimentSetupSettings settings);

    public static Map<String, String> allSettingsAsMap(ExperimentSetupSettings settings) {
        Map<String, String> map = new HashMap<String, String>();
        for (ExperimentSetting setting : values()) {
            String value = setting.getValue(settings);
            if (value != null) {
                map.put(setting.name(), value);
            }
        }
        return map;
    }
}
