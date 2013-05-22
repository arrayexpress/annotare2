/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.configmodel;

/**
 * @author Olga Melnichuk
 */
public class OntologyTerm {

    private final String accession;

    private final String label;

    public OntologyTerm(String accession, String label) {
        this.accession = accession;
        this.label = label;
    }

    public String getAccession() {
        return accession;
    }

    public String getLabel() {
        return label;
    }
}
