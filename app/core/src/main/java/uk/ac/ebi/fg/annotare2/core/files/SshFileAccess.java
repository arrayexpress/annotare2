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

import uk.ac.ebi.fg.annotare2.core.utils.LinuxShellCommandExecutor;
import uk.ac.ebi.fg.annotare2.core.utils.URIEncoderDecoder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class SshFileAccess implements RemoteFileAccess, Serializable {

    private static final long serialVersionUID = 752647115562277616L;

    public boolean isSupported(URI file) {
        return null != file && "scp".equals(file.getScheme());
    }

    public boolean isLocal(URI file) {

        return null != file && (isNullOrEmpty(file.getScheme()) || "file".equals(file.getScheme()));
    }

    public boolean isAccessible(URI file) throws IOException {
        return (isSupported(file) && executeSshCommand(
                new LinuxShellCommandExecutor(),
                file.getHost(),
                "test -e " + escapeFilePath(file.getPath()))
        );

    }

    public String getDigest(URI file) throws IOException {
        if (isSupported(file)) {
            LinuxShellCommandExecutor executor = new LinuxShellCommandExecutor();
            if (executeSshCommand(
                    executor,
                    file.getHost(),
                    "md5sum " + escapeFilePath(file.getPath()))) {
                return executor.getOutput().replaceFirst("([^\\s]+)[\\d\\D]*", "$1");
            } else {
                throw new IOException(executor.getErrors());
            }
        }
        return null;
    }

    public void copy(URI source, URI destination) throws IOException {
        if ((isSupported(source) || isLocal(source)) && (isSupported(destination) || isLocal(destination))) {
            LinuxShellCommandExecutor executor = new LinuxShellCommandExecutor();
            if (!executor.execute(
                    "scp " + scpLocationFromURI(source) + " " + scpLocationFromURI(destination)
                    )) {
                throw new IOException(executor.getErrors());
            }
        }
    }

    public URI rename(URI file, String newName) throws IOException {
        if (isSupported(file)) {
            try {
                String newPath = getDirFromPath(file.getPath()) + URIEncoderDecoder.encode(newName);
                URI newFile = new URI(file.getScheme(), file.getHost(), newPath, null);
                if (isAccessible(newFile)) {
                    throw new IOException("Unable to rename file; " + newName + " already exists");
                }
                LinuxShellCommandExecutor executor = new LinuxShellCommandExecutor();
                if (!executeSshCommand(
                        executor,
                        file.getHost(),
                        "mv " + escapeFilePath(file.getPath()) + " " + escapeFilePath(newPath))) {
                    throw new IOException(executor.getErrors());
                }
                return newFile;
            } catch (URISyntaxException x) {
                throw new IOException(x);
            }
        }
        return file;
    }

    public void delete(URI file) throws IOException {
        if (isSupported(file)) {
            LinuxShellCommandExecutor executor = new LinuxShellCommandExecutor();
            if (!executeSshCommand(
                    executor,
                    file.getHost(),
                    "rm " + escapeFilePath(file.getPath()))) {
                throw new IOException(executor.getErrors());
            }
        }
    }

    public void createDirectory(URI directory) throws IOException {
        if (isSupported(directory)) {
            LinuxShellCommandExecutor executor = new LinuxShellCommandExecutor();
            if (!executeSshCommand(
                    executor,
                    directory.getHost(),
                    "mkdir " + escapeFilePath(directory.getPath()))) {
                throw new IOException(executor.getErrors());
            }
        }
    }

    public List<String> listFiles(URI file) throws IOException {
        if (isSupported(file)) {
            LinuxShellCommandExecutor executor = new LinuxShellCommandExecutor();
            if (!executeSshCommand(
                    executor,
                    file.getHost(),
                    "ls -1 " + escapeFilePath(getDirFromPath(file.getPath())))) {
                throw new IOException(executor.getErrors());
            }
            return Arrays.asList(executor.getOutput().split("\\r\\n|[\\r\\n]"));
        } else if (null == file) {
            return null;
        } else {
            throw new IOException("Unsupported scheme " + file.getScheme());
        }
    }

    private boolean executeSshCommand(LinuxShellCommandExecutor executor, String host, String command)
            throws IOException {
        return executor.execute("ssh " + host + " \"" + command + "\"");
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    private String getDirFromPath(String path) {
        return path.replaceFirst("(.+/)[^/]*$", "$1");
    }

    private String escapeFilePath(String path) {
        return null != path ? "'" + path.replaceAll("[']", "\'").replaceAll("[\"]", "\\\\\"") + "'" : null;
    }

    private String scpLocationFromURI(URI uri) throws IOException {
        if (isSupported(uri)) {
            return uri.getHost() + ":\"" + escapeFilePath(uri.getPath()) + "\"";
        } else if (isLocal(uri)) {
            return uri.getPath();
        } else {
            throw new IOException("Unsupported scheme " + uri.getScheme());
        }
    }
}
