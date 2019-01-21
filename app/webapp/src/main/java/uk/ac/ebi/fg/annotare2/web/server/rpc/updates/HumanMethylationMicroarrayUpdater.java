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

package uk.ac.ebi.fg.annotare2.web.server.rpc.updates;

import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.Extract;
import uk.ac.ebi.fg.annotare2.submission.model.Sample;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;

import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.HUMAN_METHYLATION_MICROARRAY;
/**
 * @author Olga Melnichuk
 */
public class HumanMethylationMicroarrayUpdater extends BasicExperimentUpdater {

    public HumanMethylationMicroarrayUpdater(ExperimentProfile exp) {
        super(exp);
    }

    @Override
    protected Sample createSample(String name) {
        Sample sample = super.createSample(name);

        Extract extract = exp().createExtract(sample);
        extract.setName(sample.getName());

        for (String label : exp().getLabelNames()) {
            exp().createLabeledExtract(extract, label);
        }

        return sample;
    }

    @Override
    public void updateSettings(ExperimentSettings settings) {
        if (settings.getExperimentType() != HUMAN_METHYLATION_MICROARRAY) {
            return;
        }
        exp().setArrayDesign(settings.getArrayDesign());
        super.updateSettings(settings);
    }
}
