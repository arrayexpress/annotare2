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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Olga Melnichuk
 */
public class PrintingProtocolDto implements IsSerializable {

    private int id;

    private String name;

    private String description;

    PrintingProtocolDto() {
        /*used by GWT serialization only*/
    }

    public PrintingProtocolDto(String name, String description) {
        this(0, name, description);
    }

    public PrintingProtocolDto(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public boolean hasId() {
        return id > 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
