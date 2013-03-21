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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Olga Melnichuk
 */
public class UISubmissionDetails implements Serializable {

    private int id;

    private AccessionValue accession = new AccessionValue();

    private String title;

    private Date created;

    private UISubmissionStatus status;

    private UISubmissionType type;

    public UISubmissionDetails() {
    }

    public UISubmissionDetails(int id,
                               String accession,
                               String title,
                               Date created,
                               UISubmissionStatus status,
                               UISubmissionType type) {
        this.id = id;
        this.accession.set(accession);
        this.title = title;
        this.created = created;
        this.status = status;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public boolean hasAccession() {
        return accession.isUnaccessioned();
    }

    public String getAccession() {
        return accession.get();
    }

    public String getTitle() {
        return title;
    }

    public Date getCreated() {
        return created;
    }

    public UISubmissionStatus getStatus() {
        return status;
    }

    public UISubmissionType getType() {
        return type;
    }
}
