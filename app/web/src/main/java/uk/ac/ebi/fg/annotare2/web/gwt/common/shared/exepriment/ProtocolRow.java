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
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.HasIdentity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ProtocolRow implements IsSerializable, HasIdentity {

    private int id;
    private String name;
    private OntologyTerm protocolType;
    private String description;
    private List<String> parameters = new ArrayList<String>();
    private String hardware;
    private String software;
    private String performer;
    private boolean isAssignable;

    ProtocolRow() {
        /* used by GWT serialization */
    }

    public ProtocolRow(int id, String name, OntologyTerm protocolType, boolean isAssignable) {
        this.id = id;
        this.name = name;
        this.protocolType = protocolType;
        this.isAssignable = isAssignable;
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

    public OntologyTerm getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(OntologyTerm protocolType) {
        this.protocolType = protocolType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = new ArrayList<String>(parameters);
    }

    public String getHardware() {
        return hardware;
    }

    public void setHardware(String hardware) {
        this.hardware = hardware;
    }

    public String getSoftware() {
        return software;
    }

    public void setSoftware(String software) {
        this.software = software;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public boolean isAssignable() {
        return isAssignable;
    }
}
