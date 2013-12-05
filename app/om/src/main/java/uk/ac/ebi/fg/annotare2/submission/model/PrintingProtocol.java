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

package uk.ac.ebi.fg.annotare2.submission.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.io.Serializable;

/**
 * @author Olga Melnichuk
 */
public class PrintingProtocol implements Serializable {

    private static final String SEPARATOR = ":";

    private int id;

    private String name;

    private String description;

    PrintingProtocol() {
    /* used by GWT serialization only */
    }

    @JsonCreator
    public PrintingProtocol(int id,
                            String name,
                            String description) {
        this.id = id;
        this.description = description;
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String format() {
        return name + SEPARATOR + description;
    }

    public static PrintingProtocol parse(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        int idx = value.indexOf(SEPARATOR);
        if (idx >= 0) {
            return new PrintingProtocol(0, value.substring(0, idx), value.substring(idx + 1, value.length()));
        }
        return new PrintingProtocol(0, value, "");
    }
}
