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

package uk.ac.ebi.fg.annotare2.magetab.limpopo.idf;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class Term {

    private final String name;
    private final String accession;
    private final TermSource source;

    public Term(@Nullable String name, @Nullable String accession, TermSource source) {
        checkArgument(!isNullOrEmpty(name) || !isNullOrEmpty(accession), "Both Term Name & Term Accession could not be null or empty simultaneously");
        checkArgument(source != null, "Term Source could not be null");
        this.name = name;
        this.accession = accession;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public String getAccession() {
        return accession;
    }

    public TermSource getSource() {
        return source;
    }
}
