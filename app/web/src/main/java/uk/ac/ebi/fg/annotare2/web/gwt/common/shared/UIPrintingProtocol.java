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

/**
 * @author Olga Melnichuk
 */
@Deprecated
public class UIPrintingProtocol {

    private static final String SEPARATOR = ":";

    private String name;

    private String description;

    public UIPrintingProtocol() {
    }

    public UIPrintingProtocol(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String squeeeeze() {
        return name + SEPARATOR + description;
    }

    public static UIPrintingProtocol unsqueeeeze(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        int idx = value.indexOf(SEPARATOR);
        if (idx >= 0) {
            return new UIPrintingProtocol(value.substring(0, idx), value.substring(idx + 1, value.length()));
        }
        return new UIPrintingProtocol(value, "");
    }
}
