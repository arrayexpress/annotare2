/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.services.files;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.web.server.properties.DataFileStoreProperties;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class DataFileStore {

    private static final Logger log = LoggerFactory.getLogger(DataFileStore.class);

    private final File root;

    @Inject
    public DataFileStore(DataFileStoreProperties properties) {
        root = properties.getDataStoreDir();
    }

    public String store(DataFileSource source) throws IOException {
        String md5 = source.getDigest();
        File destination = new File(dir(md5, true), md5);

        if (destination.exists()) {
            log.warn("File {} already exists in the repository and will not be overwritten", source.getName());
            return md5;
        }

        source.copyTo(destination);
        return md5;
    }

    public File get(String digest) throws IOException {
        if (isNullOrEmpty(digest)) {
            return null;
        }
        return new File(dir(digest), digest);
    }

    public void delete(String digest) throws IOException {
        File file = get(digest);
        if (null == file) {
            return;
        }

        if (!file.delete()) {
            log.error("Unable to delete file {}", file);
            throw new IOException("Unable to delete file " + file);
        }
        log.debug("File {} removed", file.getName());
    }

    private File dir(String hash) {
        return new File(root, hash.substring(0, 3));
    }

    private File dir(String hash, boolean mkdirs) throws IOException {
        File dir = dir(hash);
        if (mkdirs) {
            if (!dir.exists() && !dir.mkdirs()) {
                log.error("Unable to create directory {}", dir);
                throw new IOException("Unable to create directory " + dir);
            }
        }
        return dir;
    }
}
