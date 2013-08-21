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
import uk.ac.ebi.fg.annotare2.dao.DataFileDao;
import uk.ac.ebi.fg.annotare2.om.DataFile;
import uk.ac.ebi.fg.annotare2.om.ExperimentSubmission;

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

    private final CopyFileMessageQueue messageQueue;

    @Inject
    public DataFileManager(DataFileStore fileStore, DataFileDao dataFileDao) {
        this.dataFileDao = dataFileDao;
        this.fileStore = fileStore;
        this.messageQueue = new CopyFileMessageQueue(fileStore, dataFileDao);
    }

    /**
     * Creates {@link DataFile} record in the database and schedules a task to copy the file into a file store.
     *
     * @param file file to be copied
     * @return {@Link DataFile] record
     */
    public DataFile upload(File file) {
        DataFile dataFile = null;
        //todo: do this in transaction{
        try {
            dataFile = dataFileDao.create(file.getName());
            messageQueue.offer(file, dataFile);
        } catch (JMSException e) {
            log.error("JMS error; please see logs for details", e);
            // transaction rollback
        }
        //}
        return dataFile;
    }

    public void removeFile(ExperimentSubmission submission, int fileId) {
        DataFile dataFile = dataFileDao.get(fileId);
        if (dataFile == null || !submission.getFiles().contains(dataFile)) {
            return;
        }
        //todo: do this in transaction{
        try {
            dataFileDao.delete(dataFile);
            submission.getFiles().remove(dataFile);  // todo: while we do not have DB
            List<DataFile> list = dataFileDao.getAllWithDigest(dataFile.getDigest());
            if (list.isEmpty()) {
                fileStore.delete(dataFile.getDigest());
            }
        } catch (IOException e) {
            log.error("File is not deleted", e);
            // transaction rollback
        }
        // }
    }
}
