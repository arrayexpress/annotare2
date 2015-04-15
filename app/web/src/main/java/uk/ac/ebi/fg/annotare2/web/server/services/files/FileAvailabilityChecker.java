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
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileAvailabilityChecker {

    private final Map<String, List<String>> filesCache;

    private final SshFileAccess access;

    public FileAvailabilityChecker() {
        this.filesCache = new HashMap<>();
        this.access = new SshFileAccess();
    }

    public boolean isAvailable(DataFileSource source) throws IOException {
        if (source instanceof RemoteFileSource) {
            URI uri = source.getUri();
            if (null != uri && access.isSupported(uri)) {
                String path = uri.toString();
                String dir = path.replaceFirst("(.+/)[^/]*$", "$1");
                String name = path.replaceFirst(".+/([^/]*)$", "$1");
                if (filesCache.containsKey(dir)) {
                    return filesCache.get(dir).contains(name);
                } else {
                    List<String> files = access.listFiles(uri);
                    filesCache.put(dir, files);
                    return files.contains(name);
                }
            } else {
                return false;
            }
        } else {
            return source.exists();
        }
    }
}
