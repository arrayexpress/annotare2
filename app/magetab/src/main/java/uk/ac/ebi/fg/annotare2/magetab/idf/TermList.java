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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class TermList {

    private final List<String> names = new ArrayList<String>();
    private final List<String> accessions = new ArrayList<String>();
    private final TermSource source;

    public TermList(TermSource source) {
        checkArgument(source != null, "Term Source could not be null");
        this.source = source;
    }

    public List<Term> getTerms() {
        List<Term> terms = new ArrayList<Term>();
        for (int i = 0; i < names.size(); i++) {
            terms.add(new Term(names.get(i), accessions.get(i), source));
        }
        return terms;
    }

    public TermSource getSource() {
        return source;
    }

    void addTerm(@Nullable String name, @Nullable String accession) {
        checkArgument(!isNullOrEmpty(name) || !isNullOrEmpty(accession), "Both Term Name & Term Accession could not be null or empty simultaneously");
        names.add(name);
        accessions.add(emptyToNull(accession));
    }

    public static class Builder {

        private TermList list;

        public Builder(TermSource source) {
            list = new TermList(source);
        }

        public Builder addTerm(String name, @Nullable String accession) {
            list.addTerm(name, accession);
            return this;
        }

        public TermList build() {
            return list;
        }
    }
}
