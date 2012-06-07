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

package uk.ac.ebi.fg.annotare2.web.server.auth;

import uk.ac.ebi.fg.annotare2.web.server.servlet.utils.RequestParam;
import uk.ac.ebi.fg.annotare2.web.server.servlet.utils.SessionAttribute;
import uk.ac.ebi.fg.annotare2.web.server.servlet.utils.ValidationErrors;

import javax.servlet.http.HttpServletRequest;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final SessionAttribute USERNAME = new SessionAttribute("username");

    public boolean isLoggedIn(HttpServletRequest request) {
        return USERNAME.exists(request.getSession());
    }

    public ValidationErrors login(HttpServletRequest request) throws LoginException {
        LoginParams params = new LoginParams(request);
        ValidationErrors errors = params.validate();
        if (errors.isEmpty()) {

/*      TODO
        if (!accountManager.contains(params.getUsername(), params.getPassword())) {
           throw new LoginException("User ");
        }
*/
            USERNAME.set(request.getSession(), params.getUsername());
        }
        return errors;
    }

    public void logout(HttpServletRequest request) {
        USERNAME.remove(request.getSession());
    }

    private static class LoginParams {
        private final RequestParam username;
        private final RequestParam password;

        private LoginParams(HttpServletRequest request) {
            username = RequestParam.from(request, "username");
            password = RequestParam.from(request, "password");
        }

        public ValidationErrors validate() {
            ValidationErrors errors = new ValidationErrors();
            for (RequestParam p : asList(username, password)) {
                if (p.isEmpty()) {
                    errors.append(p.getName(), "Please specify some value.");
                }
            }
            return errors;
        }

        public String getUsername() {
            return username.getValue();
        }

        public String getPassword() {
            return password.getValue();
        }
    }
}
