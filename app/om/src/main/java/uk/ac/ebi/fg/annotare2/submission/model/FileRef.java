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

package uk.ac.ebi.fg.annotare2.submission.model;

import java.io.Serializable;

/**
 * @author Olga Melnichuk
 */
public class FileRef implements Serializable {

    private static final long serialVersionUID = -6698786096836245227L;

    private String name;
    private String hash;


    @SuppressWarnings("unused")
    public FileRef() {
    }

    public FileRef(String name, String hash) {
        if (null == name) {
            throw new IllegalArgumentException("Name for FileRef cannot be null");
        }

        this.name = name;
        this.hash = hash;
    }

    public String getName() {
        return name;
    }

    public String getHash() {
        return hash;
    }

    public String asString() {
        return (null != hash ? hash : "null")  + '|' + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileRef fileRef = (FileRef) o;

        return name.equals(fileRef.name) && (null == hash && null == fileRef.hash || hash.equals(fileRef.hash));
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode() + (null != hash ? hash.hashCode() : 0);
    }

    public static FileRef fromString(String fileRefAsString) {
        if (null == fileRefAsString) return null;

        int separator = fileRefAsString.indexOf('|');
        if (-1 == separator || separator >= fileRefAsString.length()) return null;

        return new FileRef(fileRefAsString.substring(separator + 1), fileRefAsString.substring(0, separator));
    }
}
