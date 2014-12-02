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

package uk.ac.ebi.fg.annotare2.web.server.services.validation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class AnnotareURLStreamHandler extends URLStreamHandler {

    private final ValidatedDataFileConnector fileConnector;

    protected AnnotareURLStreamHandler(ValidatedDataFileConnector fileConnector) {
        this.fileConnector = fileConnector;
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        if (null != url) {
            String location = url.getFile();
            if (null != location) {
                Long submissionId = Long.valueOf(location.replaceFirst("^/(\\d+)/(.+)$", "$1"));
                String name = location.replaceFirst("^/(\\d+)/(.+)$", "$2");

                ValidatedDataFile file = fileConnector.getFile(submissionId, name);
                if (null != file) {
                    return new AnnotareURLConnection(url, file);
                }
            }
        }
        throw new IOException("Unable to open URL " + url);
    }

    private class AnnotareURLConnection extends URLConnection {

        private final ValidatedDataFile file;

        public AnnotareURLConnection(URL url, ValidatedDataFile file) {
            super(url);
            this.file = file;
        }

        public void connect() throws IOException {
        }

        public InputStream getInputStream() throws IOException {
            if (null == file || null == file.getPath()) {
                throw new IOException("Unable to get input stream for this file");
            } else {
                return Files.newInputStream(file.getPath(), StandardOpenOption.READ);
            }
        }
    }
}