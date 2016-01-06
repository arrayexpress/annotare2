/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns;

import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;

/**
 * @author Olga Melnichuk
 */
public class OntologyTermValueType implements ColumnValueType {

    private OntologyTerm term;

    OntologyTermValueType() {
        /* used by GWT serialization only */
    }

    public OntologyTermValueType(OntologyTerm term) {
        this.term = term;
    }

    public OntologyTerm getEfoTerm() {
        return term;
    }

    @Override
    public String getColumnName(String name) {
        return name;
    }

    @Override
    public void visit(Visitor visitor) {
        visitor.visitTermValueType(this);
    }
}
