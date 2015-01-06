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

package uk.ac.ebi.fg.annotare2.db.model;

import com.google.common.base.Predicate;
import uk.ac.ebi.fg.annotare2.submission.model.ImportedExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Collection;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Collections2.filter;
import static uk.ac.ebi.fg.annotare2.submission.transform.JsonCodec.readImportedExperiment;
import static uk.ac.ebi.fg.annotare2.submission.transform.JsonCodec.writeImportedExperiment;

@Entity
@DiscriminatorValue("IMPORTED_EXPERIMENT")
public class ImportedExperimentSubmission extends Submission {

    @Column(name = "experiment")
    private String experimentString;

    public ImportedExperimentSubmission() {
        this(null);
    }

    public ImportedExperimentSubmission(User user) {
        super(user);
    }

    public ImportedExperimentProfile getExperimentProfile() throws DataSerializationException {
        return readImportedExperiment(experimentString, getVersion());
    }

    public void setExperimentProfile(ImportedExperimentProfile exp) throws DataSerializationException {
        this.experimentString = writeImportedExperiment(exp, getVersion());
    }

    @Override
    public boolean hasNoData() {
        return isNullOrEmpty(experimentString);
    }

    public Collection<DataFile> getIdfFiles() {
        return filterFiles("(?i)^.*idf[.]txt$");
    }

    public Collection<DataFile> getSdrfFiles() {
        return filterFiles("(?i)^.*sdrf[.]txt$");
    }

    private Collection<DataFile> filterFiles(final String nameRegex) {
        return filter(getFiles(), new Predicate<DataFile>() {
            public boolean apply(@Nullable DataFile input) {
                return input != null && input.getName().matches(nameRegex);
            }
        });
    }
}
