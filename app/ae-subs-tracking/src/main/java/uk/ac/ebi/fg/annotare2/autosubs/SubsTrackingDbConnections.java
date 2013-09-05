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

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.sql.Connection;
import java.sql.SQLException;

public class SubsTrackingDbConnections
{
    private BoneCP connectionPool;

    public SubsTrackingDbConnections() throws Exception
    {
        Class.forName("com.mysql.jdbc.Driver");
        this.connectionPool = new BoneCP(new BoneCPConfig("ae-subs-tracking-db"));
    }

    public Connection getConnection() throws SQLException
    {
        if (null != this.connectionPool) {
            return this.connectionPool.getConnection();
        } else {
            throw new SQLException("Unable to obtain a connection from DB connection pool");
        }
    }

    public void close()
    {
        this.connectionPool.shutdown();
        this.connectionPool = null;
    }
}