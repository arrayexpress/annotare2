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

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.om.User;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.UserAccountService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UserInfo;
import uk.ac.ebi.fg.annotare2.web.server.auth.AuthService;

import javax.servlet.http.HttpSession;

/**
 * @author Olga Melnichuk
 */
public class UserAccountServiceImpl extends RemoteServiceServlet implements UserAccountService {

    @Inject
    private AuthService authService;

    public UserInfo getCurrentUser() {
        User user = authService.getCurrentUser(getSession());
        return new UserInfo(user.getEmail());
    }

    public void logout() {
        authService.logout(getSession());
    }

    private HttpSession getSession() {
        return getThreadLocalRequest().getSession();
    }
}
