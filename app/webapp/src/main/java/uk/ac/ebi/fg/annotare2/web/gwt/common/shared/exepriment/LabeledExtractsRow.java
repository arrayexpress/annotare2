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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.HasIdentity;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Olga Melnichuk
 */
public class LabeledExtractsRow implements HasIdentity, IsSerializable {

    private int id;
    private String name;
    private Set<String> labels;

    LabeledExtractsRow() {
    /* used by GWT serialization */
    }

    public LabeledExtractsRow(int extractId, String extractName) {
        this.id = extractId;
        this.name = extractName;
        this.labels = new LinkedHashSet<String>();
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

    public Set<String> getLabels() {
        return labels;
    }

    public void setLabels(Collection<String> labels) {
        this.labels = new LinkedHashSet<String>(labels);
    }

    public boolean addLabel(String label) {
        return labels.add(label);
    }

    public LabeledExtractsRow copy() {
        LabeledExtractsRow copy = new LabeledExtractsRow(id, name);
        copy.setLabels(labels);
        return copy;
    }
}