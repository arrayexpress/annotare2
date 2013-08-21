/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.web.server.properties.DataFileStoreProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static com.google.common.io.Closeables.close;
import static java.lang.System.currentTimeMillis;

/**
 * @author Olga Melnichuk
 */
public class DataFileStore {

    private static final Logger log = LoggerFactory.getLogger(DataFileStore.class);

    private static final int BUFFER = 8192;

    private final File root;

    @Inject
    public DataFileStore(DataFileStoreProperties properties) {
        root = properties.getDataStoreDir();
    }

    public String store(File source) throws IOException {
        String md5 = (Files.hash(source, Hashing.md5())).toString();
        File destination = new File(dir(md5, true), md5);

        if (destination.exists()) {
            log.debug("file already in the repository; don't want to copy");
            return md5;
        }

        copy(source, destination);
        return md5;
    }

    private File dir(String hash) {
        return new File(root, hash.substring(0, 3));
    }

    private File dir(String hash, boolean mkdirs) throws IOException {
        File dir = dir(hash);
        if (mkdirs) {
            if (!dir.exists() && !dir.mkdirs()) {
                log.error("can't create subdirectories: {}", dir);
                throw new IOException("Can't create directories for: " + dir);
            }
        }
        return dir;
    }

    private void copy(File source, File dest) throws IOException {
        log.debug("Copying file {} to {} ...", source, dest);
        long start = currentTimeMillis();

        FileChannel in = null;
        FileChannel out = null;

        try {
            in = new FileInputStream(source).getChannel();
            out = new FileOutputStream(dest).getChannel();

            ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER);
            while (in.read(buffer) != -1) {
                buffer.flip();

                while (buffer.hasRemaining()) {
                    out.write(buffer);
                }

                buffer.clear();
            }
        } finally {
            close(in, true);
            close(out, true);
        }
        long duration = currentTimeMillis() - start;
        log.debug("copying {} finished in {} sec", source.getName() + " -> " + dest.getName(), (duration / 1000.0));
    }

    public void delete(String digest) throws IOException {
        File file = new File(dir(digest), digest);
        if (!file.delete()) {
            throw new IOException("Can't remove file: " + file);
        }
        log.debug("File removed: " + file);
    }
}
