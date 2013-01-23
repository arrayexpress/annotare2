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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf;

import java.io.Serializable;

/**
 * @author Olga Melnichuk
 */
public class UITerm implements Serializable {

    private String category;

    private String name;

    private String accession;

    private UITermSource termSource;

    public UITerm() {
    }

    public UITerm(String name, String accession, UITermSource termSource) {
        this(name, accession, termSource, null);
    }

    public UITerm(String name, String accession, UITermSource termSource, String category) {
        this.name = name;
        this.accession = accession;
        this.termSource = termSource;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getAccession() {
        return accession;
    }

    public UITermSource getTermSource() {
        return termSource;
    }
}
