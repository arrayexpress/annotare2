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

package uk.ac.ebi.fg.annotare2.db.model;

import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.ac.ebi.fg.annotare2.submission.transform.JsonCodec.readExperiment;
import static uk.ac.ebi.fg.annotare2.submission.transform.JsonCodec.writeExperiment;

/**
 * @author Olga Melnichuk
 */
@Entity
@DiscriminatorValue("EXPERIMENT")
public class ExperimentSubmission extends Submission {

    @Column(name = "experiment")
    private String experimentString;

    public ExperimentSubmission() {
        this(null);
    }

    public ExperimentSubmission(User user) {
        super(user);
    }

    public ExperimentProfile getExperimentProfile() throws DataSerializationException {
        return readExperiment(experimentString, getVersion());
    }

    public void setExperimentProfile(ExperimentProfile exp) throws DataSerializationException {
        this.experimentString = writeExperiment(exp, getVersion());
    }

    public String getExperimentJSON() {
        return experimentString;
    }

    @Override
    public boolean hasNoData() {
        return isNullOrEmpty(experimentString);
    }
}
