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

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.db.om.User;
import uk.ac.ebi.fg.annotare2.web.server.login.utils.RequestParam;
import uk.ac.ebi.fg.annotare2.web.server.login.utils.ValidationErrors;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountManager;
import uk.ac.ebi.fg.annotare2.web.server.services.EmailSender;
import uk.ac.ebi.fg.annotare2.web.server.transaction.Transactional;

import javax.servlet.http.HttpServletRequest;

import static java.util.Arrays.asList;

public class SignUpServiceImpl implements SignUpService {

    @Inject
    private AccountManager manager;

    @Inject
    private EmailSender emailer;

    @Transactional
    public ValidationErrors signUp(HttpServletRequest request) throws Exception {
        SignUpParams params = new SignUpParams(request);
        ValidationErrors errors = params.validate();
        if (errors.isEmpty()) {
            User u = manager.createUser(params.getName(), params.getEmail(), params.getPassword());
            emailer.sendFromTemplate(
                    EmailSender.NEW_USER_TEMPLATE,
                    ImmutableMap.of(
                            "to.name", u.getName(),
                            "to.email", u.getEmail(),
                            "verification.token", u.getVerificationToken()
                    )
            );
        }
        return errors;

    }

    static class SignUpParams {
        public static final String NAME_PARAM = "name";
        public static final String EMAIL_PARAM = "email";
        public static final String PASSWORD_PARAM = "password";
        public static final String CONFIRM_PASSWORD_PARAM = "confirm-password";

        private final RequestParam name;
        private final RequestParam email;
        private final RequestParam password;
        private final RequestParam confirmPassword;

        private SignUpParams(HttpServletRequest request) {
            name = RequestParam.from(request, NAME_PARAM);
            email = RequestParam.from(request, EMAIL_PARAM);
            password = RequestParam.from(request, PASSWORD_PARAM);
            confirmPassword = RequestParam.from(request, CONFIRM_PASSWORD_PARAM);
        }

        public ValidationErrors validate() {
            ValidationErrors errors = new ValidationErrors();
            for (RequestParam p : asList(name, email, password)) {
                if (p.isEmpty()) {
                    errors.append(p.getName(), "Field \"" + p.getName() + "\" is required");
                }
            }
            if (!password.getValue().equals(confirmPassword.getValue())) {
                errors.append(confirmPassword.getName(), "Passwords do not match");
            }
            return errors;
        }

        public String getName() {
            return name.getValue();
        }

        public String getEmail() {
            return email.getValue();
        }

        public String getPassword() {
            return password.getValue();
        }
    }
}
