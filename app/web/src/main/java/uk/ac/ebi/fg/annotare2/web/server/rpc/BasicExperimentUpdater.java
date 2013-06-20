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

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.configmodel.Extract;
import uk.ac.ebi.fg.annotare2.configmodel.Sample;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;

import java.util.Collection;

/**
 * @author Olga Melnichuk
 */
public class BasicExperimentUpdater extends ExperimentUpdater {

    BasicExperimentUpdater(ExperimentProfile exp) {
        super(exp);
    }

    @Override
    public Sample createSample(String name) {
        Sample sample  = exp().createSample();
        sample.setName(name);

        Extract extract = exp().createExtract(sample);
        extract.setName(sample.getName());
        for(String label : exp().getLabels()) {
            exp().createLabeledExtract(extract, label);
        }
        return sample;
    }

    @Override
    public void updateSample(SampleRow row) {
        super.updateSample(row);
        Sample sample = exp().getSample(row.getId());
        Collection<Extract> extracts = exp().getExtracts(sample);
        Extract first = extracts.iterator().next();
        first.setName(sample.getName());
    }
}
