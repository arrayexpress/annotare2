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

import com.google.common.base.Charsets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @author Olga Melnichuk
 */
public abstract class Submission implements HasEffectiveAcl {

    private int id;

    private SubmissionType type;

    private Date created;

    private User createdBy;
    
    private Acl acl;

    private String investigation;

    private String sampleAndDataRel;

    private SubmissionStatus status = SubmissionStatus.IN_PROGRESS;

    private String title;

    private String accession;

    protected Submission(SubmissionType type,
                         User createdBy,
                         Acl acl) {
        this.type = type;
        this.created = new Date();
        this.createdBy = createdBy;
        this.acl = acl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setInvestigation(String text) {
        this.investigation = text;
    }

    public void setSampleAndDataRelationship(String rel) {
        this.sampleAndDataRel = rel;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public InputStream getInvestigation() throws IOException {
        String str = (investigation == null) ? "" : investigation;
        return new ByteArrayInputStream(str.getBytes(Charsets.UTF_8));
    }

    public InputStream getSampleAndDataRelationship() {
        String str = (sampleAndDataRel == null) ? "" : sampleAndDataRel;
        return new ByteArrayInputStream(str.getBytes(Charsets.UTF_8));
    }

    public Date getCreated() {
        return created;
    }

    public SubmissionType getType() {
        return type;
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
        return new EffectiveAcl(acl, createdBy);
    }
}
