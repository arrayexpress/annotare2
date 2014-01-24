/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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
import uk.ac.ebi.fg.annotare2.db.model.User;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountService;

import javax.servlet.http.HttpSession;

/**
 * @author Olga Melnichuk
 */
abstract class AuthBasedRemoteService extends RemoteServiceServlet {

    private final AccountService accountService;

    public AuthBasedRemoteService(AccountService accountService) {
        this.accountService = accountService;
    }

    protected User getCurrentUser() {
        return accountService.getCurrentUser(getSession());
    }

    protected HttpSession getSession() {
        return getThreadLocalRequest().getSession();
    }
}
