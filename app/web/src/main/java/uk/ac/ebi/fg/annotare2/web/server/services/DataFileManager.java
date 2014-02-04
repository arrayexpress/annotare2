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
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;
import uk.ac.ebi.fg.annotare2.web.server.services.files.*;

import javax.jms.JMSException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Olga Melnichuk
 */
public class DataFileManager {

    //private static final Logger log = LoggerFactory.getLogger(DataFileManager.class);

    private final DataFileDao dataFileDao;
    private final DataFileStore fileStore;

    private final FileCopyMessageQueue messageQueue;

    @Inject
    public DataFileManager(DataFileStore fileStore, DataFileDao dataFileDao, FileCopyMessageQueue messageQueue) {
        this.dataFileDao = dataFileDao;
        this.fileStore = fileStore;
        this.messageQueue = messageQueue;
    }

    /**
     * Creates {@link DataFile} record in the database and schedules a task to copy the file into a file store.
     *
     * @param source     file to be copied
     * @param submission submission to add file to
     */
    public void store(DataFileSource source, Submission submission)
            throws JMSException, DataSerializationException, IOException {
        boolean shouldStore = !(source instanceof RemoteFileSource
                && (submission instanceof ExperimentSubmission
                        && ((ExperimentSubmission)submission).getExperimentProfile().getType().isSequencing()));

        DataFile dataFile = dataFileDao.create(source.getName(), shouldStore, submission);
        dataFile.setSourceUri(source.getUri().toString());
        dataFile.setDigest(source.getDigest());
        submission.getFiles().add(dataFile);
        if (shouldStore) {
            messageQueue.offer(source, dataFile);
        }
    }

    public DataFileSource getFile(DataFile dataFile) throws IOException {
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
