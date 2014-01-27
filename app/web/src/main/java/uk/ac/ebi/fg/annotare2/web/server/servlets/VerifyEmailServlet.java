/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

import static uk.ac.ebi.fg.annotare2.web.server.servlets.ServletNavigation.LOGIN;
import static uk.ac.ebi.fg.annotare2.web.server.servlets.ServletNavigation.VERIFY_EMAIL;
import static uk.ac.ebi.fg.annotare2.web.server.servlets.SessionInformation.EMAIL_SESSION_ATTRIBUTE;
import static uk.ac.ebi.fg.annotare2.web.server.servlets.SessionInformation.INFO_SESSION_ATTRIBUTE;

public class VerifyEmailServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(VerifyEmailServlet.class);

    @Inject
    private AccountService accountService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ValidationErrors errors = new ValidationErrors();
        if (null != request.getParameter(FormParams.EMAIL_PARAM)) {
            log.debug("Email verification request received; processing");

            try {
                errors.append(accountService.verifyEmail(request));
                EMAIL_SESSION_ATTRIBUTE.set(request.getSession(), request.getParameter(FormParams.EMAIL_PARAM));
                if (errors.isEmpty()) {
                    log.debug("Email verification successful; redirecting to log in");
                    INFO_SESSION_ATTRIBUTE.set(request.getSession(), "You have successfully verified your email address; please sign in now");
                    LOGIN.redirect(request, response);
                    return;
                }
            } catch (AccountServiceException e) {
                log.debug("Email verification request failed", e);
                errors.append(e.getMessage());
            }
            request.setAttribute("errors", errors);
        } else if (null != request.getParameter(FormParams.RESEND_PARAM)) {
            try {
                errors.append(accountService.resendVerifyEmail((String) EMAIL_SESSION_ATTRIBUTE.get(request.getSession())));
                if (errors.isEmpty()) {
                    INFO_SESSION_ATTRIBUTE.set(request.getSession(), "Email verification code has been re-sent to you, please enter it now");
                }
            } catch (AccountServiceException e) {
                log.debug("Email verification request failed", e);
                errors.append(e.getMessage());
            }
        }

        request.setAttribute("errors", errors);
        VERIFY_EMAIL.forward(getServletConfig().getServletContext(), request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
