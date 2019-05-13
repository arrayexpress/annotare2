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

package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.core.components.DataFileManager;
import uk.ac.ebi.fg.annotare2.core.files.DataFileHandle;
import uk.ac.ebi.fg.annotare2.core.files.LocalFileHandle;
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
import uk.ac.ebi.fg.annotare2.web.server.services.files.DataFileStore;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Olga Melnichuk
 */
public class DataFileManagerImpl implements DataFileManager {

    private final DataFileDao dataFileDao;
    private final DataFileStore fileStore;

    @Inject
    public DataFileManagerImpl(DataFileStore fileStore, DataFileDao dataFileDao) {
        this.dataFileDao = dataFileDao;
        this.fileStore = fileStore;
    }

    @Override
    public void addFile(DataFileHandle source, String md5, Submission submission, boolean shouldStore, long fileSize)
            throws DataSerializationException, IOException {
        DataFile dataFile = dataFileDao.create(source.getName(), submission);

        dataFile.setSourceUri(source.getUri().toString());
        dataFile.setSourceDigest(md5);
        dataFile.setStatus(shouldStore ? DataFileStatus.TO_BE_STORED : DataFileStatus.TO_BE_ASSOCIATED);
        dataFile.setFileSize(fileSize);

        dataFileDao.save(dataFile);
        submission.getFiles().add(dataFile);
    }

    @Override
    public void addFile(DataFileHandle source, String md5, Submission submission, boolean shouldStore)
            throws DataSerializationException, IOException {
        DataFile dataFile = dataFileDao.create(source.getName(), submission);

        dataFile.setSourceUri(source.getUri().toString());
        dataFile.setSourceDigest(md5);
        dataFile.setStatus(shouldStore ? DataFileStatus.TO_BE_STORED : DataFileStatus.TO_BE_ASSOCIATED);

        dataFileDao.save(dataFile);
        submission.getFiles().add(dataFile);
    }

    @Override
    public void storeAssociatedFile(DataFile dataFile) {
        dataFile.setStatus(DataFileStatus.TO_BE_STORED);
        dataFileDao.save(dataFile);
    }

    @Override
    public Set<DataFile> getAssignedFiles(Submission submission) throws DataSerializationException {
        return getAssignedFiles(submission, FileType.values());
    }

    @Override
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
                if (assignedFiles.contains(new FileRef(file.getName(), file.getDigest()))) {
                    result.add(file);
                }
            }
        }
        return result;
    }

    public Collection<FileRef> getColumnFiles(Submission submission, FileType fileType) throws DataSerializationException {
        Collection<FileRef> result = new ArrayList<>();

        if (submission instanceof ExperimentSubmission) {
            ExperimentProfile exp = ((ExperimentSubmission) submission).getExperimentProfile();
            for (FileColumn col : exp.getFileColumns(fileType)) {
                result.addAll(col.getFileRefs());
            }
        }
        return result;
    }

    @Override
    public DataFileHandle getFileHandle(DataFile dataFile) throws IOException {
        if (DataFileStatus.STORED == dataFile.getStatus()) {
            return new LocalFileHandle(fileStore.get(dataFile.getDigest()));
        } else if (DataFileStatus.ASSOCIATED == dataFile.getStatus()) {
            try {
                return DataFileHandle.createFromUri(new URI(dataFile.getSourceUri()));
            } catch (URISyntaxException e) {
                return null;
            }

        } else {
            throw new IOException("Unable to get data data file " + dataFile.getName() + ": invalid status " + dataFile.getStatus().getTitle());
        }

    }

    @Override
    public void renameDataFile(DataFile dataFile, String newFileName) {
        dataFile.setName(newFileName);
        dataFileDao.save(dataFile);
    }

    @Override
    public void deleteDataFile(DataFile dataFile) throws IOException {
        dataFileDao.delete(dataFile);
    }

    @Override
    public void deleteDataFileSoftly(DataFile dataFile) throws IOException {
        dataFileDao.softDelete(dataFile);
    }

    @Override
    public DataFile get(long fileId) throws RecordNotFoundException {
        return dataFileDao.get(fileId);
    }
}
