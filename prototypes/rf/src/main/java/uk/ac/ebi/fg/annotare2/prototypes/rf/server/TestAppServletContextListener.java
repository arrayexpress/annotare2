package uk.ac.ebi.fg.annotare2.prototypes.rf.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * @author Olga Melnichuk
 */
public class TestAppServletContextListener extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(Stage.DEVELOPMENT, new TestAppServletModule());
    }
}
