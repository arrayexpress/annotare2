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

package uk.ac.ebi.fg.annotare2.autosubs;

import com.google.inject.Inject;
import org.apache.commons.lang.RandomStringUtils;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConnectionProvider;
import uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.records.DataFilesRecord;
import uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.records.ExperimentsRecord;
import uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.records.SpreadsheetsRecord;
import uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.records.UsersRecord;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.ac.ebi.fg.annotare2.autosubs.jooq.Tables.*;

public class SubsTracking {
    private final SubsTrackingProperties properties;

    private final static String STATUS_PENDING = "Waiting";
    private final static String SUBS_TRACKING_DATA_SOURCE = "java:/comp/env/jdbc/subsTrackingDataSource";

    @Inject
    public SubsTracking( SubsTrackingProperties properties ) {
        this.properties = properties;
    }

    public Connection getConnection() throws SubsTrackingException {
        try {
            return ((DataSource)(new InitialContext().lookup(SUBS_TRACKING_DATA_SOURCE))).getConnection();
        } catch (Exception x) {
            throw new SubsTrackingException(SubsTrackingException.CAUGHT_EXCEPTION, x);
        }
    }

    public void releaseConnection(Connection connection) throws SubsTrackingException {
        try {
            if (null != connection && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException x) {
            throw new SubsTrackingException(SubsTrackingException.CAUGHT_EXCEPTION, x);
        }
    }

    public Integer addSubmission(Connection connection, Submission submission) throws SubsTrackingException {

        Integer subsTrackingId = null;
        DSLContext context = getContext(connection);

        if (submission instanceof ExperimentSubmission) {

            try {
                Integer userId = getAnnotareUserId(context);
                ExperimentsRecord r =
                        context.insertInto(EXPERIMENTS)
                                .set(EXPERIMENTS.IS_DELETED, 0)
                                .set(EXPERIMENTS.IN_CURATION, 0)
                                .set(EXPERIMENTS.USER_ID, userId)
                                .set(EXPERIMENTS.DATE_SUBMITTED, new Timestamp(new Date().getTime()))
                                .set(EXPERIMENTS.ACCESSION, submission.getAccession())
                                .set(EXPERIMENTS.NAME, asciiCompliantString(submission.getTitle()))
                                .set(EXPERIMENTS.SUBMITTER_DESCRIPTION, asciiCompliantString(((ExperimentSubmission) submission).getExperimentProfile().getDescription()))
                                .set(EXPERIMENTS.EXPERIMENT_TYPE, properties.getAeSubsTrackingExperimentType())
                                .set(EXPERIMENTS.IS_UHTS, ((ExperimentSubmission) submission).getExperimentProfile().getType().isSequencing() ? 1 : 0)
                                .set(EXPERIMENTS.NUM_SUBMISSIONS, 1)
                                .returning(EXPERIMENTS.ID)
                                .fetchOne();
                if (null != r) {
                    subsTrackingId = r.getId();
                }
            } catch (DataSerializationException x) {
                throw new SubsTrackingException(SubsTrackingException.CAUGHT_EXCEPTION, x);
            }
        } else {
            throw new SubsTrackingException(SubsTrackingException.NOT_IMPLEMENTED_EXCEPTION);
        }
        return subsTrackingId;
    }

    public void updateSubmission(Connection connection, Submission submission) throws SubsTrackingException {
        if (submission instanceof ExperimentSubmission) {
            try {
                Timestamp updateDate = new Timestamp(new Date().getTime());
                ExperimentsRecord r =
                        getContext(connection).selectFrom(EXPERIMENTS)
                                .where(EXPERIMENTS.ID.equal(submission.getSubsTrackingId()))
                                .fetchOne();

                if (null == r || 1 == r.getIsDeleted()) {
                    throw new SubsTrackingException(SubsTrackingException.MISSING_RECORD_EXCEPTION);
                }

                if (1 == r.getInCuration()) {
                    throw new SubsTrackingException(SubsTrackingException.IN_CURATION_ON_RESUBMISSION_EXCEPTION);
                }

                Integer numSubmissions = null == r.getNumSubmissions() ? 1 : r.getNumSubmissions();

                getContext(connection).update(EXPERIMENTS)
                        .set(EXPERIMENTS.DATE_LAST_EDITED, updateDate)
                        .set(EXPERIMENTS.DATE_SUBMITTED, updateDate)
                        .set(EXPERIMENTS.NAME, asciiCompliantString(submission.getTitle()))
                        .set(EXPERIMENTS.SUBMITTER_DESCRIPTION, asciiCompliantString(((ExperimentSubmission) submission).getExperimentProfile().getDescription()))
                        .set(EXPERIMENTS.EXPERIMENT_TYPE, properties.getAeSubsTrackingExperimentType())
                        .set(EXPERIMENTS.NUM_SUBMISSIONS, numSubmissions + 1)
                        .where(EXPERIMENTS.ID.equal(submission.getSubsTrackingId()))
                        .execute();

            } catch (DataSerializationException x) {
                throw new SubsTrackingException(SubsTrackingException.CAUGHT_EXCEPTION, x);
            }
        } else {
            throw new SubsTrackingException(SubsTrackingException.NOT_IMPLEMENTED_EXCEPTION);
        }
    }

    public void sendSubmission(Connection connection, Integer subsTrackingId) throws SubsTrackingException {

        if (null == subsTrackingId) {
            throw new SubsTrackingException(SubsTrackingException.INVALID_ID_EXCEPTION);
        }

        getContext(connection).update(EXPERIMENTS)
                .set(EXPERIMENTS.STATUS, STATUS_PENDING)
                .set(EXPERIMENTS.IN_CURATION, 1)
                .where(EXPERIMENTS.ID.equal(subsTrackingId))
                .execute();
    }

    public void deleteFiles(Connection connection, Integer subsTrackingId) throws SubsTrackingException {

        if (null == subsTrackingId) {
            throw new SubsTrackingException(SubsTrackingException.INVALID_ID_EXCEPTION);
        }

        DSLContext context = getContext(connection);

        context.update(SPREADSHEETS)
                .set(SPREADSHEETS.IS_DELETED, 1)
                .where(SPREADSHEETS.EXPERIMENT_ID.equal(subsTrackingId))
                .execute();

        context.update(DATA_FILES)
                .set(DATA_FILES.IS_DELETED, 1)
                .where(DATA_FILES.EXPERIMENT_ID.equal(subsTrackingId))
                .execute();
    }

    public Integer addMageTabFile(Connection connection, Integer subsTrackingId, String fileName)
            throws SubsTrackingException {

        if (null == subsTrackingId) {
            throw new SubsTrackingException(SubsTrackingException.INVALID_ID_EXCEPTION);
        }

        Integer spreadsheetId = null;

        SpreadsheetsRecord r =
                getContext(connection).insertInto(SPREADSHEETS)
                        .set(SPREADSHEETS.IS_DELETED, 0)
                        .set(SPREADSHEETS.EXPERIMENT_ID, subsTrackingId)
                        .set(SPREADSHEETS.NAME, fileName)
                        .returning(SPREADSHEETS.ID)
                        .fetchOne();
        if (null != r) {
            spreadsheetId = r.getId();
        }
        return spreadsheetId;
    }

    public boolean hasMageTabFileAdded(Connection connection, Integer subsTrackingId, String fileName)
            throws SubsTrackingException {

        if (null == subsTrackingId) {
            throw new SubsTrackingException(SubsTrackingException.INVALID_ID_EXCEPTION);
        }

        Integer count =
                getContext(connection).selectCount()
                        .from(SPREADSHEETS)
                        .where(SPREADSHEETS.EXPERIMENT_ID.equal(subsTrackingId)
                                .and(SPREADSHEETS.NAME.equal(fileName)))
                        .fetchOne(0, Integer.class);

        return (count > 0);
    }

    public Integer addDataFile(Connection connection, Integer subsTrackingId, String fileName)
            throws SubsTrackingException {

        if (null == subsTrackingId) {
            throw new SubsTrackingException(SubsTrackingException.INVALID_ID_EXCEPTION);
        }

        Integer dataFileId = null;

        DataFilesRecord r =
                getContext(connection).insertInto(DATA_FILES)
                        .set(DATA_FILES.IS_DELETED, 0)
                        .set(DATA_FILES.IS_UNPACKED, 1)
                        .set(DATA_FILES.EXPERIMENT_ID, subsTrackingId)
                        .set(DATA_FILES.NAME, fileName)
                        .returning(DATA_FILES.ID)
                        .fetchOne();
        if (null != r) {
            dataFileId = r.getId();
        }

        return dataFileId;
    }

    public boolean isInCuration(Connection connection, Integer subsTrackingId) throws SubsTrackingException {

        if (null == subsTrackingId) {
            throw new SubsTrackingException(SubsTrackingException.INVALID_ID_EXCEPTION);
        }

        ExperimentsRecord r =
                getContext(connection).selectFrom(EXPERIMENTS)
                    .where(EXPERIMENTS.ID.equal(subsTrackingId).and(EXPERIMENTS.IS_DELETED.equal(0)))
                    .fetchOne();

        if (null == r) {
            throw new SubsTrackingException(SubsTrackingException.MISSING_RECORD_EXCEPTION);
        }

        return 1 == r.getInCuration();
    }

    public String getAccession(Connection connection, Integer subsTrackingId) throws SubsTrackingException {

        if (null == subsTrackingId) {
            throw new SubsTrackingException(SubsTrackingException.INVALID_ID_EXCEPTION);
        }

        ExperimentsRecord r =
                getContext(connection).selectFrom(EXPERIMENTS)
                        .where(EXPERIMENTS.ID.equal(subsTrackingId).and(EXPERIMENTS.IS_DELETED.equal(0)))
                        .fetchOne();

        if (null == r) {
            throw new SubsTrackingException(SubsTrackingException.MISSING_RECORD_EXCEPTION);
        }

        return r.getAccession();
    }

    private Integer getAnnotareUserId(DSLContext context) throws SubsTrackingException {

        String subsTrackingUser = properties.getAeSubsTrackingUser();
        if (isNullOrEmpty(subsTrackingUser)) {
            throw new SubsTrackingException(SubsTrackingException.USER_NOT_CONFIGURED_EXCEPTION);
        }

        UsersRecord r =
                context.selectFrom(USERS)
                        .where(USERS.LOGIN.equal(subsTrackingUser))
                        .and(USERS.IS_DELETED.equal(0))
                        .fetchOne();

        if (null == r) {
            // here we create the user
            r = context.insertInto(USERS)
                    .set(USERS.LOGIN, subsTrackingUser)
                    .set(USERS.PASSWORD, RandomStringUtils.randomAlphanumeric(16))
                    .set(USERS.IS_DELETED, 0)
                    .returning(USERS.ID)
                    .fetchOne();
        }
        return ( null != r ) ? r.getId() : null;
    }

    private DSLContext getContext(Connection connection) throws SubsTrackingException {
        if (properties.getAeSubsTrackingEnabled()) {
            try {
                Settings settings = new Settings()
                        .withRenderSchema(false);
                return DSL.using(new DefaultConnectionProvider(connection), SQLDialect.MYSQL, settings);
            } catch (Exception x) {
                throw new SubsTrackingException(SubsTrackingException.CAUGHT_EXCEPTION, x);
            }
        } else {
            return null;
        }
    }

    private String asciiCompliantString(String s) {
        try {
            byte[] b = s.getBytes("US-ASCII");
            return new String(b, "US-ASCII");
        } catch (UnsupportedEncodingException x) {
            //
        }
        return null;
    }
}