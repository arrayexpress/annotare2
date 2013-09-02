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


import uk.ac.ebi.fg.annotare2.db.om.User;
import uk.ac.ebi.fg.annotare2.web.server.login.utils.ValidationErrors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Olga Melnichuk
 */
public interface AuthService {

    boolean isLoggedIn(HttpServletRequest request);

    ValidationErrors login(HttpServletRequest request) throws LoginException;

    void logout(HttpSession session);

    User getCurrentUser(HttpSession session);
}
