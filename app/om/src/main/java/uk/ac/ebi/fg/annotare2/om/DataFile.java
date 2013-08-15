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

import java.util.Date;

import static uk.ac.ebi.fg.annotare2.om.enums.DataFileStatus.TO_BE_STORED;

/**
 * @author Olga Melnichuk
 */
public class DataFile {

    private int id;

    private String name;

    private Date created;

    private String digest;

    private long size;

    private DataFileStatus status;

    public DataFile(String name, int id) {
        this.name = name;
        this.id = id;
        created = new Date();
        status = TO_BE_STORED;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
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
