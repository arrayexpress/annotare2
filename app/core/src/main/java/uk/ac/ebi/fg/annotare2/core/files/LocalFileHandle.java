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

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

public class LocalFileHandle extends DataFileHandle implements Serializable {

    private static final long serialVersionUID = 7526471155622776156L;

    private final File file;

    private String digest;

    public LocalFileHandle(File file) throws IllegalArgumentException {
        if (null == file) {
            throw new IllegalArgumentException("File argument cannot be null");
        }
        this.file = file;
        this.digest = null;
    }

    @Override
    public boolean exists() throws IOException {
        return file.exists();
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public URI getUri() {
        try {
            return new URI("file", "", file.getPath(), "");
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Override
    public String getDigest() throws IOException {
        if (null == digest) {
            digest = Files.hash(file, Hashing.md5()).toString();
        }
        return digest;
    }

    @Override
    public DataFileHandle copyTo(URI destination) throws IOException {
        DataFileHandle destFileHandle = DataFileHandle.createFromUri(destination);
        if (destFileHandle instanceof LocalFileHandle) {
            Files.copy(file, ((LocalFileHandle)destFileHandle).getFile());
        } else if (destFileHandle instanceof RemoteFileHandle) {
            ((RemoteFileHandle)destFileHandle).copyFrom(file);
        }
        return destFileHandle;
    }

    @Override
    public DataFileHandle rename(String newName) throws IOException {
        File newFile = new File(file.getParentFile(), newName);
        Files.move(file, newFile);
        return new LocalFileHandle(newFile);
    }

    @Override
    public void delete() throws IOException {
        if (!file.delete()) {
            throw new IOException("Unable to delete file " + file.getAbsolutePath());
        }
    }

    private File getFile() {
        return file;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
