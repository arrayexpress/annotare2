/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import uk.ac.ebi.fg.annotare2.core.components.Messenger;
import uk.ac.ebi.fg.annotare2.db.model.User;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountService;

import javax.servlet.http.HttpSession;

import static com.google.common.base.Strings.nullToEmpty;

/**
 * @author Olga Melnichuk
 */
abstract class AuthBasedRemoteService extends RemoteServiceServlet {

    private final AccountService accountService;
    private final Messenger messenger;


    public AuthBasedRemoteService(AccountService accountService, Messenger messenger) {
        this.accountService = accountService;
        this.messenger = messenger;
    }

    protected HttpSession getSession() {
        return getThreadLocalRequest().getSession();
    }

    protected User getCurrentUser() {
        return accountService.getCurrentUser(getSession());
    }

    protected String getCurrentUserEmail() {
        return accountService.getCurrentUserEmail(getSession());
    }

    protected String getRequestURI() {
        return nullToEmpty(getThreadLocalRequest().getRequestURI());
    }

    protected String getRequestReferer() {
        return nullToEmpty(getThreadLocalRequest().getHeader("Referer"));
    }

    @Override
    protected void doUnexpectedFailure(Throwable e) {
        messenger.sendException(
                "Unexpected exception in RPC call\n" +
                        "URI: " + getRequestURI()  + "\n" +
                        "Referer: " + getRequestReferer() + "\n" +
                        "User: " + getCurrentUserEmail() + "",
                e)
        ;
        super.doUnexpectedFailure(e);
    }

}
