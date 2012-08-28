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

import com.google.common.annotations.GwtCompatible;
import uk.ac.ebi.fg.annotare2.magetab.base.Row;

import java.util.Date;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class Info {

    private Row.Cell<String> title;

    private Row.Cell<String> description;

    private Row.Cell<Date> dateOfExperiment;

    private Row.Cell<Date> dateOfPublicRelease;

    public Row.Cell<String> getTitle() {
        return title;
    }

    public void setTitle(Row.Cell<String> title) {
        this.title = title;
    }

    public Row.Cell<String> getDescription() {
        return description;
    }

    public void setDescription(Row.Cell<String> description) {
        this.description = description;
    }

    public Row.Cell<Date> getDateOfExperiment() {
        return dateOfExperiment;
    }

    public void setDateOfExperiment(Row.Cell<Date> dateOfExperiment) {
        this.dateOfExperiment = dateOfExperiment;
    }

    public Row.Cell<Date> getDateOfPublicRelease() {
        return dateOfPublicRelease;
    }

    public void setDateOfPublicRelease(Row.Cell<Date> dateOfPublicRelease) {
        this.dateOfPublicRelease = dateOfPublicRelease;
    }
}
