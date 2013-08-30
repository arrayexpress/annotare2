/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

import com.google.common.util.concurrent.Service;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceServletContextListener;
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
import uk.ac.ebi.fg.annotare2.magetab.init.Magetab;
import uk.ac.ebi.fg.annotare2.magetabcheck.CheckerModule;
import uk.ac.ebi.fg.annotare2.web.server.services.CopyFileMessageQueue;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.inject.internal.util.$Lists.newArrayList;
import static com.google.inject.util.Modules.override;
import static org.reflections.util.ClasspathHelper.*;

/**
 * @author Olga Melnichuk
 */
public class AppServletContextListener extends GuiceServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(AppServletContextListener.class);

    private Set<URL> libPaths = newHashSet();
    private List<Service> servicesToStop = newArrayList();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        updateDb(servletContextEvent);

        findMageTabCheckAnnotationPackages();

        lookupPropertiesInContext();

        //todo: not used any more, need to be moved out
        Magetab.init();
        super.contextInitialized(servletContextEvent);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        stopServices();
        super.contextDestroyed(servletContextEvent);
    }

    @Override
    protected Injector getInjector() {
        Injector injector = Guice.createInjector(Stage.DEVELOPMENT,
                override(new CheckerModule()).with(new AppServletModule(libPaths)));

        startServices(injector);
        return injector;
    }

    private void startServices(Injector injector) {
        log.info("Starting services on context init");
        Service service = injector.getInstance(HibernateSessionFactoryProvider.class);
        service.start();

        service = injector.getInstance(CopyFileMessageQueue.class);
        service.start();
        servicesToStop.add(service);
    }

    private void stopServices() {
        log.info("Stopping services on context destroy");
        for (Service service : servicesToStop) {
            service.stop();
        }
    }

    private void lookupPropertiesInContext() {
        try {
            Context context = new InitialContext();
            setSystemProperty("annotare.properties", (String) lookup(context, "java:comp/env/annotareProperties"));
            setSystemProperty("checker.properties", (String) lookup(context, "java:comp/env/mageTabCheckProperties"));
        } catch (NamingException e) {
            log.error("Naming context initialization failure", e);
        }
    }

    private Object lookup(Context context, String name) throws NamingException {
        try {
            return context.lookup(name);
        } catch (NameNotFoundException e) {
            log.debug("name '{}' not found in the naming context", name);
            return null;
        }
    }

    private void setSystemProperty(String name, String value) {
        if (!isNullOrEmpty(value)) {
            log.info("set: {}='{}'", name, value);
            System.setProperty(name, value);
        }
    }

    private void findMageTabCheckAnnotationPackages() {
        /* note: better not to use forWebInfLib(), as you can't rely on servletContext.getResource(...) */
        //todo: move package names with magetabcheck annotations to the config
        libPaths.addAll(forPackage("uk.ac.ebi.fg.annotare2.magetabcheck.checks"));
    }

    private void updateDb(ServletContextEvent servletContextEvent) {
        try {
            Context ic = null;
            Connection connection = null;
            try {
                ic = new InitialContext();
                //todo get ds string from web.xml
                DataSource dataSource = (DataSource) ic.lookup("java:comp/env/jdbc/annotareDataSource");

                connection = dataSource.getConnection();

                Thread currentThread = Thread.currentThread();
                ClassLoader contextClassLoader = currentThread.getContextClassLoader();
                ResourceAccessor threadClFO = new ClassLoaderResourceAccessor(contextClassLoader);

                ResourceAccessor clFO = new ClassLoaderResourceAccessor();
                ResourceAccessor fsFO = new FileSystemResourceAccessor();


                Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
                //todo move to web.xml
                Liquibase liquibase = new Liquibase("uk/ac/ebi/fg/annotare2/db/changelog/changelog-master.xml", new CompositeResourceAccessor(clFO, fsFO, threadClFO), database);

                Enumeration<String> initParameters = servletContextEvent.getServletContext().getInitParameterNames();
                while (initParameters.hasMoreElements()) {
                    String name = initParameters.nextElement().trim();
                    if (name.startsWith("liquibase.parameter.")) {
                        liquibase.setChangeLogParameter(name.substring("liquibase.parameter".length()), servletContextEvent.getServletContext().getInitParameter(name));
                    }
                }

                liquibase.update("");
            } finally {
                if (ic != null) {
                    ic.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
