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

package uk.ac.ebi.fg.annotare2.magetab.idf;

import uk.ac.ebi.fg.annotare2.magetab.base.Row;

/**
 * @author Olga Melnichuk
 */
public class Info {

    private Row.Cell title;

    private Row.Cell description;

    private Row.Cell dateOfExperiment;

    private Row.Cell dateOfPublicRelease;

    public Row.Cell getTitle() {
        return title;
    }

    public void setTitle(Row.Cell title) {
        this.title = title;
    }

    public Row.Cell getDescription() {
        return description;
    }

    public void setDescription(Row.Cell description) {
        this.description = description;
    }

    public Row.Cell getDateOfExperiment() {
        return dateOfExperiment;
    }

    public void setDateOfExperiment(Row.Cell dateOfExperiment) {
        this.dateOfExperiment = dateOfExperiment;
    }

    public Row.Cell getDateOfPublicRelease() {
        return dateOfPublicRelease;
    }

    public void setDateOfPublicRelease(Row.Cell dateOfPublicRelease) {
        this.dateOfPublicRelease = dateOfPublicRelease;
    }
}
