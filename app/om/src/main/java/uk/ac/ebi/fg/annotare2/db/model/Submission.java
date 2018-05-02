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

package uk.ac.ebi.fg.annotare2.db.model;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.db.model.enums.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.submission.transform.ModelVersion;

import javax.persistence.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static uk.ac.ebi.fg.annotare2.db.model.FilterNames.NOT_DELETED_DATA_FILE_FILTER;
import static uk.ac.ebi.fg.annotare2.db.model.FilterNames.NOT_DELETED_SUBMISSION_FILTER;

/**
 * @author Olga Melnichuk
 */
@Entity
@Table(name = "submissions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@FilterDef(name = NOT_DELETED_SUBMISSION_FILTER, defaultCondition = "deleted = 0")
@Filter(name = NOT_DELETED_SUBMISSION_FILTER)
public abstract class Submission implements HasEffectiveAcl {


    private static final Logger logger = LoggerFactory.getLogger(Submission.class);

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "version", nullable = false)
    private ModelVersion version;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "submitted")
    private Date submitted;

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
    @Filter(name = NOT_DELETED_DATA_FILE_FILTER)
    private Set<DataFile> files;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "submission")
    @OrderBy("posted ASC")
    private Set<SubmissionFeedback> feedback;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "submission")
    @OrderBy("created ASC")
    private Set<Message> messages;

    @Column(name = "ftpSubDirectory")
    private String ftpSubDirectory;

    @Column(name = "subsTrackingId")
    private Integer subsTrackingId;

    @Column(name = "otrsTicketNumber")
    private String otrsTicketNumber;

    @Column(name = "rtTicketNumber")
    private String rtTicketNumber;

    @Column(name = "deleted", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean deleted;

    protected Submission() {
        this(null);
    }

    protected Submission(User createdBy) {
        this.version = ModelVersion.CURRENT_VERSION;
        this.created = new Date();
        this.updated = new Date();
        this.createdBy = createdBy;
        this.ownedBy = createdBy;
        this.status = SubmissionStatus.IN_PROGRESS;
        this.files = newHashSet();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setVersion(ModelVersion version) {
        this.version = version;
    }

    public ModelVersion getVersion() {
        return version;
    }

    public Date getCreated() {
        return created;
    }

    public Date getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Date submitted) {
        this.submitted = submitted;
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
        logger.debug("Changing status of submission {} from {} to {}", id, this.status, status);
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

    public String getFtpSubDirectory() {
        return ftpSubDirectory;
    }

    public void setFtpSubDirectory(String ftpSubDirectory) {
        this.ftpSubDirectory = ftpSubDirectory;
    }

    public Integer getSubsTrackingId() {
        return this.subsTrackingId;
    }

    public void setSubsTrackingId(Integer subsTrackingId) {
        this.subsTrackingId = subsTrackingId;
    }

    public String getOtrsTicketNumber() {
        return this.otrsTicketNumber;
    }

    public void setOtrsTicketNumber(String otrsTicketNumber) {
        this.otrsTicketNumber = otrsTicketNumber;
    }

    public String getRtTicketNumber() {
        return this.rtTicketNumber;
    }

    public void setRtTicketNumber(String rtTicketNumber) {
        this.rtTicketNumber = rtTicketNumber;
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
