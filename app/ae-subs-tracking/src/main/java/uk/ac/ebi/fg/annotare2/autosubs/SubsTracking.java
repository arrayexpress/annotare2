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

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import uk.ac.ebi.fg.annotare2.db.om.Submission;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class SubsTracking
{
    private final DataSource dbDataSource;

    public SubsTracking() {
        try {
            InitialContext context = new InitialContext();
            this.dbDataSource = (DataSource) context.lookup( "java:/comp/env/jdbc/subsTrackingDataSource" );
        } catch (NamingException x) {
            throw new RuntimeException(x);
        }
    }

    public void addSubmission( Submission submission ) {

        DSLContext context = DSL.using(this.dbDataSource, SQLDialect.MYSQL);
    }
}