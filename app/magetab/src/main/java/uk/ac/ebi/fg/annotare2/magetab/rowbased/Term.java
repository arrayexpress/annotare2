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
import uk.ac.ebi.fg.annotare2.magetab.table.Row;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class Term {

    private Row.Cell<String> name;

    private Row.Cell<String> accession;

    private Row.Cell<String> ref;

    private TermSource termSource;

    public Row.Cell<String> getName() {
        return name;
    }

    public Row.Cell<String> getAccession() {
        return accession;
    }

    public Row.Cell<String> getRef() {
        return ref;
    }

    public TermSource getTermSource() {
        return termSource;
    }

    public void setTermSource(TermSource termSource) {
        this.termSource = termSource;
        this.ref.setValue(termSource == null ? "" : termSource.getName().getValue());
    }

    public static class Builder {

        private final Term term = new Term();

        public void setName(Row.Cell<String> name) {
            term.name = name;
        }

        public void setAccession(Row.Cell<String> accession) {
            term.accession = accession;
        }

        public void setRef(Row.Cell<String> ref) {
            term.ref = ref;
        }

        public Term build() {
            return term;
        }
    }
}
