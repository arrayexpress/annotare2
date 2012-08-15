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
public class Term {

    private Row.Cell name;

    private Row.Cell accession;

    private Row.Cell ref;

    public Row.Cell getName() {
        return name;
    }

    public void setName(Row.Cell name) {
        this.name = name;
    }

    public Row.Cell getAccession() {
        return accession;
    }

    public void setAccession(Row.Cell accession) {
        this.accession = accession;
    }

    public Row.Cell getRef() {
        return ref;
    }

    public void setRef(Row.Cell ref) {
        this.ref = ref;
    }
}
