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
import org.jooq.impl.DSL;
import static uk.ac.ebi.fg.annotare2.autosubs.jooq.Tables.EXPERIMENTS;
import static uk.ac.ebi.fg.annotare2.autosubs.jooq.Tables.USERS;
import uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.records.ExperimentsRecord;
import uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.records.UsersRecord;
import uk.ac.ebi.fg.annotare2.configmodel.DataSerializationException;
import uk.ac.ebi.fg.annotare2.db.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.om.Submission;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Date;

public class SubsTracking {
    private final SubsTrackingProperties properties;
    private final DataSource dbDataSource;

    @Inject
    public SubsTracking( SubsTrackingProperties properties ) {
        this.properties = properties;
        if (properties.getAeSubsTrackingEnabled()) {
            try {
                InitialContext context = new InitialContext();
                this.dbDataSource = (DataSource) context.lookup( "java:/comp/env/jdbc/subsTrackingDataSource" );
            } catch (NamingException x) {
                throw new RuntimeException(x);
            }
        } else {
            this.dbDataSource = null;
        }
    }

    public Integer addSubmission( Submission submission ) {

        Integer subsTrackingId = null;

        if (properties.getAeSubsTrackingEnabled() && submission instanceof ExperimentSubmission) {
            DSLContext context = DSL.using(this.dbDataSource, SQLDialect.MYSQL);

            try {
                Integer userId = getAnnotareUserId(context);
                ExperimentsRecord r =
                        context.insertInto(EXPERIMENTS)
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

            }
        } else {
            throw new RuntimeException("Unable to process array design submission just yet, to be implemented");
        }
        return subsTrackingId;
    }

    private Integer getAnnotareUserId( DSLContext context ) {
        String subsTrackingUser = properties.getAeSubsTrackingUser();
        if (null == subsTrackingUser || "".equals(subsTrackingUser) ) {
            throw new RuntimeException("Submission tracking user name is not defined in the configuration");
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
}