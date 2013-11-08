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

package uk.ac.ebi.fg.annotare2.db.om;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import uk.ac.ebi.fg.annotare2.db.om.enums.SubmissionStatus;

import javax.persistence.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static uk.ac.ebi.fg.annotare2.db.om.FilterNames.NONE_DELETED_SUBMISSIONS_FILTER;

/**
 * @author Olga Melnichuk
 */
@Entity
@Table(name = "submissions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@FilterDef(name = NONE_DELETED_SUBMISSIONS_FILTER, defaultCondition = "deleted = 0")
@Filter(name = NONE_DELETED_SUBMISSIONS_FILTER)
public abstract class Submission implements HasEffectiveAcl {

    @Id
    @GeneratedValue
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated", nullable = false)
    private Date updated;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubmissionStatus status;

    @Column(name = "title")
    private String title;

    @Column(name = "accession")
    private String accession;

    @ManyToOne
    @JoinColumn(name = "createdBy", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "ownedBy", nullable = false)
    private User ownedBy;

    @ManyToOne
    @JoinColumn(name = "acl")
    private Acl acl;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "ownedBy")
    @OrderBy("created ASC")
    private Set<DataFile> files;

    @Column(name = "subsTrackingId")
    private Integer subsTrackingId;

    @Column(name = "deleted", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean deleted;

    protected Submission() {
        this(null);
    }

    protected Submission(User createdBy) {
        this.created = new Date();
        this.createdBy = createdBy;
        this.ownedBy = createdBy;
        status = SubmissionStatus.IN_PROGRESS;
        files = newHashSet();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public User getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(User owner) {
        this.ownedBy = owner;
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

    public Acl getAcl() {
        return acl;
    }

    public void setAcl(Acl acl) {
        this.acl = acl;
    }

    public Integer getSubsTrackingId() {
        return this.subsTrackingId;
    }

    public void setSubsTrackingId(Integer subsTrackingId) {
        this.subsTrackingId = subsTrackingId;
    }

    public Set<DataFile> getFiles() {
        return files;
    }

    public EffectiveAcl getEffectiveAcl() {
        return new EffectiveAcl(acl, Optional.of(createdBy), Optional.of(ownedBy));
    }

    protected InputStream asStream(String str) {
        return new ByteArrayInputStream((str == null ? "" : str).getBytes(Charsets.UTF_8));
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public abstract boolean hasNoData();
}
