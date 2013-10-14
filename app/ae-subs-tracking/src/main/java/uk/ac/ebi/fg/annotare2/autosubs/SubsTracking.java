package uk.ac.ebi.fg.annotare2.autosubs;

/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

import com.google.inject.Inject;
import org.apache.commons.lang.RandomStringUtils;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.records.DataFilesRecord;
import uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.records.ExperimentsRecord;
import uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.records.SpreadsheetsRecord;
import uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.records.UsersRecord;
import uk.ac.ebi.fg.annotare2.configmodel.DataSerializationException;
import uk.ac.ebi.fg.annotare2.db.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.om.Submission;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Date;

import static uk.ac.ebi.fg.annotare2.autosubs.jooq.Tables.*;

public class SubsTracking {
    private final SubsTrackingProperties properties;
    private final DSLContext jooqDslContext;
    private static final String STATUS_PENDING = "Waiting";

    @Inject
    public SubsTracking( SubsTrackingProperties properties ) {
        this.properties = properties;
        if (properties.getAeSubsTrackingEnabled()) {
            try {
                InitialContext context = new InitialContext();
                DataSource dataSource = (DataSource) context.lookup( "java:/comp/env/jdbc/subsTrackingDataSource" );
                Settings settings = new Settings()
                        .withRenderSchema(false);
                this.jooqDslContext = DSL.using(dataSource, SQLDialect.MYSQL, settings);
            } catch (NamingException x) {
                throw new RuntimeException(x);
            }
        } else {
            this.jooqDslContext = null;
        }
    }

    public Integer addSubmission( Submission submission ) {

        Integer subsTrackingId = null;

        if (submission instanceof ExperimentSubmission) {

            try {
                Integer userId = getAnnotareUserId();
                ExperimentsRecord r =
                        getContext().insertInto(EXPERIMENTS)
                                .set(EXPERIMENTS.IS_DELETED, 0)
                                .set(EXPERIMENTS.IN_CURATION, 0)
                                .set(EXPERIMENTS.USER_ID, userId)
                                .set(EXPERIMENTS.DATE_SUBMITTED, new Timestamp(new Date().getTime()))
                                .set(EXPERIMENTS.ACCESSION, submission.getAccession())
                                .set(EXPERIMENTS.NAME, submission.getTitle())
                                .set(EXPERIMENTS.SUBMITTER_DESCRIPTION, ((ExperimentSubmission) submission).getExperimentProfile().getDescription())
                                .set(EXPERIMENTS.EXPERIMENT_TYPE, properties.getAeSubsTrackingExperimentType())
                                .returning(EXPERIMENTS.ID)
                                .fetchOne();
                if (null != r) {
                    subsTrackingId = r.getId();
                }
            } catch (DataSerializationException x) {
                throw new RuntimeException(x);
            }
        } else {
            throw new RuntimeException("Unable to process array design submission just yet, to be implemented");
        }
        return subsTrackingId;
    }

    public void updateSubmission( Submission submission ) {
        if (submission instanceof ExperimentSubmission) {
            try {
                Timestamp updateDate = new Timestamp(new Date().getTime());
                getContext().update(EXPERIMENTS)
                        .set(EXPERIMENTS.IS_DELETED, 0)
                        .set(EXPERIMENTS.IN_CURATION, 0)
                        .set(EXPERIMENTS.DATE_LAST_EDITED, updateDate)
                        .set(EXPERIMENTS.DATE_SUBMITTED, updateDate)
                        .set(EXPERIMENTS.ACCESSION, submission.getAccession())
                        .set(EXPERIMENTS.NAME, submission.getTitle())
                        .set(EXPERIMENTS.SUBMITTER_DESCRIPTION, ((ExperimentSubmission) submission).getExperimentProfile().getDescription())
                        .set(EXPERIMENTS.EXPERIMENT_TYPE, properties.getAeSubsTrackingExperimentType())
                        .execute();

            } catch (DataSerializationException x) {
                throw new RuntimeException(x);
            }
        } else {
            throw new RuntimeException("Unable to process array design submission just yet, to be implemented");
        }
    }

    public void sendSubmission( Integer subsTrackingId ) {
        if (null != subsTrackingId) {
            getContext().update(EXPERIMENTS)
                    .set(EXPERIMENTS.STATUS, STATUS_PENDING)
                    .set(EXPERIMENTS.IN_CURATION, 1)
                    .where(EXPERIMENTS.ID.equal(subsTrackingId))
                    .execute();
        }
    }

    public void deleteFiles( Integer subsTrackingId ) {
        if (null != subsTrackingId) {
            getContext().update(SPREADSHEETS)
                    .set(SPREADSHEETS.IS_DELETED, 1)
                    .where(SPREADSHEETS.EXPERIMENT_ID.equal(subsTrackingId))
                    .execute();

            getContext().update(DATA_FILES)
                    .set(DATA_FILES.IS_DELETED, 1)
                    .where(DATA_FILES.EXPERIMENT_ID.equal(subsTrackingId))
                    .execute();
        }
    }

    public Integer addMageTabFile( Integer subsTrackingId, String fileName ) {
        Integer spreadsheetId = null;

        if (null != subsTrackingId) {
            SpreadsheetsRecord r =
                    getContext().insertInto(SPREADSHEETS)
                            .set(SPREADSHEETS.IS_DELETED, 0)
                            .set(SPREADSHEETS.EXPERIMENT_ID, subsTrackingId)
                            .set(SPREADSHEETS.NAME, fileName)
                            .returning(SPREADSHEETS.ID)
                            .fetchOne();
            if (null != r) {
                spreadsheetId = r.getId();
            }
        }
        return spreadsheetId;
    }

    public boolean hasMageTabFileAdded( Integer subsTrackingId, String fileName ) {
        if (null != subsTrackingId) {
            Select<?> count =
                    getContext().selectCount()
                            .from(SPREADSHEETS)
                            .where(SPREADSHEETS.EXPERIMENT_ID.equal(subsTrackingId)
                                    .and(SPREADSHEETS.NAME.equal(fileName)));

            return (count.fetchCount() > 0);
        } else {
            return false;
        }
    }

    public Integer addDataFile( Integer subsTrackingId, String fileName ) {
        Integer dataFileId = null;

        if (null != subsTrackingId) {
            //try {
            DataFilesRecord r =
                    getContext().insertInto(DATA_FILES)
                            .set(DATA_FILES.IS_DELETED, 0)
                            .set(DATA_FILES.IS_UNPACKED, 1)
                            .set(DATA_FILES.EXPERIMENT_ID, subsTrackingId)
                            .set(DATA_FILES.NAME, fileName)
                            .returning(DATA_FILES.ID)
                            .fetchOne();
            if (null != r) {
                dataFileId = r.getId();
            }
            //}
        }
        return dataFileId;
    }

    private Integer getAnnotareUserId() {
        String subsTrackingUser = properties.getAeSubsTrackingUser();
        if (null == subsTrackingUser || "".equals(subsTrackingUser) ) {
            throw new RuntimeException("Submission tracking user name is not defined in the configuration");
        }

        UsersRecord r =
                getContext().selectFrom(USERS)
                        .where(USERS.LOGIN.equal(subsTrackingUser))
                        .and(USERS.IS_DELETED.equal(0))
                        .fetchOne();

        if (null == r) {
            // here we create the user
            r = getContext().insertInto(USERS)
                    .set(USERS.LOGIN, subsTrackingUser)
                    .set(USERS.PASSWORD, RandomStringUtils.randomAlphanumeric(16))
                    .set(USERS.IS_DELETED, 0)
                    .returning(USERS.ID)
                    .fetchOne();
        }
        return ( null != r ) ? r.getId() : null;
    }

    private DSLContext getContext() {
        return this.jooqDslContext;
    }
}