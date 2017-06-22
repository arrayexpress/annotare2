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
import uk.ac.ebi.fg.annotare2.core.AccessControlException;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.User;
import uk.ac.ebi.fg.annotare2.db.model.enums.Permission;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountManager;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManagerImpl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.nio.file.Path;

public class DataFileConnector {

    private final DataFileStore dataFileStore;
    private final AccountManager accountManager;
    private final SubmissionManagerImpl submissionManager;

    private static final Logger log = LoggerFactory.getLogger(DataFileConnector.class);

    @Inject
    public DataFileConnector(DataFileStore dataFileStore,
                             AccountManager accountManager,
                             SubmissionManagerImpl submissionManager) {
        this.dataFileStore = dataFileStore;
        this.accountManager = accountManager;
        this.submissionManager = submissionManager;

        registerAnnotareURLScheme();
    }

    public URL getFileUrl(Long userId, Long submissionId, String fileName) {
        try {
            return new URL("annotare:/" + userId + "/" + submissionId + "/" + fileName);
        } catch (MalformedURLException x) {
            log.error(null, x);
            return null;
        }
    }

    public boolean containsFile(Long userId, Long submissionId, String name) {
        return null != getFilePath(userId, submissionId, name);
    }

    public Path getFilePath(Long userId, Long submissionId, String name) {
        try {
            User user = accountManager.getById(userId);
            Submission submission = submissionManager.getSubmission(
                    user, submissionId, Submission.class, Permission.VIEW
            );
            for (DataFile file : submission.getFiles()) {
                if (file.getName().equals(name)) {
                    return dataFileStore.get(file.getDigest()).toPath();
                }
            }
        } catch (RecordNotFoundException x) {
            log.error(null, x);
        } catch (AccessControlException x) {
            log.error(null, x);
        } catch (IOException x) {
            log.error(null, x);
        }
        return null;
    }

    private void registerAnnotareURLScheme() {
        try {
            for (final Field field : URL.class.getDeclaredFields()) {
                if ("factory".equalsIgnoreCase(field.getName()) ) {
                    field.setAccessible(true);
                    field.set(
                            null,
                            new AnnotareURLStreamHandlerFactory(
                                    (URLStreamHandlerFactory) field.get(null),
                                    this
                            )
                    );
                }
            }
        } catch (Throwable e) {
            log.error("Cannot register Annotare URL Scheme!");
        }
    }
}
