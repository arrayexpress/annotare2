/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.magetabcheck.CheckerModule;
import uk.ac.ebi.fg.annotare2.web.server.services.SubsTrackingWatchdog;
import uk.ac.ebi.fg.annotare2.web.server.services.ae.ArrayExpressArrayDesignList;
import uk.ac.ebi.fg.annotare2.web.server.services.files.DataFilesPeriodicProcess;
import uk.ac.ebi.fg.annotare2.web.server.services.migration.SubmissionMigrator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.inject.util.Modules.override;
import static org.reflections.util.ClasspathHelper.forPackage;

/**
 * @author Olga Melnichuk
 */
public class AppServletContextListener extends GuiceServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(AppServletContextListener.class);

    private Set<URL> libPaths = newHashSet();
    private Injector injector = null;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        findMageTabCheckAnnotationPackages();

        lookupPropertiesInContext();

        super.contextInitialized(servletContextEvent);

        startServices();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        stopServices();

        unregisterJdbc();

        super.contextDestroyed(servletContextEvent);
    }

    @Override
    protected Injector getInjector() {
        if (null == injector) {
            injector = Guice.createInjector(Stage.DEVELOPMENT,
                    override(new CheckerModule()).with(new AppServletModule(libPaths)));
        }
        return injector;
    }

    private void startServices() {
        log.info("Starting services on context init...");
        injector.getInstance(HibernateSessionFactoryProvider.class).start();
        injector.getInstance(SubmissionMigrator.class).start();
        injector.getInstance(DataFilesPeriodicProcess.class).start();
        injector.getInstance(SubsTrackingWatchdog.class).start();
        injector.getInstance(ArrayExpressArrayDesignList.class).start();
    }

    private void stopServices() {
        log.info("Stopping services on context destroy");
        injector.getInstance(ArrayExpressArrayDesignList.class).stop();
        injector.getInstance(SubsTrackingWatchdog.class).stop();
        injector.getInstance(DataFilesPeriodicProcess.class).stop();
        injector.getInstance(SubmissionMigrator.class).stop();
        injector.getInstance(HibernateSessionFactoryProvider.class).stop();
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
}
