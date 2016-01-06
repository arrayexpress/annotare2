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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Olga Melnichuk
 */
public class UserDto implements IsSerializable {

    private String name;
    private boolean isCurator;

    UserDto() {
        /* used by GWT Serialization */
    }

    public UserDto(String name, boolean isCurator) {
        this.name = name;
        this.isCurator = isCurator;
    }

    public String getName() {
        return name;
    }

    public boolean isCurator() {
        return isCurator;
    }
}
