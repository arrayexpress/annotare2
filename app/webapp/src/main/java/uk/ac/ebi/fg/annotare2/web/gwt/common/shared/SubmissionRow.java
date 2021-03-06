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
public class SubmissionRow implements IsSerializable {

    private long id;

    private Accession accession;

    private String title;

    private Date created;

    private SubmissionStatus status;

    private SubmissionType type;

    private String userEmail;

    public SubmissionRow() {
    }

    public SubmissionRow(long id,
                         String accession,
                         String title,
                         Date created,
                         SubmissionStatus status,
                         SubmissionType type,
                         String userEmail) {
        this.id = id;
        this.accession = new Accession(accession);
        this.title = title;
        this.created = created;
        this.status = status;
        this.type = type;
        this.userEmail = userEmail;
    }

    public long getId() {
        return id;
    }

    public String getAccession() {
        return accession.getText();
    }

    public String getTitle() {
        return title;
    }

    public Date getCreated() {
        return created;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public SubmissionType getType() {
        return type;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
