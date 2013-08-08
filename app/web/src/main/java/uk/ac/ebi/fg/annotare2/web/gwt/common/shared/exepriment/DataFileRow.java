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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.HasIdentity;

import java.util.Date;

/**
 * @author Olga Melnichuk
 */
public class DataFileRow implements IsSerializable, HasIdentity {

    private int id;

    private String name;

    private String size;

    private Date created;

    private String md5;

    DataFileRow() {
        /* used by GWT serialization */
    }

    public DataFileRow(int id, String name, String md5, String size, Date created) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.created = created;
        this.md5 = md5;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public Date getCreated() {
        return created;
    }

    public String getMd5() {
        return md5;
    }
}
