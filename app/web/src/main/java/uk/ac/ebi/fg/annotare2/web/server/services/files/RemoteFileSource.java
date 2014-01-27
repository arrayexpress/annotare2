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

import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.URI;

public class RemoteFileSource implements DataFileSource, Serializable {

    private static final long serialVersionUID = 7526471155622776167L;

    private final URI uri;
    private final RemoteFileAccess access;

    public RemoteFileSource(URI uri) throws IOException {
        this.uri = uri;
        if (null != uri && "scp".equals(uri.getScheme())) {
            this.access = new ScpFileAccess();
        } else {
            throw new IOException("Remote access is unavailable for " + uri);
        }
    }

    public boolean exists() throws IOException {
        return access.isAccessible(uri);
    }

    public String getName() {
        return FilenameUtils.getName(uri.getPath());
    }

    public String getDigest() throws IOException {
        return access.getDigest(uri);
    }

    public void copyTo(File destination) throws IOException {
        access.copyTo(uri, destination);
    }

    public void delete() throws IOException {
        access.delete(uri);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
