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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;

/**
 * @author Olga Melnichuk
 */
public class PublicationDto implements IsSerializable {

    private int id;

    private String title;

    private String authors;

    private String pubMedId;

    private String doi;

    private OntologyTerm status;

    PublicationDto() {
        /*used by GWT serialization only*/
    }

    public PublicationDto(int id) {
        this.id = id;
    }

    public PublicationDto(PublicationDto other) {
        this(other.getId(),
                other.getTitle(),
                other.getAuthors(),
                other.getPubMedId(),
                other.getDoi(),
                other.getStatus());
    }

    public PublicationDto(int id, String title, String authors, String pubMedId, String doi, OntologyTerm status) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.pubMedId = pubMedId;
        this.doi = doi;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getPubMedId() {
        return pubMedId;
    }

    public String getDoi() {
        return doi;
    }

    public OntologyTerm getStatus() {
        return status;
    }

    public Editor editor() {
        return new Editor(this);
    }

    public static class Editor {

        private final PublicationDto copy;

        public Editor(PublicationDto dto) {
            this.copy = new PublicationDto(dto);
        }

        public String getTitle() {
            return copy.title;
        }

        public void setTitle(String title) {
            copy.title = title;
        }

        public void setAuthors(String authors) {
            copy.authors = authors;
        }

        public String getAuthors() {
            return copy.authors;
        }

        public void setPubMedId(String pubMedId) {
            copy.pubMedId = pubMedId;
        }

        public String getPubMedId() {
            return copy.pubMedId;
        }

        public void setDoi(String doi) {
            copy.doi = doi;
        }

        public String getDoi() {
            return copy.doi;
        }

        public void setStatus(OntologyTerm status) {
            copy.status = status;
        }

        public OntologyTerm getStatus() {
            return copy.status;
        }

        public PublicationDto copy() {
            return new PublicationDto(copy);
        }
    }
}
