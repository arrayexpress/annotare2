/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.server.services.validation;

import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.web.server.services.files.DataFileStore;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.util.Set;

public class ValidatedDataFileConnector {

    private final ValidatedDataFileMap fileMap;

    private final DataFileStore dataFileStore;

    @Inject
    public ValidatedDataFileConnector(DataFileStore dataFileStore) {
        this.dataFileStore = dataFileStore;
        fileMap = new ValidatedDataFileMap();
        registerAnnotareURLScheme();
    }

    public void addDataFiles(Long submissionId, Set<DataFile> files) throws IOException {

        ValidatedDataFile vf;
        for (DataFile file : files) {
            if (file.getStatus().isLocal()) {
                vf = new ValidatedDataFile(
                        file.getName(),
                        dataFileStore.get(file.getDigest()).toPath()
                        );
            } else {
                vf = new ValidatedDataFile(file.getName(), null);
            }
            fileMap.put(submissionId, vf);
        }
    }

    public void addFile(Long submissionId, String name) {
        fileMap.put(submissionId, new ValidatedDataFile(name, null));
    }

    public ValidatedDataFile getFile(Long submissionId, String name) {
        return fileMap.get(submissionId, name);
    }

    public void removeFiles(Long submissionId) {
        fileMap.remove(submissionId);
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
            //
        }
    }
}
