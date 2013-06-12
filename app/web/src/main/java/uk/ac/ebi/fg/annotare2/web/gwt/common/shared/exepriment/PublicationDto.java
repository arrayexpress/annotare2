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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.HasTemporaryIdentity;

/**
 * @author Olga Melnichuk
 */
public class PublicationDto implements IsSerializable, HasTemporaryIdentity {

    private int id;

    private int tmpId;

    private String title;

    private String pubMedId;

    private String authors;

    PublicationDto() {
        /*used by GWT serialization only*/
    }

    public PublicationDto(int id) {
        this.id = id;
        this.tmpId = id;
    }

    public PublicationDto(PublicationDto other) {
        this(other.getId(),
                other.getTmpId(),
                other.getTitle(),
                other.getAuthors(),
                other.getPubMedId());
    }

    public PublicationDto(int id, int tmpId, String title, String authors, String pubMedId) {
        this.id = id;
        this.tmpId = tmpId;
        this.title = title;
        this.authors = authors;
        this.pubMedId = pubMedId;
    }

    public String getAuthors() {
        return authors;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getTmpId() {
        return tmpId;
    }

    public String getPubMedId() {
        return pubMedId;
    }

    public String getTitle() {
        return title;
    }

    public Editor editor() {
        return new Editor(this);
    }

    public boolean isTheSameAs(PublicationDto that) {
        if (authors != null ? !authors.equals(that.authors) : that.authors != null) return false;
        if (pubMedId != null ? !pubMedId.equals(that.pubMedId) : that.pubMedId != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        return true;
    }

    public PublicationDto updatedCopy(PublicationDto updates) {
        return new PublicationDto(
                id,
                updates.getTmpId(),
                updates.getTitle(),
                updates.getAuthors(),
                updates.getPubMedId());
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

        public PublicationDto copy() {
            return new PublicationDto(copy);
        }
    }
}
