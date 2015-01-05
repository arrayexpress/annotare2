/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.server.rpc.updates;

import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.Extract;
import uk.ac.ebi.fg.annotare2.submission.model.Sample;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;

import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.TWO_COLOR_MICROARRAY;

/**
 * @author Olga Melnichuk
 */
public class TwoColorMicroarrayUpdater extends BasicExperimentUpdater {

    public TwoColorMicroarrayUpdater(ExperimentProfile exp) {
        super(exp);
    }

    @Override
    public void createSample() {
        Sample sample = createAndReturnSample();

        Extract extract = exp().createExtract(sample);
        extract.setName(sample.getName());

        for (String label : exp().getLabelNames()) {
            exp().createLabeledExtract(extract, label);
        }
    }

    @Override
    public void updateSettings(ExperimentSettings settings) {
        if (settings.getExperimentType() != TWO_COLOR_MICROARRAY) {
            return;
        }
        exp().setArrayDesign(settings.getArrayDesign());
        super.updateSettings(settings);
    }
}
