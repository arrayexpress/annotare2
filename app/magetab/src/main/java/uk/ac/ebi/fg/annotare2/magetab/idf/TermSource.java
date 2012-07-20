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

package uk.ac.ebi.fg.annotare2.magetab.idf;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class TermSource {

    public static final TermSource DEFAULT = new TermSource("default", null, "no file");

    private final String name;
    private final String version;
    private final String file;

    public TermSource(String name, @Nullable String version, String file) {
        checkArgument(!isNullOrEmpty(name), "Term Source Name can't be null or empty");
        checkArgument(!isNullOrEmpty(file), "Term Source File can't be null or empty");
        this.name = name;
        this.version = version;
        this.file = file;
    }

    public boolean isDefault() {
        return this == DEFAULT;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getFile() {
        return file;
    }

    @Override
    public String toString() {
        return "TermSource{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", file='" + file + '\'' +
                '}';
    }
}
