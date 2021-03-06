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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.core.components;

import uk.ac.ebi.fg.annotare2.magetabcheck.efo.EfoTerm;

import java.util.Collection;

/**
 * @author Olga Melnichuk
 */
public interface EfoSearch {

    Collection<EfoTerm> searchByPrefix(String prefix, int limit);

    Collection<EfoTerm> searchByPrefix(String prefix, String branchAccession, int limit);

    Collection<EfoTerm> getChildTerms(EfoTerm term, int limit);

    Collection<EfoTerm> getDescendantTerms(EfoTerm term, int limit);

    EfoTerm searchByLabel(String label);

    EfoTerm searchByLabel(String label, String branchAccession);

    EfoTerm searchByAccession(String accession, String branchAccession);

    EfoTerm searchByAccession(String accession);

    //EfoTerm getSystemTerm(SystemEfoTerm term);
}