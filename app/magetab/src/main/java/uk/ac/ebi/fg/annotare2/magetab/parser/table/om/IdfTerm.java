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
public class IdfTerm {

    private TableCell name;

    private TableCell accession;

    private TableCell ref;

    public TableCell getName() {
        return name;
    }

    public void setName(TableCell name) {
        this.name = name;
    }

    public TableCell getAccession() {
        return accession;
    }

    public void setAccession(TableCell accession) {
        this.accession = accession;
    }

    public TableCell getRef() {
        return ref;
    }

    public void setRef(TableCell ref) {
        this.ref = ref;
    }
}
