/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.om;

import uk.ac.ebi.fg.annotare2.configmodel.DataSerializationException;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfile;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static uk.ac.ebi.fg.annotare2.configmodel.JsonCodec.fromJson2Experiment;
import static uk.ac.ebi.fg.annotare2.configmodel.JsonCodec.toJsonString;

/**
 * @author Olga Melnichuk
 */
public class ExperimentSubmission extends Submission {

    private String experimentString;

    private Set<DataFile> files;

    public ExperimentSubmission(User user, Acl acl) {
        super(user, acl);
        this.files = newHashSet();
    }

    public ExperimentProfile getExperimentProfile() throws DataSerializationException {
        return fromJson2Experiment(experimentString);
    }

    public void setExperimentProfile(ExperimentProfile exp) throws DataSerializationException {
        this.experimentString = toJsonString(exp);
    }

    @Override
    public boolean hasNoData() {
        return isNullOrEmpty(experimentString);
    }

    @Override
    public void discardAll() {
        experimentString = null;
    }

    public Set<DataFile> getFiles() {
        return files;
    }
}
