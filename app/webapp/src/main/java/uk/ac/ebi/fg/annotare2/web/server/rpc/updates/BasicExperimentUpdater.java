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
    public void updateSample(SampleRow row) {
        super.updateSample(row);
        Sample sample = exp().getSample(row.getId());
        Collection<Extract> extracts = exp().getExtracts(sample);
        // 1 sample -> 1 extract
        Extract extract = extracts.iterator().next();
        extract.setName(sample.getName());
    }
}
