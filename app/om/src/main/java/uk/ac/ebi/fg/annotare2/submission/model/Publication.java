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

package uk.ac.ebi.fg.annotare2.submission.model;

import java.io.Serializable;

/**
 * @author Olga Melnichuk
 */
public class Publication implements Serializable {

    private int id;

    private String title;

    private String authors;

    private String pubMedId;

    private String doi;

    private OntologyTerm status;

    Publication() {
    /* used by GWT serialization */
    }

    public Publication(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getPubMedId() {
        return pubMedId;
    }

    public void setPubMedId(String pubMedId) {
        this.pubMedId = pubMedId;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public OntologyTerm getStatus() {
        return status;
    }

    public void setStatus(OntologyTerm status) {
        this.status = status;
    }
}
