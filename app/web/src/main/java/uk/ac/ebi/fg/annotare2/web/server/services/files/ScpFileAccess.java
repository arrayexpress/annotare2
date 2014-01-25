package uk.ac.ebi.fg.annotare2.web.server.services.files;

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

import uk.ac.ebi.fg.annotare2.web.server.services.utils.LinuxShellCommandExecutor;

import java.io.*;
import java.net.URI;

public class ScpFileAccess implements RemoteFileAccess, Serializable {

    private static final long serialVersionUID = 752647115562277616L;

    public boolean isAccessible(URI file) throws IOException {
        if (null != file && "scp".equals(file.getScheme())) {
            return new LinuxShellCommandExecutor().execute("ssh " + file.getHost() + " test -f " + file.getPath());
        }
        return false;
    }

    public String getDigest(URI file) throws IOException {
        if (null != file && "scp".equals(file.getScheme())) {
            LinuxShellCommandExecutor executor = new LinuxShellCommandExecutor();
            if (executor.execute("ssh " + file.getHost() + " md5sum " + file.getPath())) {
                return executor.getOutput().replaceFirst("([^\\s]+)[\\d\\D]*", "$1");
            } else {
                throw new IOException(executor.getErrors());
            }
        }
        return null;
    }

    public void copyTo(URI file, File destination) throws IOException {
        if (null != file && "scp".equals(file.getScheme())) {
            LinuxShellCommandExecutor executor = new LinuxShellCommandExecutor();
            if (!(executor.execute("scp " + file.getHost() + ":" + file.getPath() + " " + destination.getPath()))) {
                throw new IOException(executor.getErrors());
            }
        }
    }

    public void delete(URI file) throws IOException {
        if (null != file && "scp".equals(file.getScheme())) {
            LinuxShellCommandExecutor executor = new LinuxShellCommandExecutor();
            if (!(executor.execute("ssh " + file.getHost() + " rm " + file.getPath()))) {
                throw new IOException(executor.getErrors());
            }
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
