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

import com.google.common.annotations.GwtCompatible;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
@JsonIgnoreProperties(ignoreUnknown = true)
public class OntologyTerm implements Serializable {

    @JsonProperty("accession")
    private String accession;

    @JsonProperty("label")
    private String label;

    OntologyTerm() {
    /* used by GWT serialization only */
    }

    @JsonCreator
    public OntologyTerm(@JsonProperty("accession") String accession,
                        @JsonProperty("label") String label) {
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
