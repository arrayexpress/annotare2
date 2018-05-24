package uk.ac.ebi.fg.annotare2.web.server.servlets;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountService;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountServiceException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static uk.ac.ebi.fg.annotare2.web.server.servlets.ServletNavigation.HOME;
import static uk.ac.ebi.fg.annotare2.web.server.servlets.ServletNavigation.PRIVACY_NOTICE;

/**
 * Created by haideri on 23/05/2018.
 */
public class PrivacyNoticeServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(PrivacyNoticeServlet.class);

    @Inject
    private AccountService accountService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        try {
            accountService.setPrivacyNoticeVersion(request,1);
            HOME.restoreAndRedirect(request, response);
        }catch (AccountServiceException e){
            log.debug("Privacy Notice Acceptance failed.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PRIVACY_NOTICE.forward(getServletConfig().getServletContext(), request, response);
    }
}
