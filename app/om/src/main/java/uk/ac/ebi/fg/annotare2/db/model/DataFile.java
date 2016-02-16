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

import uk.ac.ebi.fg.annotare2.db.model.enums.DataFileStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Olga Melnichuk
 */
@Entity
@Table(name = "data_files")
public class DataFile {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "fileName", nullable = false)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false)
    private Date created;

    @Column(name = "digest")
    private String digest;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DataFileStatus status;

    @Column(name = "sourceUri")
    private String sourceUri;

    @Column(name = "sourceDigest")
    private String sourceDigest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownedBy", nullable = false)
    private Submission ownedBy;

    @Column(name = "deleted", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean deleted;

    public DataFile() {
        this(null);
    }

    public DataFile(String name) {
        created = new Date();
        status = DataFileStatus.TO_BE_STORED;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public Date getCreated() {
        return created;
    }

    public DataFileStatus getStatus() {
        return status;
    }

    public void setStatus(DataFileStatus status) {
        this.status = status;
    }

    public String getSourceUri() {
        return sourceUri;
    }

    public void setSourceUri(String sourceUri) {
        this.sourceUri = sourceUri;
    }

    public String getSourceDigest() {
        return sourceDigest;
    }

    public void setSourceDigest(String sourceDigest) {
        this.sourceDigest = sourceDigest;
    }

    public Submission getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(Submission submission) {
        this.ownedBy = submission;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataFile dataFile = (DataFile) o;

        if (id != null ? !id.equals(dataFile.id) : dataFile.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
