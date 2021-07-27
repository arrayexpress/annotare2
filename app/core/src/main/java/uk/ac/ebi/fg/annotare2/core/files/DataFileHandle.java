/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.core.files;

import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static com.google.common.base.Strings.isNullOrEmpty;

public abstract class DataFileHandle {

    public abstract boolean exists() throws IOException;

    public abstract String getName() throws IOException;

    public abstract URI getUri();

    public abstract String getDigest() throws IOException;

    public abstract DataFileHandle copyTo(URI destination) throws IOException;

    public abstract Pair<DataFileHandle, Boolean> copyIfNotPresent(URI destination) throws IOException;

    public abstract DataFileHandle rename(String newName) throws IOException;

    public abstract void delete() throws IOException;

    public static DataFileHandle createFromUri(URI uri) throws IOException {
        if (null == uri) {
            return null;
        }
        if (isNullOrEmpty(uri.getScheme()) || "file".equals(uri.getScheme())) {
            return new LocalFileHandle(new File(uri.getPath()));
        } else {
            return new RemoteFileHandle(uri);
        }
    }
}
