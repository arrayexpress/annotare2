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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceServletContextListener;
import org.reflections.util.ClasspathHelper;
import uk.ac.ebi.fg.annotare2.magetab.init.Magetab;
import uk.ac.ebi.fg.annotare2.magetabcheck.CheckerModule;

import javax.servlet.ServletContextEvent;
import java.net.URL;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.google.inject.util.Modules.override;

/**
 * @author Olga Melnichuk
 */
public class AppServletContextListener extends GuiceServletContextListener {

    private Set<URL> libPaths = newHashSet();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        libPaths.addAll(ClasspathHelper.forWebInfLib(servletContextEvent.getServletContext()));
        Magetab.init();
        super.contextInitialized(servletContextEvent);
    }

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(Stage.DEVELOPMENT,
                override(new CheckerModule()).with(new AppServletModule(libPaths)));
    }
}
