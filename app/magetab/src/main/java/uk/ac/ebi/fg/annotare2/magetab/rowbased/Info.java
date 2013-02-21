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

package uk.ac.ebi.fg.annotare2.magetab.rowbased;

import com.google.common.annotations.GwtCompatible;
import uk.ac.ebi.fg.annotare2.magetab.table.Cell;

import java.util.Date;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class Info {

    private Cell<String> title;

    private Cell<String> description;

    private Cell<Date> dateOfExperiment;

    private Cell<Date> dateOfPublicRelease;

    private Cell<String> sdrfFile;

    public Cell<String> getTitle() {
        return title;
    }

    public void setTitle(Cell<String> title) {
        this.title = title;
    }

    public Cell<String> getDescription() {
        return description;
    }

    public void setDescription(Cell<String> description) {
        this.description = description;
    }

    public Cell<Date> getDateOfExperiment() {
        return dateOfExperiment;
    }

    public void setDateOfExperiment(Cell<Date> dateOfExperiment) {
        this.dateOfExperiment = dateOfExperiment;
    }

    public Cell<Date> getDateOfPublicRelease() {
        return dateOfPublicRelease;
    }

    public void setDateOfPublicRelease(Cell<Date> dateOfPublicRelease) {
        this.dateOfPublicRelease = dateOfPublicRelease;
    }

    public Cell<String> getSdrfFile() {
        return sdrfFile;
    }

    public void setSdrfFile(Cell<String> sdrfFile) {
        this.sdrfFile = sdrfFile;
    }
}
