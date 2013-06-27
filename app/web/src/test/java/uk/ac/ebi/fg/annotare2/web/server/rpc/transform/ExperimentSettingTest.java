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

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;

import java.util.Map;

import static org.junit.Assert.assertTrue;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.ExperimentSetting.allSettingsAsMap;

/**
 * @author Olga Melnichuk
 */
public class ExperimentSettingTest {

    @Test
    public void emptySettingsTest() {
        Map<String, String> map = allSettingsAsMap(new ExperimentSetupSettings());
        assertTrue(map.isEmpty());
    }
}
