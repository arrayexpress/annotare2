/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class SystemEfoTermMap implements IsSerializable {

    private Map<SystemEfoTerm, OntologyTerm> map;

    public SystemEfoTermMap() {
        map = new HashMap<SystemEfoTerm, OntologyTerm>();
    }

    public OntologyTerm getEfoTerm(SystemEfoTerm term) {
        return map.get(term);
    }

    public void put(SystemEfoTerm systemTerm, OntologyTerm ontologyTerm) {
        map.put(systemTerm, ontologyTerm);
    }
}
