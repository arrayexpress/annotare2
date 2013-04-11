package uk.ac.ebi.fg.annotare2.prototypes.rf.server;

import com.google.inject.Scope;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.google.web.bindery.requestfactory.server.DefaultExceptionHandler;
import com.google.web.bindery.requestfactory.server.ExceptionHandler;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;
import uk.ac.ebi.fg.annotare2.prototypes.rf.server.services.SubmissionService;

/**
 * @author Olga Melnichuk
 */
public class TestAppServletModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(ExceptionHandler.class).to(DefaultExceptionHandler.class);
        bind(SubmissionService.class).in(Scopes.SINGLETON);

        bind(RequestFactoryServlet.class).in(Singleton.class);

        serve("/gwtRequest").with(GuiceRequestFactoryServlet.class);
    }
}