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

package uk.ac.ebi.fg.annotare2.ae;

import com.google.inject.Inject;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConnectionProvider;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.ac.ebi.fg.annotare2.ae.jooq.Tables.*;

public class AEConnection {
    //private final static Logger logger = LoggerFactory.getLogger(ArrayExpress.class);

    private final AEConnectionProperties connectionProperties;

    private HikariDataSource dataSource;

    public enum SubmissionState {
        NOT_LOADED,
        PRIVATE,
        PUBLIC
    }

    @Inject
    public AEConnection(AEConnectionProperties properties) throws AEConnectionException {
        this.connectionProperties = properties;
        this.dataSource = null;
    }

    public SubmissionState getSubmissionState(String accession) throws AEConnectionException {

        if (isNullOrEmpty(accession)) {
            throw new AEConnectionException("Accession should not be empty");
        }

        Connection connection = null;
        try {
            connection = getConnection();
            Record2<String, BigInteger> r =
                    getContext(connection).select(STUDY.ACC, SC_OWNER.SC_USER_ID)
                            .from(STUDY)
                                    .leftOuterJoin(SC_LABEL)
                                            .on(STUDY.ACC.equal(SC_LABEL.NAME))
                                    .leftOuterJoin(SC_OWNER)
                                            .on(SC_LABEL.ID.equal(SC_OWNER.SC_LABEL_ID)
                                                    .and(SC_OWNER.SC_USER_ID.equal(BigInteger.ONE)))
                            .where(STUDY.ACC.equal(accession.toUpperCase()))
                            .fetchOne();
            if (null != r) {
                if (accession.equalsIgnoreCase(r.getValue(STUDY.ACC))) {
                    if (null != r.getValue(SC_OWNER.SC_USER_ID)) {
                        return SubmissionState.PUBLIC;
                    } else {
                        return SubmissionState.PRIVATE;
                    }
                }
            }
            return SubmissionState.NOT_LOADED;

        } catch (AEConnectionException e) {
            throw e;
        } catch (Exception e) {
            throw new AEConnectionException(e);
        } finally {
            if (null != connection) {
                releaseConnection(connection);
            }
        }
    }

    private Connection getConnection() throws AEConnectionException {
        if (null == dataSource) {
            throw new AEConnectionException("Unable to obtain a connection; pool has not been initialized");
        }
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new AEConnectionException(e);
        }
    }

    private void releaseConnection(Connection connection) throws AEConnectionException {
        try {
            if (null != connection && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new AEConnectionException(e);
        }
    }

    private DSLContext getContext(Connection connection) throws AEConnectionException {
        try {
            Settings settings = new Settings()
                    .withRenderSchema(false);
            return DSL.using(new DefaultConnectionProvider(connection), SQLDialect.ORACLE, settings);
        } catch (Exception e) {
            throw new AEConnectionException(e);
        }
    }

    public void initialize() throws AEConnectionException {
        if (null != dataSource) {
            throw new AEConnectionException("Illegal repeat initialization of AEConnection");
        }

        if (connectionProperties.isAeConnectionEnabled()) {
            try {
                Class.forName(connectionProperties.getAeConnectionDriverClass());
            } catch (ClassNotFoundException x) {
                String message = "Unable to load driver [" +
                        connectionProperties.getAeConnectionDriverClass() +
                        "] for AEConnection";
                throw new AEConnectionException(message);
            }

            dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(connectionProperties.getAeConnectionURL());
            dataSource.setUsername(connectionProperties.getAeConnectionUser());
            dataSource.setPassword(connectionProperties.getAeConnectionPassword());
            dataSource.setConnectionTestQuery("SELECT 1 FROM STUDY WHERE ROWNUM = 1");
        }
    }

    public void terminate() throws AEConnectionException {
        if (null != dataSource) {
            dataSource.shutdown();
            dataSource = null;
        }
    }
}
