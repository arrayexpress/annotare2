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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.db.model.enums.SubmissionStatus;

import java.util.Date;

/**
 * @author Olga Melnichuk
 */
public class SubmissionDetails implements IsSerializable {

    private long id;

    private Accession accession;

    private String title;

    private Date created;

    private Date updated;

    private SubmissionStatus status;

    private SubmissionType type;

    private String ftpSubDirectory;

    private boolean isEmpty;

    private boolean isOwnedByCreator;

    public SubmissionDetails() {
    }

    public SubmissionDetails(long id,
                             String accession,
                             String title,
                             Date created,
                             Date updated,
                             SubmissionStatus status,
                             SubmissionType type,
                             String ftpSubDirectory,
                             boolean isEmpty,
                             boolean isOwnedByCreator) {
        this.id = id;
        this.accession = new Accession(accession);
        this.title = title;
        this.created = created;
        this.updated = updated;
        this.status = status;
        this.type = type;
        this.ftpSubDirectory = ftpSubDirectory;
        this.isEmpty = isEmpty;
        this.isOwnedByCreator = isOwnedByCreator;
    }

    public long getId() {
        return id;
    }

    public Accession getAccession() {
        return accession;
    }

    public String getTitle() {
        return title;
    }

    public Date getCreated() {
        return created;
    }

    public Date getUpdated() {
        return updated;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public SubmissionType getType() {
        return type;
    }

    public String getFtpSubDirectory() {
        return ftpSubDirectory;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public boolean isOwnedByCreator() {
        return isOwnedByCreator;
    }
}
