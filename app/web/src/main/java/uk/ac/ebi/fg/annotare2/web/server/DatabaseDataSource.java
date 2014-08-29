/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server;

import com.google.inject.Inject;
import com.jolbox.bonecp.BoneCPDataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.CompositeResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;
import uk.ac.ebi.fg.annotare2.web.server.properties.AnnotareProperties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;

public class DatabaseDataSource {

    private BoneCPDataSource ds;

    @Inject
    public DatabaseDataSource(AnnotareProperties properties) throws NamingException {
        Context context = new InitialContext();

        this.ds = new BoneCPDataSource();
        this.ds.setDriverClass(properties.getDbConnectionDriver());
        this.ds.setJdbcUrl(properties.getDbConnectionURL());
        this.ds.setUsername(properties.getDbConnectionUser());
        this.ds.setPassword(properties.getDbConnectionPassword());
        this.ds.setPartitionCount(1);
        this.ds.setIdleMaxAgeInMinutes(10);
        //this.ds.setIdleConnectionTestPeriodInMinutes(1);
        this.ds.setMinConnectionsPerPartition(20);
        this.ds.setMaxConnectionsPerPartition(100);
        //this.ds.setConnectionTestStatement("SELECT 1");

        // register data source in the naming context so hibernate can find it
        context.bind("annotareDb", this.ds);

        //updateDatabase();
    }

    @SuppressWarnings("unused")
    public DataSource getDataSource() {
        return ds;
    }

    public void shutDown() {
        this.ds.getPool().shutdown();
        this.ds = null;
    }

    private void updateDatabase() {
        try {
            Connection connection = null;
            try {
                connection = ds.getConnection();

                Thread currentThread = Thread.currentThread();
                ClassLoader contextClassLoader = currentThread.getContextClassLoader();
                ResourceAccessor threadClFO = new ClassLoaderResourceAccessor(contextClassLoader);

                ResourceAccessor clFO = new ClassLoaderResourceAccessor();
                ResourceAccessor fsFO = new FileSystemResourceAccessor();


                Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
                Liquibase liquibase = new Liquibase("uk/ac/ebi/fg/annotare2/db/changelog/changelog-master.xml", new CompositeResourceAccessor(clFO, fsFO, threadClFO), database);

                // TODO: reinstate parameters if/when we need them
                //Enumeration<String> initParameters = servletContextEvent.getServletContext().getInitParameterNames();
                //while (initParameters.hasMoreElements()) {
                //    String name = initParameters.nextElement().trim();
                //    if (name.startsWith("liquibase.parameter.")) {
                //        liquibase.setChangeLogParameter(name.substring("liquibase.parameter".length()), servletContextEvent.getServletContext().getInitParameter(name));
                //    }
                //}

                liquibase.update("");
            } finally {
                if (null != connection) {
                    connection.close();
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
