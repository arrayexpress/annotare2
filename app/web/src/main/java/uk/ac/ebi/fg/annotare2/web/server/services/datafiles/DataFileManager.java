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

package uk.ac.ebi.fg.annotare2.web.server.services.datafiles;

import uk.ac.ebi.fg.annotare2.om.DataFile;

import java.io.File;

/**
 * @author Olga Melnichuk
 */
public interface DataFileManager {

    /**
     * Creates {@link DataFile} record in the database and schedules a task to copy the file into a file store.
     * @param file file to be copied
     * @return {@Link DataFile] record
     */
    DataFile upload(File file);
}
