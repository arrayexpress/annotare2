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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.net.URI;

public class RemoteFileHandle extends DataFileHandle implements Serializable {

    private static final long serialVersionUID = 7526471155622776167L;

    private final URI uri;
    private final RemoteFileAccess access;
    private String digest;

    public RemoteFileHandle(URI uri) throws IOException {
        this.digest = null;
        this.uri = uri;
        if (null != uri && "scp".equals(uri.getScheme())) {
            this.access = new SshFileAccess();
        } else {
            throw new IOException("Remote access is unavailable for " + uri);
        }
    }

    @Override
    public boolean exists() throws IOException {
        return access.isAccessible(uri);
    }

    @Override
    public String getName() throws IOException {
        return FilenameUtils.getName(uri.getPath());
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public String getDigest() throws IOException {
        if (null == digest) {
            digest = access.getDigest(uri);
        }
        return digest;
    }

    @Override
    public DataFileHandle copyTo(URI destination) throws IOException {
        access.copy(uri, destination);
        return DataFileHandle.createFromUri(destination);
    }

    @Override
    public Pair<DataFileHandle, Boolean> copyIfNotPresent(URI destination) throws IOException {
       return MutablePair.of(copyTo(destination), true);
    }

    @Override
    public DataFileHandle rename(String newName) throws IOException {
        return new RemoteFileHandle(access.rename(uri, newName));
    }

    @Override
    public void delete() throws IOException {
        access.delete(uri);
    }

    public String getDirectory() throws IOException {
        return FilenameUtils.getPath(uri.getPath());
    }

    public void copyFrom(File source) throws IOException {
        access.copy(source.toURI(), uri);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
