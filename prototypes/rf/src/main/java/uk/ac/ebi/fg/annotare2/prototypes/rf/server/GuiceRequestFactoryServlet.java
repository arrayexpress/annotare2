package uk.ac.ebi.fg.annotare2.prototypes.rf.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.requestfactory.server.ExceptionHandler;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;

/**
 * @author Olga Melnichuk
 */
@Singleton
public class GuiceRequestFactoryServlet extends RequestFactoryServlet {

    @Inject
    GuiceRequestFactoryServlet(ExceptionHandler exceptionHandler, GuiceServiceLayer guiceServiceLayer) {
        super(exceptionHandler, guiceServiceLayer);
    }
}
