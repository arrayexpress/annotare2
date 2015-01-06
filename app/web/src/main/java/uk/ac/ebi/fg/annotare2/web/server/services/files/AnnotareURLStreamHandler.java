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

package uk.ac.ebi.fg.annotare2.web.server.services.files;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class AnnotareURLStreamHandler extends URLStreamHandler {

    private final DataFileConnector fileConnector;

    protected AnnotareURLStreamHandler(DataFileConnector fileConnector) {
        this.fileConnector = fileConnector;
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        if (null != url) {
            String location = url.getFile();
            if (null != location) {
                Long userId = Long.valueOf(location.replaceFirst("^/(\\d+)/(\\d+)/(.+)$", "$1"));
                Long submissionId = Long.valueOf(location.replaceFirst("^/(\\d+)/(\\d+)/(.+)$", "$2"));
                String name = location.replaceFirst("^/(\\d+)/(\\d+)/(.+)$", "$3");

                Path file = fileConnector.getFilePath(userId, submissionId, name);
                if (null != file) {
                    return new AnnotareURLConnection(url, file);
                } else if ("idf.txt".equals(name) || "sdrf.txt".equals(name)) {
                    return new AnnotareURLConnection(url, null);
                }
            }
        }
        throw new IOException("Unable to open URL " + url);
    }

    private class AnnotareURLConnection extends URLConnection {

        private final Path file;

        public AnnotareURLConnection(URL url, Path file) {
            super(url);
            this.file = file;
        }

        public void connect() throws IOException {
        }

        public InputStream getInputStream() throws IOException {
            if (null == file) {
                throw new IOException("Unable to get input stream for this file");
            } else {
                return Files.newInputStream(file, StandardOpenOption.READ);
            }
        }
    }
}