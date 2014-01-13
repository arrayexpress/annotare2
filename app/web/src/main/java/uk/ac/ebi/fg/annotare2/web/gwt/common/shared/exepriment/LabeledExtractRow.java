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

/**
 * @author Olga Melnichuk
 */
public class LabeledExtractRow implements HasIdentity, IsSerializable {

    private int id;
    private String name;
    private String label;

    LabeledExtractRow() {
    /* used by GWT serialization */
    }

    public LabeledExtractRow(int extractId, String extractName, String label) {
        this.id = extractId;
        this.name = extractName;
        this.label = label;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LabeledExtractRow copy() {
        return new LabeledExtractRow(id, name, label);
    }
}
