/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package uk.ac.ebi.fg.annotare2.web.server.services.validation;

import com.google.common.base.Objects;

import java.nio.file.Path;

public class ValidatedDataFile {

    private final String name;
    private final Path path;

    public ValidatedDataFile(String name, Path path) {
        this.name = name;
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (null == other || !(other instanceof ValidatedDataFile)) {
            return false;
        }
        ValidatedDataFile otherVf = (ValidatedDataFile)other;

        return (Objects.equal(name, otherVf.name) && Objects.equal(path, otherVf.path));
    }

    @Override
    public int hashCode() {
        return (null != name ? name.hashCode() : 0) +
                (31 * (null != path ? path.hashCode() : 0));
    }
}
