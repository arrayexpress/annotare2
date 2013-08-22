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
package uk.ac.ebi.fg.annotare2.om;

import uk.ac.ebi.fg.annotare2.om.enums.DataFileStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import static uk.ac.ebi.fg.annotare2.om.enums.DataFileStatus.TO_BE_STORED;

/**
 * @author Olga Melnichuk
 */
@Entity
@Table(name = "data_files")
public class DataFile implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "fileName")
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    private Date created;

    @Column(name = "digest")
    private String digest;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DataFileStatus status;

    public DataFile() {
        created = new Date();
        status = TO_BE_STORED;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataFile dataFile = (DataFile) o;

        if (id != dataFile.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
