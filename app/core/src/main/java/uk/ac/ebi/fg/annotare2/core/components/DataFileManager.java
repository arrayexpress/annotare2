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

package uk.ac.ebi.fg.annotare2.core.components;

import uk.ac.ebi.fg.annotare2.core.files.DataFileHandle;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;

import java.io.IOException;
import java.util.Set;

public interface DataFileManager {

    void addFile(DataFileHandle source, String md5, Submission submission, boolean shouldStore, long fileSize)
            throws DataSerializationException, IOException;

    void addFile(DataFileHandle source, String md5, Submission submission, boolean shouldStore)
            throws DataSerializationException, IOException;

    void storeAssociatedFile(DataFile dataFile);

    Set<DataFile> getAssignedFiles(Submission submission) throws DataSerializationException;

    Set<DataFile> getAssignedFiles(Submission submission, FileType... fileTypes)
            throws DataSerializationException;

    DataFileHandle getFileHandle(DataFile dataFile) throws IOException;

    void renameDataFile(DataFile dataFile, String newFileName);

    void deleteDataFile(DataFile dataFile) throws IOException;

    void deleteDataFileSoftly(DataFile dataFile) throws IOException;

    DataFile get(long fileId) throws RecordNotFoundException;
}
