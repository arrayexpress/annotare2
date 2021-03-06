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

package uk.ac.ebi.fg.annotare2.db.dao;

import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.enums.DataFileStatus;

import java.util.Collection;

/**
 * @author Olga Melnichuk
 */
public interface DataFileDao {

    DataFile get(long id) throws RecordNotFoundException;

    DataFile create(String fileName, Submission submission);

    void delete(DataFile dataFile);

    void softDelete(DataFile dataFile);

    void save(DataFile dataFile);

    Collection<DataFile> getFilesByDigest(String digest);

    Collection<DataFile> getFilesByStatus(DataFileStatus... statuses);
}
