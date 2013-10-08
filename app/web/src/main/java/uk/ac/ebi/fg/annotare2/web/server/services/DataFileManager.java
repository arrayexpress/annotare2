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

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.db.dao.DataFileDao;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.om.DataFile;
import uk.ac.ebi.fg.annotare2.db.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.om.Submission;

import javax.jms.JMSException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class DataFileManager {

    private static final Logger log = LoggerFactory.getLogger(DataFileManager.class);

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
     * @param file       file to be copied
     * @param submission submission to add file to
     */
    public void upload(File file, Submission submission) throws JMSException {
        DataFile dataFile = dataFileDao.create(file.getName(), submission);
        submission.getFiles().add(dataFile);
        messageQueue.offer(file, dataFile);
    }

    public File getFile(DataFile dataFile) throws IOException {
        return fileStore.get(dataFile.getDigest());
    }

    public boolean removeFile(ExperimentSubmission submission, DataFile dataFile) throws IOException {
        if (!submission.getFiles().contains(dataFile)) {
            return false;
        }
        dataFileDao.delete(dataFile);
        submission.getFiles().remove(dataFile);
        List<DataFile> list = dataFileDao.getAllWithDigest(dataFile.getDigest());
        if (list.isEmpty()) {
            fileStore.delete(dataFile.getDigest());
        }
        return true;
    }

    public DataFile get(long fileId) throws RecordNotFoundException {
        return dataFileDao.get(fileId);
    }
}
