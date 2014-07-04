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

package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.db.dao.DataFileDao;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.enums.DataFileStatus;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.FileColumn;
import uk.ac.ebi.fg.annotare2.submission.model.FileRef;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;
import uk.ac.ebi.fg.annotare2.web.server.services.files.DataFileSource;
import uk.ac.ebi.fg.annotare2.web.server.services.files.DataFileStore;
import uk.ac.ebi.fg.annotare2.web.server.services.files.LocalFileSource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Olga Melnichuk
 */
public class DataFileManager {

    private final DataFileDao dataFileDao;
    private final DataFileStore fileStore;

    @Inject
    public DataFileManager(DataFileStore fileStore, DataFileDao dataFileDao/*, FileCopyMessageQueue messageQueue*/) {
        this.dataFileDao = dataFileDao;
        this.fileStore = fileStore;
    }

    /**
     * Creates {@link DataFile} record in the database and schedules a task to copy the file into a file store.
     *
     * @param source     file to be copied
     * @param md5        verification md5 if not null
     * @param submission submission to add file to
     */
    public void addFile(DataFileSource source, String md5, Submission submission, boolean shouldStore)
            throws DataSerializationException, IOException {
        DataFile dataFile = dataFileDao.create(source.getName(), submission);

        dataFile.setSourceUri(source.getUri().toString());
        dataFile.setSourceDigest(md5);
        dataFile.setStatus(shouldStore ? DataFileStatus.TO_BE_STORED : DataFileStatus.TO_BE_ASSOCIATED);
        submission.getFiles().add(dataFile);
    }

    public void storeAssociatedFile(DataFile dataFile) {
        dataFile.setStatus(DataFileStatus.TO_BE_STORED);
    }

    public Set<DataFile> getAssignedFiles(Submission submission) throws DataSerializationException {
        return getAssignedFiles(submission, FileType.values());
    }

    public Set<DataFile> getAssignedFiles(Submission submission, FileType... fileTypes)
            throws DataSerializationException {
        Set<DataFile> result = new HashSet<DataFile>();

        if (submission instanceof ExperimentSubmission) {
            ExperimentProfile exp = ((ExperimentSubmission)submission).getExperimentProfile();
            Set<FileRef> assignedFiles = new HashSet<FileRef>();
            for (FileColumn col : exp.getFileColumns(fileTypes)) {
                assignedFiles.addAll(col.getFileRefs());
            }
            for (DataFile file : submission.getFiles()) {
                if (file.getStatus().isOk() && assignedFiles.contains(new FileRef(file.getName(), file.getDigest()))) {
                    result.add(file);
                }
            }
        }
        return result;
    }
    
    public DataFileSource getFileSource(DataFile dataFile) throws IOException {
        if (DataFileStatus.STORED == dataFile.getStatus()) {
            return new LocalFileSource(fileStore.get(dataFile.getDigest()));
        } else if (DataFileStatus.ASSOCIATED == dataFile.getStatus()) {
            try {
                return DataFileSource.createFromUri(new URI(dataFile.getSourceUri()));
            } catch (URISyntaxException e) {
                return null;
            }

        } else {
            throw new IOException("Unable to get data data file " + dataFile.getName() + ": invalid status " + dataFile.getStatus().getTitle());
        }

    }

    public void renameDataFile(DataFile dataFile, String newFileName) {
        dataFile.setName(newFileName);
        dataFileDao.save(dataFile);
    }

    public void deleteDataFile(DataFile dataFile) throws IOException {
        dataFileDao.delete(dataFile);
    }

    public void deleteDataFileSoftly(DataFile dataFile) throws IOException {
        dataFileDao.softDelete(dataFile);
    }

    public DataFile get(long fileId) throws RecordNotFoundException {
        return dataFileDao.get(fileId);
    }
}
