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
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class SampleRow implements IsSerializable, HasIdentity {

    private int id;

    private int tmpId;

    private String name;

    private Map<String, String> values;

    public SampleRow() {
        /* used by GWT serialization only */
    }

    public SampleRow(int id, String name) {
        this(id, name, new HashMap<String, String>());
    }

    public SampleRow(int id, String name, Map<String, String> values) {
        this.id = id;
        this.name = name;
        this.values = new HashMap<String, String>();
        this.values.putAll(values);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue(SampleColumn column) {
        String v = values.get(column.getName());
        return v == null ? "" : v;
    }

    public void setValue(String value, SampleColumn column) {
        values.put(column.getName(), value);
    }

    public int getId() {
        return id;
    }

    public int getTmpId() {
        return tmpId;
    }

    public void setTmpId(int tmpId) {
        this.tmpId = tmpId;
    }

    public Map<String, String> getValues() {
        return new HashMap<String, String>(values);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SampleRow sampleRow = (SampleRow) o;

        if (id != sampleRow.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
