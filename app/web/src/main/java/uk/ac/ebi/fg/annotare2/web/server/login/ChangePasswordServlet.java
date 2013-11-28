package uk.ac.ebi.fg.annotare2.web.server.login;

/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.web.server.login.utils.ValidationErrors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static uk.ac.ebi.fg.annotare2.web.server.login.ServletNavigation.LOGIN;
import static uk.ac.ebi.fg.annotare2.web.server.login.ServletNavigation.SIGNUP;

public class ChangePasswordServlet extends HttpServlet {
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
                LOGIN.redirect(request, response);
                return;
            }
            log.debug("Sign-up form had invalid entries");
        } catch (Exception e) {
            log.debug("Sign-up failed");
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
