/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.magetab.limpopo.idf;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class Protocol {

    private String name;
    private Term type;
    private String description;
    private List<String> parameters = new ArrayList<String>();
    private String hardware;
    private String software;
    private String contact;

    public String getName() {
        return name;
    }

    public Term getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public String getHardware() {
        return hardware;
    }

    public String getSoftware() {
        return software;
    }

    public String getContact() {
        return contact;
    }

    public static class Builder {

        private final Protocol prot = new Protocol();

        public Builder setName(String name) {
            prot.name = name;
            return this;
        }

        public Builder setDescription(String text) {
            prot.description = text;
            return this;
        }

        public Builder setHardware(String hardware) {
            prot.hardware = hardware;
            return this;
        }

        public Builder setSoftware(String software) {
            prot.software = software;
            return this;
        }

        public Builder setContact(String contact) {
            prot.contact = contact;
            return this;
        }

        public Builder setParameters(List<String> params) {
            prot.parameters.addAll(params);
            return this;
        }

        public Builder setType(Term type) {
            prot.type = type;
            return this;
        }

        public Protocol build() {
            return prot;
        }
    }
}
