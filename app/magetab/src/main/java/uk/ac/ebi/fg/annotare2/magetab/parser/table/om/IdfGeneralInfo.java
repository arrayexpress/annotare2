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

package uk.ac.ebi.fg.annotare2.magetab.parser.table.om;

import uk.ac.ebi.fg.annotare2.magetab.parser.table.TableCell;

/**
 * @author Olga Melnichuk
 */
public class IdfGeneralInfo {

    private TableCell title;

    private TableCell description;

    public TableCell getTitle() {
        return title;
    }

    public void setTitle(TableCell title) {
        this.title = title;
    }

    public TableCell getDescription() {
        return description;
    }

    public void setDescription(TableCell description) {
        this.description = description;
    }
}
