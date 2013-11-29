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

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.db.om.User;
import uk.ac.ebi.fg.annotare2.web.server.UnauthorizedAccessException;
import uk.ac.ebi.fg.annotare2.web.server.login.utils.FormParams;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountManager;
import uk.ac.ebi.fg.annotare2.web.server.login.utils.RequestParam;
import uk.ac.ebi.fg.annotare2.web.server.login.utils.SessionAttribute;
import uk.ac.ebi.fg.annotare2.web.server.login.utils.ValidationErrors;
import uk.ac.ebi.fg.annotare2.web.server.services.EmailSender;
import uk.ac.ebi.fg.annotare2.web.server.transaction.Transactional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Olga Melnichuk
 */
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private static final SessionAttribute USER_EMAIL_ATTRIBUTE = new SessionAttribute("email");

    private AccountManager accountManager;
    private EmailSender emailer;

    @Inject
    public AccountServiceImpl(AccountManager accountManager, EmailSender emailer) {
        this.accountManager = accountManager;
        this.emailer = emailer;
    }

    public boolean isLoggedIn(HttpServletRequest request) {
        return USER_EMAIL_ATTRIBUTE.exists(request.getSession());
    }

    @Transactional
    public ValidationErrors signUp(HttpServletRequest request) throws AccountServiceException {
        SignUpParams params = new SignUpParams(request);
        ValidationErrors errors = params.validate();
        if (errors.isEmpty()) {
            if (null != accountManager.getByEmail(params.getEmail())) {
                errors.append("email", "User with this email already exists");
            } else {
                User u = accountManager.createUser(params.getName(), params.getEmail(), params.getPassword());
                try {
                    emailer.sendFromTemplate(
                            EmailSender.NEW_USER_TEMPLATE,
                            ImmutableMap.of(
                                    "to.name", u.getName(),
                                    "to.email", u.getEmail(),
                                    "verification.token", u.getVerificationToken()
                            )
                    );
                } catch (MessagingException x) {
                    //
                }
            }
        }
        return errors;
    }

    @Transactional
    public ValidationErrors changePassword(HttpServletRequest request) throws AccountServiceException {
        SignUpParams params = new SignUpParams(request);
        ValidationErrors errors = params.validate();
        if (errors.isEmpty()) {
            if (null != accountManager.getByEmail(params.getEmail())) {
                errors.append("email", "User with this email already exists");
            } else {
                User u = accountManager.createUser(params.getName(), params.getEmail(), params.getPassword());
                try {
                    emailer.sendFromTemplate(
                            EmailSender.NEW_USER_TEMPLATE,
                            ImmutableMap.of(
                                    "to.name", u.getName(),
                                    "to.email", u.getEmail(),
                                    "verification.token", u.getVerificationToken()
                            )
                    );
                } catch (MessagingException x) {
                    //
                }
            }
        }
        return errors;
    }

    @Transactional
    public ValidationErrors login(HttpServletRequest request) throws AccountServiceException {
        LoginParams params = new LoginParams(request);
        ValidationErrors errors = params.validate();
        if (errors.isEmpty()) {
            if (!accountManager.isValid(params.getEmail(), params.getPassword())) {
                log.debug("User '{}' entered invalid params", params.getEmail());
                throw new AccountServiceException("Sorry, the email or password you entered is not valid.");
            }
            log.debug("User '{}' logged in", params.getEmail());
            USER_EMAIL_ATTRIBUTE.set(request.getSession(), params.getEmail());
        }
        return errors;
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public User getCurrentUser(HttpSession session) {
        String email = (String) USER_EMAIL_ATTRIBUTE.get(session);
        User user = accountManager.getByEmail(email);
        if (user == null) {
            throw new UnauthorizedAccessException("Sorry, you are not logged in");
        }
        return user;
    }

    static class LoginParams extends FormParams {

        private LoginParams(HttpServletRequest request) {
            addParam(RequestParam.from(request, EMAIL_PARAM), true);
            addParam(RequestParam.from(request, PASSWORD_PARAM), true);
        }

        public ValidationErrors validate() {
            return validateMandatory();
        }

        public String getEmail() {
            return getParamValue(EMAIL_PARAM);
        }

        public String getPassword() {
            return getParamValue(PASSWORD_PARAM);
        }
    }

    static class SignUpParams extends FormParams {

        private SignUpParams(HttpServletRequest request) {
            addParam(RequestParam.from(request, NAME_PARAM), true);
            addParam(RequestParam.from(request, EMAIL_PARAM), true);
            addParam(RequestParam.from(request, PASSWORD_PARAM), true);
            addParam(RequestParam.from(request, CONFIRM_PASSWORD_PARAM), false);
        }

        public ValidationErrors validate() {
            ValidationErrors errors = validateMandatory();

            if (!isEmailGoodEnough()) {
                errors.append(EMAIL_PARAM, "Email is not valid; should at least contain @ sign");
            }

            if (!isPasswordGoodEnough()) {
                errors.append(PASSWORD_PARAM, "Password is too weak; should be at least 4 characters long containing at least one digit");
            }

            if (!hasPasswordConfirmed()) {
                errors.append(CONFIRM_PASSWORD_PARAM, "Passwords do not match");
            }

            return errors;
        }

        public String getName() {
            return getParamValue(NAME_PARAM);
        }

        public String getEmail() {
            return getParamValue(EMAIL_PARAM);
        }

        public String getPassword() {
            return getParamValue(PASSWORD_PARAM);
        }
    }
}
