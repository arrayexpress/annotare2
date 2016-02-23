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

package uk.ac.ebi.fg.annotare2.web.server.services.files;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.core.components.FtpManager;
import uk.ac.ebi.fg.annotare2.core.files.SshFileAccess;
import uk.ac.ebi.fg.annotare2.core.properties.AnnotareProperties;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.google.common.base.Strings.nullToEmpty;

public class FtpManagerImpl implements FtpManager {

    private static final Logger log = LoggerFactory.getLogger(FtpManagerImpl.class);

    private final String root;
    private final SshFileAccess access;

    @Inject
    public FtpManagerImpl(AnnotareProperties properties) {
        if (properties.isFtpEnabled()) {
            String root = properties.getFtpPickUpDir();
            if (root.startsWith("/")) {
                root = "file://" + root;
            }
            if (!root.endsWith("/")) {
                root = root + "/";
            }
            this.root = root;
            this.access = new SshFileAccess();
        } else {
            this.root = null;
            this.access = null;
        }
    }

    @Override
    public boolean isEnabled() {
        return null != root;
    }

    @Override
    public String getRoot() {
        return root;
    }

    @Override
    public String getDirectory(String subDirectory) {
        subDirectory = nullToEmpty(subDirectory);
        return root + subDirectory + (subDirectory.isEmpty() ? "" : "/");
    }

    @Override
    public boolean doesExist(String relativePath) {
        try {
            return access.isAccessible(new URI(root + relativePath));
        } catch (URISyntaxException | IOException x) {
            log.error("Exception caught", x);
            return false;
        }
    }

    @Override
    public void createDirectory(String relativePath) {
        try {
            access.createDirectory(new URI(root + relativePath));
        } catch (URISyntaxException | IOException x) {
            log.error("Exception caught", x);
        }
    }
}
