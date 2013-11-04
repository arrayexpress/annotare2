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

import org.jooq.exception.DataAccessException;
import org.jooq.impl.DataSourceConnectionProvider;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SubsTrackingConnectionProvider extends DataSourceConnectionProvider {

    private final static String SUBS_TRACKING_DATA_SOURCE = "java:/comp/env/jdbc/subsTrackingDataSource";

    public SubsTrackingConnectionProvider() throws NamingException {
        super((DataSource)(new InitialContext().lookup(SUBS_TRACKING_DATA_SOURCE)));
    }

    @Override
    public Connection acquire() {
        Connection connection = super.acquire();

        if (null != connection) {
            try {
                if (!connection.getAutoCommit())
                    connection.setAutoCommit(true);
            } catch (SQLException x) {
                throw new DataAccessException("Error enabling autoCommit for the connection", x);
            }
        }

        return connection;
    }

    @Override
    public void release(Connection connection) {
        super.release(connection);
    }
}
