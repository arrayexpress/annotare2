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

package uk.ac.ebi.fg.annotare2.om;

import com.google.common.base.Optional;

import java.util.Date;

/**
 * @author Olga Melnichuk
 */
public abstract class Submission implements HasEffectiveAcl {
    public interface Visitor {
        void visit(ExperimentSubmission submission);

        void visit(ArrayDesignSubmission submission);
    }

    private int id;

    private Date created;

    private User createdBy;

    private Acl acl;

    private SubmissionStatus status = SubmissionStatus.IN_PROGRESS;

    private String title;

    private String accession;

    protected Submission(User createdBy, Acl acl) {
        this.created = new Date();
        this.createdBy = createdBy;
        this.acl = acl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public EffectiveAcl getEffectiveAcl() {
        return new EffectiveAcl(acl, Optional.of(createdBy));
    }

    public abstract void accept(Visitor visitor);
}
