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

package uk.ac.ebi.fg.annotare2.web.server.login;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.web.server.servlet.utils.ValidationErrors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static uk.ac.ebi.fg.annotare2.web.server.login.ServletNavigation.*;

/**
 * @author Olga Melnichuk
 */
public class LoginServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    @Inject
    private AuthService authService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("Login details submitted; validating..");
        ValidationErrors errors = new ValidationErrors();
        try {
            errors.append(authService.login(request));
            if (errors.isEmpty()) {
                log.debug("Login details are valid; Authorization succeeded");
                HOME.restoreAndRedirect(request, response);
                return;
            }
            log.debug("Login detail are invalid");
        } catch (LoginException e) {
            log.debug("Authorization failed");
            errors.append(e.getMessage());
        }

        request.setAttribute("errors", errors);
        LOGIN.forward(getServletConfig().getServletContext(), request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGIN.forward(getServletConfig().getServletContext(), request, response);
    }
}
