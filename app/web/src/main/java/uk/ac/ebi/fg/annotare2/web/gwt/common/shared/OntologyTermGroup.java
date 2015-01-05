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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared;

import com.google.common.base.Predicate;
import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class OntologyTermGroup implements IsSerializable {

    private String name;
    private List<OntologyTerm> terms;
    private Map<String, String> definitions;

    OntologyTermGroup() {
        /* used by GWT serialization */
    }

    public OntologyTermGroup(String name) {
        this.name = name;
        this.terms = new ArrayList<OntologyTerm>();
        this.definitions = new HashMap<String, String>();
    }

    public void add(OntologyTerm term, String definition) {
        this.terms.add(term);
        definitions.put(term.getAccession(), definition);
    }

    public String getName() {
        return name;
    }

    public List<OntologyTerm> getTerms() {
        return new ArrayList<OntologyTerm>(terms);
    }

    public String getDefinition(OntologyTerm term) {
        return definitions.get(term.getAccession());
    }

    public boolean isEmpty() {
        return this.terms.isEmpty();
    }

    public OntologyTermGroup filter(Predicate<OntologyTerm> predicate) {
        OntologyTermGroup filtered = new OntologyTermGroup(getName());
        for(OntologyTerm term : getTerms()) {
            if (predicate.apply(term)) {
                filtered.add(term, getDefinition(term));
            }
        }
        return filtered;
    }
}
