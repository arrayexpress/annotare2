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
import uk.ac.ebi.fg.annotare2.db.om.User;
import uk.ac.ebi.fg.annotare2.web.server.UnauthorizedAccessException;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountManager;
import uk.ac.ebi.fg.annotare2.web.server.login.utils.RequestParam;
import uk.ac.ebi.fg.annotare2.web.server.login.utils.SessionAttribute;
import uk.ac.ebi.fg.annotare2.web.server.login.utils.ValidationErrors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private static final SessionAttribute USER_EMAIL = new SessionAttribute("email");

    private AccountManager accountManager;

    @Inject
    public AuthServiceImpl(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public boolean isLoggedIn(HttpServletRequest request) {
        return USER_EMAIL.exists(request.getSession());
    }

    public ValidationErrors login(HttpServletRequest request) throws LoginException {
        LoginParams params = new LoginParams(request);
        ValidationErrors errors = params.validate();
        if (errors.isEmpty()) {
            if (!accountManager.isValid(params.getEmail(), params.getPassword())) {
                log.debug("User '{}' entered invalid params", params.getEmail());
                throw new LoginException("Sorry, the email or password you entered is not valid.");
            }
            log.debug("User '{}' logged in", params.getEmail());
            USER_EMAIL.set(request.getSession(), params.getEmail());
        }
        return errors;
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public User getCurrentUser(HttpSession session) {
        String email = (String) USER_EMAIL.get(session);
        User user = accountManager.getByEmail(email);
        if (user == null) {
            throw new UnauthorizedAccessException("Sorry, you are not logged in");
        }
        return user;
    }

    static class LoginParams {
        public static final String EMAIL_PARAM = "email";
        public static final String PASSWORD_PARAM = "password";

        private final RequestParam email;
        private final RequestParam password;

        private LoginParams(HttpServletRequest request) {
            email = RequestParam.from(request, EMAIL_PARAM);
            password = RequestParam.from(request, PASSWORD_PARAM);
        }

        public ValidationErrors validate() {
            ValidationErrors errors = new ValidationErrors();
            for (RequestParam p : asList(email, password)) {
                if (p.isEmpty()) {
                    errors.append(p.getName(), "Please specify a value, " + p.getName() + " is required");
                }
            }
            return errors;
        }

        public String getEmail() {
            return email.getValue();
        }

        public String getPassword() {
            return password.getValue();
        }
    }
}
