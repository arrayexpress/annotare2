/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.server.services.files;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import java.io.*;

public class LocalFileSource implements DataFileSource, Serializable {

    private static final long serialVersionUID = 7526471155622776156L;

    private final File file;

    public LocalFileSource(File file) throws IllegalArgumentException {
        if (null == file) {
            throw new IllegalArgumentException("File argument cannot be null");
        }
        this.file = file;
    }

    public boolean exists() throws IOException {
        return file.exists();
    }

    public String getName() {
        return file.getName();
    }

    public String getDigest() throws IOException {
        return Files.hash(file, Hashing.md5()).toString();
    }

    public void copyTo(File destination) throws IOException {
        Files.copy(file, destination);
    }

    public void delete() throws IOException {
        if (!file.delete()) {
            throw new IOException("Unable to delete file " + file.getAbsolutePath());
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
