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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.db.model.enums.DataFileStatus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.HasIdentity;

import java.util.Date;

/**
 * @author Olga Melnichuk
 */
public class DataFileRow implements IsSerializable, HasIdentity {

    private long id;

    private String name;

    private Date created;

    private String md5;

    private DataFileStatus status;

    DataFileRow() {
        /* used by GWT serialization */
    }

    public DataFileRow(long id, String name, String md5, DataFileStatus status, Date created) {
        this.id = id;
        this.name = name;
        this.created = created;
        this.md5 = md5;
        this.status = status;
    }

    @Override
    public Object getIdentity() {
        return id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getCreated() {
        return created;
    }

    public String getMd5() {
        return md5;
    }

    public DataFileStatus getStatus() {
        return status;
    }
}
