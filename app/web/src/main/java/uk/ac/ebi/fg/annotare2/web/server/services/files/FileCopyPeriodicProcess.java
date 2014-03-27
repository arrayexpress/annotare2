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
import uk.ac.ebi.fg.annotare2.db.model.enums.DataFileStatus;
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
import static uk.ac.ebi.fg.annotare2.db.model.enums.DataFileStatus.STORED;

public class FileCopyPeriodicProcess extends AbstractIdleService {

    private static final Logger log = LoggerFactory.getLogger(FileCopyConsumer.class);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final DataFileStore fileStore;
    private final DataFileDao fileDao;
    private final HibernateSessionFactory sessionFactory;
    private final EmailSender emailer;

    @Inject
    public FileCopyPeriodicProcess(DataFileStore fileStore,
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
                        log.error("File copy error", x);
                        emailer.sendException("File copy error", x);
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
        for (DataFile file : fileDao.getFilesByStatus(DataFileStatus.TO_BE_STORED)) {
            copyFile(file);
        }
    }

    @Transactional
    public void copyFile(DataFile file) throws UnexpectedException {
        try {
            DataFileSource source = DataFileSource.createFromUri(new URI(file.getSourceUri()));
            if (source.exists()) {
                String digest = fileStore.store(source);
                if (!Objects.equal(digest, source.getDigest())) {
                    throw new IOException("MD5 is different between the source and the stored file");
                }
                file.setStatus(STORED);
                source.delete();
                file.setSourceUri(null);
                fileDao.save(file);
            } else {
                log.error("Unable to find source file {}", source.getName());
            }
        } catch (IOException x) {
            throw new UnexpectedException("File copy error", x);
        } catch (URISyntaxException x) {
            throw new UnexpectedException("File copy error", x);
        }
    }
}
