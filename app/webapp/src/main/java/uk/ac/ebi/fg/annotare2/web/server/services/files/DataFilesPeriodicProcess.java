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

package uk.ac.ebi.fg.annotare2.web.server.services.files;

import com.google.common.base.Objects;
import com.google.inject.Inject;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.core.UnexpectedException;
import uk.ac.ebi.fg.annotare2.core.components.Messenger;
import uk.ac.ebi.fg.annotare2.core.files.DataFileHandle;
import uk.ac.ebi.fg.annotare2.core.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.core.transaction.Transactional;
import uk.ac.ebi.fg.annotare2.db.dao.DataFileDao;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static uk.ac.ebi.fg.annotare2.db.model.enums.DataFileStatus.*;

public class DataFilesPeriodicProcess {

    private static final Logger logger = LoggerFactory.getLogger(DataFilesPeriodicProcess.class);
//    private final ScheduledExecutorService poolScheduler = Executors.newScheduledThreadPool(10);

    private final DataFileStore fileStore;
    private final DataFileDao fileDao;
    private final HibernateSessionFactory sessionFactory;
    private final AnnotareProperties properties;
    private final Messenger messenger;
    private BlockingQueue<Long> fileCopyingSet;
    private final ScheduledExecutorService scheduler;

    @Inject
    public DataFilesPeriodicProcess(DataFileStore fileStore,
                                    DataFileDao fileDao,
                                    HibernateSessionFactory sessionFactory,
                                    AnnotareProperties properties,
                                    Messenger messenger) {
        this.fileStore = fileStore;
        this.fileDao = fileDao;
        this.sessionFactory = sessionFactory;
        this.properties = properties;
        this.messenger = messenger;
        this.scheduler = Executors.newScheduledThreadPool(properties.getWatchdogThreadCount());
        this.fileCopyingSet = new ArrayBlockingQueue<>(properties.getFileCopyingThreadCount());

    }

    @PostConstruct
    protected void startUp() throws Exception {
        final Runnable periodicProcess = new Runnable() {
            @Override
            public void run() {
                Session session = sessionFactory.openSession();
                try {
                    periodicRun();
                } catch (Throwable x) {
                    logger.error(x.getMessage(), x);
                    messenger.send("Error in data file periodic process", x);
                } finally {
                    session.close();
                }
            }
        };

        for (int i = 0; i < properties.getFileCopyingThreadCount(); i++) {
            scheduler.scheduleAtFixedRate(periodicProcess, i * 20, 30, SECONDS);
        }
    }

    @PreDestroy
    protected void shutDown() throws Exception {
        scheduler.shutdown();
//        poolScheduler.shutdown();
        if (scheduler.awaitTermination(1, MINUTES) /*&& poolScheduler.awaitTermination(1, MINUTES)*/) {
            logger.info("Data file periodic process has shut down");
        } else {
            logger.warn("Data file periodic process has failed to shut down properly");
        }
    }

    private void periodicRun() throws Exception {
        logger.info("Thread {} for file copying started", Thread.currentThread().getId());
        final FileAvailabilityChecker availabilityChecker = new FileAvailabilityChecker();
        for (final DataFile file : fileDao.getFilesByStatus(TO_BE_STORED, TO_BE_ASSOCIATED, ASSOCIATED)) {

            if (addFileToFileCopyingSet(file)) {
                try {
                    // FTP files will not be processed if FTP is not enabled
                    if (!file.isDeleted() &&
                            (properties.isFtpEnabled() || !file.getSourceUri().contains(properties.getFtpPickUpDir()))) {
                        switch (file.getStatus()) {
                            case TO_BE_STORED:
                                copyFile(file, availabilityChecker);
                                break;

                            case TO_BE_ASSOCIATED:
                                verifyFile(file, availabilityChecker);
                                break;

                            case ASSOCIATED:
                                maintainAssociation(file, availabilityChecker);
                                break;

                            //case FILE_NOT_FOUND_ERROR:
                            //    attemptToRestoreAssociation(file, availabilityChecker);
                        }
                    }
                } finally {
                    removeDataFileFromDataFileCopyingSet(file);
                    logger.info("Thread {} removed file {}: {} for submission {} to current file copying set.", Thread.currentThread().getId(), file.getName(), file.getStatus(), file.getOwnedBy().getId());
                }
            }
        }
    }

    private synchronized boolean addFileToFileCopyingSet(DataFile file) {
        sessionFactory.getCurrentSession().refresh(file);

        if (file.getStatus() == TO_BE_STORED) {
            if (!fileCopyingSet.contains(file.getId()) && fileCopyingSet.remainingCapacity() > 0) {
                logger.info("Thread {} is adding file {}: {} for submission {} to current file copying set.", Thread.currentThread().getId(), file.getName(), file.getStatus(), file.getOwnedBy().getId());
                fileCopyingSet.add(file.getId());
                logger.info("Thread {} added file {}: {} for submission {} to current file copying set.", Thread.currentThread().getId(), file.getName(), file.getStatus(), file.getOwnedBy().getId());
                return true;
            }
        }
        return false;
    }

    private synchronized void removeDataFileFromDataFileCopyingSet(DataFile file) {
        fileCopyingSet.remove(file.getId());
    }

    @Transactional
    public void copyFile(DataFile file, FileAvailabilityChecker availabilityChecker) throws UnexpectedException {
        try {
            DataFileHandle source = DataFileHandle.createFromUri(new URI(file.getSourceUri()));
            if (availabilityChecker.isAvailable(source)) {
                String digest = source.getDigest();
                if (null != file.getSourceDigest() && !Objects.equal(digest, file.getSourceDigest())) {
                    file.setStatus(MD5_ERROR);
                    logger.error("MD5 mismatch for source file {}", source.getUri());
                } else {
                    file.setFileSize(fileStore.store(source)); // save the file size after copying the file
                    file.setSourceDigest(null);
                    file.setDigest(digest);
                    file.setStatus(STORED);
                    source.delete();
                    file.setSourceUri(null);
                }
            } else {
                file.setStatus(FILE_NOT_FOUND_ERROR);
                logger.error("Unable to find source file {}", source.getUri());
            }
            fileDao.save(file);
        } catch (IOException | URISyntaxException x) {
            throw new UnexpectedException(x.getMessage(), x);
        }
    }

    @Transactional
    public void verifyFile(DataFile file, FileAvailabilityChecker availabilityChecker) throws UnexpectedException {
        try {
            DataFileHandle source = DataFileHandle.createFromUri(new URI(file.getSourceUri()));
            if (availabilityChecker.isAvailable(source)) {
                String digest = source.getDigest();
                if (null != file.getSourceDigest() && !Objects.equal(digest, file.getSourceDigest())) {
                    file.setStatus(MD5_ERROR);
                    logger.error("MD5 mismatch for source file {}", source.getUri());
                } else {
                    file.setSourceDigest(null);
                    file.setDigest(digest);
                    file.setStatus(ASSOCIATED);
                }
            } else {
                file.setStatus(FILE_NOT_FOUND_ERROR);
                logger.error("Unable to find source file {}", source.getUri());
            }
            fileDao.save(file);
        } catch (IOException | URISyntaxException x) {
            throw new UnexpectedException(x.getMessage(), x);
        }
    }

    @Transactional
    public void maintainAssociation(DataFile file, FileAvailabilityChecker availabilityChecker) throws UnexpectedException {
        try {
            DataFileHandle source = DataFileHandle.createFromUri(new URI(file.getSourceUri()));
            if (availabilityChecker.isAvailable(source)) {
                if (!source.getName().equals(file.getName())) {
                    // check md5 to verify the file and rename source file
                    String digest = source.getDigest();
                    if (null != file.getDigest() && !Objects.equal(digest, file.getDigest())) {
                        file.setStatus(MD5_ERROR);
                        logger.error("MD5 mismatch for source file {}", source.getUri());
                    } else {
                        logger.info("Renamed source file {} to {}", source.getUri(), file.getName());
                        file.setSourceUri(source.rename(file.getName()).getUri().toString());
                    }
                    fileDao.save(file);
                }
            } else {
                file.setStatus(FILE_NOT_FOUND_ERROR);
                fileDao.save(file);
            }
        } catch (IOException | URISyntaxException x) {
            throw new UnexpectedException(x.getMessage(), x);
        }
    }

//    @Transactional
//    public void attemptToRestoreAssociation(DataFile file, FileAvailabilityChecker availabilityChecker) throws UnexpectedException {
//        try {
//            DataFileHandle source = DataFileHandle.createFromUri(new URI(file.getSourceUri()));
//            if (availabilityChecker.isAvailable(source)) {
//                // check md5 to verify the file is still the same
//                String digest = source.getDigest();
//                if (null != file.getDigest() && !Objects.equal(digest, file.getDigest())) {
//                    file.setStatus(MD5_ERROR);
//                    logger.error("MD5 mismatch for source file {}", source.getUri());
//                } else {
//                    file.setStatus(ASSOCIATED);
//                }
//                fileDao.save(file);
//
//            }
//        } catch (IOException | URISyntaxException x) {
//            throw new UnexpectedException(x.getMessage(), x);
//        }
//    }
}
