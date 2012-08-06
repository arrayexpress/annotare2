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

/**
 * @author Olga Melnichuk
 */
public class Publication {

    private String pubMedId;
    private String doi;
    private String authors;
    private String title;
    private Term status;

    public String getPubMedId() {
        return pubMedId;
    }

    public String getDoi() {
        return doi;
    }

    public String getAuthors() {
        return authors;
    }

    public String getTitle() {
        return title;
    }

    public Term getStatus() {
        return status;
    }

    public static class Builder {

        private final Publication pub = new Publication();

        public Builder setTitle(String title) {
            pub.title = title;
            return this;
        }

        public Builder setAuthors(String authors) {
            pub.authors = authors;
            return this;
        }

        public Builder setPubMedId(String pubMedId) {
            pub.pubMedId = pubMedId;
            return this;
        }

        public Builder setDoi(String doi) {
            pub.doi = doi;
            return this;
        }

        public Builder setStatus(Term status) {
            pub.status = status;
            return this;
        }

        public Publication build() {
            return pub;
        }
    }
}
