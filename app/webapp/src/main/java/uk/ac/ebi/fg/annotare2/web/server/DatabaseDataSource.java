/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server;

import com.google.inject.Inject;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.CompositeResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.core.properties.AnnotareProperties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class DatabaseDataSource {

    private HikariDataSource ds;

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Inject
    public DatabaseDataSource(AnnotareProperties properties) throws NamingException {
        Context context = new InitialContext();

        ds = new HikariDataSource();
        ds.setPoolName("AnnotareDB-Pool");
        ds.setDriverClassName(properties.getDbConnectionDriver());
        ds.setJdbcUrl(properties.getDbConnectionURL());
        ds.setUsername(properties.getDbConnectionUser());
        ds.setPassword(properties.getDbConnectionPassword());
        ds.setConnectionTestQuery("SELECT 1");
        ds.addDataSourceProperty("dataSource.cachePrepStmts", "true");
        ds.addDataSourceProperty("dataSource.prepStmtCacheSize", "250");
        ds.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", "2048");
        ds.addDataSourceProperty("dataSource.useServerPrepStmts", "true");
        ds.addDataSourceProperty("autoReconnect", "true");
        ds.addDataSourceProperty("useUnicode", "true");
        ds.addDataSourceProperty("characterEncoding", "utf8");
        ds.addDataSourceProperty("connectionCollation", "utf8_unicode_ci");

        // register data source in the naming context so hibernate can find it
        context.bind("annotareDb", this.ds);
    }

    @SuppressWarnings("unused")
    public DataSource getDataSource() {
        return ds;
    }

    @PostConstruct
    public void startUp() {
        updateDatabase();
    }

    @PreDestroy
    public void shutDown() {
        ds.close();
        ds = null;
        unregisterJdbc();
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

    private void unregisterJdbc() {
        Enumeration<Driver> en = DriverManager.getDrivers();
        while (en.hasMoreElements()) {
            try {
                DriverManager.deregisterDriver(en.nextElement());
            } catch (SQLException e) {
                //
            }
        }
    }

}
