/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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
import uk.ac.ebi.fg.annotare2.submission.model.ExtractAttribute;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.HasIdentity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class ExtractAttributesRow implements IsSerializable, HasIdentity {

    private int id;
    private String name;
    private Map<ExtractAttribute, String> values;

    ExtractAttributesRow() {
        /*used by GWT serialization only */
    }

    public ExtractAttributesRow(int id, String name, Map<ExtractAttribute, String> values) {
        this.id = id;
        this.name = name;
        this.values = new HashMap<ExtractAttribute, String>(values);
    }

    @Override
    public Object getIdentity() {
        return id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue(ExtractAttribute attr) {
        return values.get(attr);
    }

    public void setValue(String value, ExtractAttribute attr) {
        values.put(attr, value);
    }

    public Map<ExtractAttribute, String> getValues() {
        return new HashMap<ExtractAttribute, String>(values);
    }

    public ExtractAttributesRow copy() {
        return new ExtractAttributesRow(id, name, values);
    }
}
