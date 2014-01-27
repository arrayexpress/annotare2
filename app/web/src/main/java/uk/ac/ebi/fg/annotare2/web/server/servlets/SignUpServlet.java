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

import static uk.ac.ebi.fg.annotare2.web.server.servlets.ServletNavigation.SIGNUP;
import static uk.ac.ebi.fg.annotare2.web.server.servlets.ServletNavigation.VERIFY_EMAIL;
import static uk.ac.ebi.fg.annotare2.web.server.servlets.SessionInformation.EMAIL_SESSION_ATTRIBUTE;
import static uk.ac.ebi.fg.annotare2.web.server.servlets.SessionInformation.INFO_SESSION_ATTRIBUTE;

public class SignUpServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(SignUpServlet.class);

    @Inject
    private AccountService accountService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("Sign-up data submitted; checking..");
        ValidationErrors errors = new ValidationErrors();
        try {
            errors.append(accountService.signUp(request));
            if (errors.isEmpty()) {
                log.debug("Sign-up successful; redirect to login page");
                EMAIL_SESSION_ATTRIBUTE.set(request.getSession(), request.getParameter(FormParams.EMAIL_PARAM));
                INFO_SESSION_ATTRIBUTE.set(request.getSession(), "Registration successful; email verification code has been sent to you, please enter it now");
                VERIFY_EMAIL.redirect(request, response);
                return;
            } else {
                log.debug("Sign-up form failed validation");
            }
        } catch (AccountServiceException e) {
            log.debug("Sign-up failed", e);
            errors.append(e.getMessage());
        }

        request.setAttribute("errors", errors);
        SIGNUP.forward(getServletConfig().getServletContext(), request, response);

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SIGNUP.forward(getServletConfig().getServletContext(), request, response);
    }
}
