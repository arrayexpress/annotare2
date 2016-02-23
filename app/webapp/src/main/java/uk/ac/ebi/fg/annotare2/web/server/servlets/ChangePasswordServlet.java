/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.server.servlets;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountService;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountServiceException;
import uk.ac.ebi.fg.annotare2.web.server.servlets.utils.FormParams;
import uk.ac.ebi.fg.annotare2.web.server.servlets.utils.ValidationErrors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static uk.ac.ebi.fg.annotare2.web.server.servlets.ServletNavigation.CHANGE_PASSWORD;
import static uk.ac.ebi.fg.annotare2.web.server.servlets.ServletNavigation.LOGIN;
import static uk.ac.ebi.fg.annotare2.web.server.servlets.SessionInformation.EMAIL_SESSION_ATTRIBUTE;
import static uk.ac.ebi.fg.annotare2.web.server.servlets.SessionInformation.INFO_SESSION_ATTRIBUTE;

public class ChangePasswordServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(ChangePasswordServlet.class);

    @Inject
    private AccountService accountService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("Change password request received; processing");

        String phase = "email";
        ValidationErrors errors = new ValidationErrors();

        if (null != request.getParameter(FormParams.EMAIL_PARAM)) {
            try {
                errors.append(accountService.changePassword(request));
                if (errors.isEmpty()) {
                    if (null == request.getParameter(FormParams.TOKEN_PARAM)) {
                        log.debug("Change request email has been sent; show information");
                        INFO_SESSION_ATTRIBUTE.set(request.getSession(), "Email sent to the specified address; please check your mailbox");
                        phase = "token";
                    } else if (null == request.getParameter(FormParams.PASSWORD_PARAM)) {
                        log.debug("Token validated; enable password inputs");
                        phase = "password";
                    } else {
                        EMAIL_SESSION_ATTRIBUTE.set(request.getSession(), request.getParameter(FormParams.EMAIL_PARAM));
                        INFO_SESSION_ATTRIBUTE.set(request.getSession(), "You have successfully changed password; please sign in now");
                        LOGIN.redirect(request, response);
                        return;
                    }
                } else {
                    phase = request.getParameter("phase");
                }
            } catch (AccountServiceException e) {
                log.debug("Change password request failed", e);
                errors.append(e.getMessage());
            }
        }
        request.setAttribute("errors", errors);
        request.setAttribute("phase", phase);
        CHANGE_PASSWORD.forward(getServletConfig().getServletContext(), request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
