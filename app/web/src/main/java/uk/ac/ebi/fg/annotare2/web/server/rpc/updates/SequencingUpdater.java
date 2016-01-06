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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.rpc.updates;

import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.Extract;
import uk.ac.ebi.fg.annotare2.submission.model.Sample;

/**
 * @author Olga Melnichuk
 */
public class SequencingUpdater extends BasicExperimentUpdater {

    public SequencingUpdater(ExperimentProfile exp) {
         super(exp);
    }

    @Override
    protected Sample createSample(String name) {
        Sample sample  = super.createSample(name);

        Extract extract = exp().createExtract(sample);
        extract.setName(sample.getName());

        exp().createLabeledExtract(extract, null);
        return sample;
    }
}
