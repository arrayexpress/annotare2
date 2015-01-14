/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package uk.ac.ebi.fg.annotare2.submission.model;

import java.io.Serializable;

import static com.google.common.base.Strings.isNullOrEmpty;

public class ImportedExperimentProfile implements Serializable {

    private static final long serialVersionUID = -6077425344898508542L;

    private String title;

    private String description;

    private String aeExperimentType;

    public ImportedExperimentProfile() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAeExperimentType() {
        return aeExperimentType;
    }

    public void setAeExperimentType(String aeExperimentType) {
        this.aeExperimentType = aeExperimentType;
    }

    public void populate(ImportedExperimentProfile profile) {
        if (!isNullOrEmpty(profile.title)) {
            title = profile.title;
        }
        if (!isNullOrEmpty(profile.description)) {
            description = profile.description;
        }
        if (!isNullOrEmpty(profile.aeExperimentType)) {
            aeExperimentType = profile.aeExperimentType;
        }
    }
}
