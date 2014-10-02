/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.db.dao.DataFileDao;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.web.server.UnexpectedException;
import uk.ac.ebi.fg.annotare2.web.server.services.EmailSender;
import uk.ac.ebi.fg.annotare2.web.server.transaction.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;
import static uk.ac.ebi.fg.annotare2.db.model.enums.DataFileStatus.*;

public class DataFilesPeriodicProcess extends AbstractIdleService {

    private static final Logger log = LoggerFactory.getLogger(DataFilesPeriodicProcess.class);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final DataFileStore fileStore;
    private final DataFileDao fileDao;
    private final HibernateSessionFactory sessionFactory;
    private final EmailSender emailer;

    @Inject
    public DataFilesPeriodicProcess(DataFileStore fileStore,
                                    DataFileDao fileDao,
                                    HibernateSessionFactory sessionFactory,
                                    EmailSender emailer) {
        this.fileStore = fileStore;
        this.fileDao = fileDao;
        this.sessionFactory = sessionFactory;
        this.emailer = emailer;

    }

    @Override
    protected void startUp() throws Exception {
        final Runnable periodicProcess = new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    Session session = sessionFactory.openSession();
                    try {
                        periodicRun();
                    } catch (Throwable x) {
                        log.error(x.getMessage(), x);
                        emailer.sendException("Error in data file periodic process", x);
                    } finally {
                        session.close();
                    }
                }
            }
        };

        scheduler.scheduleAtFixedRate(periodicProcess, 0, 5, SECONDS);
    }

    @Override
    protected void shutDown() throws Exception {
        scheduler.shutdown();
    }

    private void periodicRun() throws Exception {
        for (DataFile file : fileDao.getFilesByStatus(TO_BE_STORED, TO_BE_ASSOCIATED, ASSOCIATED)) {
            switch (file.getStatus()) {
                case TO_BE_STORED:
                    copyFile(file);
                    break;

                case TO_BE_ASSOCIATED:
                    verifyFile(file);
                    break;

                case ASSOCIATED:
                    maintainAssociation(file);
            }
        }
    }

    @Transactional
    public void copyFile(DataFile file) throws UnexpectedException {
        try {
            DataFileSource source = DataFileSource.createFromUri(new URI(file.getSourceUri()));
            if (source.exists()) {
                String digest = source.getDigest();
                if (null != file.getSourceDigest() && !Objects.equal(digest, file.getSourceDigest())) {
                    file.setStatus(MD5_ERROR);
                    log.error("MD5 mismatch for source file {}", source.getUri());
                } else {
                    fileStore.store(source);
                    file.setSourceDigest(null);
                    file.setDigest(digest);
                    file.setStatus(STORED);
                    source.delete();
                    file.setSourceUri(null);
                }
            } else {
                throw new IOException("Unable to find source file " + source.getUri() + "");
            }
            fileDao.save(file);
        } catch (IOException x) {
            throw new UnexpectedException(x.getMessage(), x);
        } catch (URISyntaxException x) {
            throw new UnexpectedException(x.getMessage(), x);
        }
    }

    @Transactional
    public void verifyFile(DataFile file) throws UnexpectedException {
        try {
            DataFileSource source = DataFileSource.createFromUri(new URI(file.getSourceUri()));
            if (source.exists()) {
                String digest = source.getDigest();
                if (null != file.getSourceDigest() && !Objects.equal(digest, file.getSourceDigest())) {
                    file.setStatus(MD5_ERROR);
                    log.error("MD5 mismatch for source file {}", source.getUri());
                } else {
                    file.setSourceDigest(null);
                    file.setDigest(digest);
                    file.setStatus(ASSOCIATED);
                }
            } else {
                file.setStatus(FILE_NOT_FOUND_ERROR);
                log.error("Unable to find source file {}", source.getUri());
            }
            fileDao.save(file);
        } catch (IOException x) {
            throw new UnexpectedException(x.getMessage(), x);
        } catch (URISyntaxException x) {
            throw new UnexpectedException(x.getMessage(), x);
        }
    }

    @Transactional
    public void maintainAssociation(DataFile file) throws UnexpectedException {
        try {
            DataFileSource source = DataFileSource.createFromUri(new URI(file.getSourceUri()));
            if (source.exists()) {
                if (!source.getName().equals(file.getName())) {
                    // check md5 to verify the file and rename source file
                    String digest = source.getDigest();
                    if (null != file.getDigest() && !Objects.equal(digest, file.getDigest())) {
                        file.setStatus(MD5_ERROR);
                        log.error("MD5 mismatch for source file {}", source.getUri());
                    } else {
                        log.info("Renamed source file {} to {}", source.getUri(), file.getName());
                        file.setSourceUri(source.rename(file.getName()).getUri().toString());
                    }
                }

            } else {
                file.setStatus(FILE_NOT_FOUND_ERROR);
            }
            fileDao.save(file);
        } catch (IOException x) {
            throw new UnexpectedException(x.getMessage(), x);
        } catch (URISyntaxException x) {
            throw new UnexpectedException(x.getMessage(), x);
        }
    }
}
