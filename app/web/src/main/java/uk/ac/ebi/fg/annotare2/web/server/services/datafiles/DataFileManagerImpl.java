package uk.ac.ebi.fg.annotare2.web.server.services.datafiles;

import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.dao.DataFileDao;
import uk.ac.ebi.fg.annotare2.om.DataFile;

import java.io.File;

/**
 * @author Olga Melnichuk
 */
public class DataFileManagerImpl implements DataFileManager {

    private final DataFileDao dataFileDao;
    private final CopyFileMessageQueue messageQueue;

    @Inject
    public DataFileManagerImpl(DataFileDao dataFileDao, CopyFileMessageQueue messageQueue) {
        this.dataFileDao = dataFileDao;
        this.messageQueue = messageQueue;
    }

    @Override
    public DataFile upload(File file) {
        DataFile dataFile = new DataFile(file.getName());
        dataFileDao.save(dataFile);
        messageQueue.add(dataFile);
        return dataFile;
    }
}
