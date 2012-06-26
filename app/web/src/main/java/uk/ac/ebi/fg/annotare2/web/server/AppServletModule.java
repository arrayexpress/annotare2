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

package uk.ac.ebi.fg.annotare2.web.server;

import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;
import uk.ac.ebi.fg.annotare2.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.dao.UserDao;
import uk.ac.ebi.fg.annotare2.dao.dummy.SubmissionDaoDummy;
import uk.ac.ebi.fg.annotare2.dao.dummy.UserDaoDummy;
import uk.ac.ebi.fg.annotare2.web.server.auth.*;
import uk.ac.ebi.fg.annotare2.web.server.rpc.CurrentUserAccountServiceImpl;
import uk.ac.ebi.fg.annotare2.web.server.rpc.SubmissionServiceImpl;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountManager;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;

/**
 * @author Olga Melnichuk
 */
public class AppServletModule extends ServletModule {

    @Override
    protected void configureServlets() {
        filter("/UserApp/*", "/index.html").through(SecurityFilter.class);

        serveRegex("(/login)(?:;jsessionid=[A-Z0-9]+)?").with(LoginServlet.class);
        serveRegex("(/logout)(?:;jsessionid=[A-Z0-9]+)?").with(LogoutServlet.class);

        bind(SecurityFilter.class).in(Scopes.SINGLETON);
        bind(LoginServlet.class).in(Scopes.SINGLETON);
        bind(LogoutServlet.class).in(Scopes.SINGLETON);

        serve("/UserApp/me").with(CurrentUserAccountServiceImpl.class);
        bind(CurrentUserAccountServiceImpl.class).in(Scopes.SINGLETON);

        serve("/UserApp/mySubmissions").with(SubmissionServiceImpl.class);
        bind(SubmissionServiceImpl.class).in(Scopes.SINGLETON);

        bind(UserDao.class).to(UserDaoDummy.class).in(Scopes.SINGLETON);
        bind(SubmissionDao.class).to(SubmissionDaoDummy.class).in(Scopes.SINGLETON);
        bind(AccountManager.class).in(Scopes.SINGLETON);
        bind(SubmissionManager.class).in(Scopes.SINGLETON);

        bind(AuthService.class).to(AuthServiceImpl.class).in(Scopes.SINGLETON);
    }
}
