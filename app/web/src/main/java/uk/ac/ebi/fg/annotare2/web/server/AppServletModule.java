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
import gwtupload.server.UploadServlet;
import uk.ac.ebi.fg.annotare2.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.dao.UserDao;
import uk.ac.ebi.fg.annotare2.dao.dummy.SubmissionDaoDummy;
import uk.ac.ebi.fg.annotare2.dao.dummy.UserDaoDummy;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.CurrentUserAccountService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.IdfService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionListService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionService;
import uk.ac.ebi.fg.annotare2.web.server.login.*;
import uk.ac.ebi.fg.annotare2.web.server.rpc.CurrentUserAccountServiceImpl;
import uk.ac.ebi.fg.annotare2.web.server.rpc.IdfServiceImpl;
import uk.ac.ebi.fg.annotare2.web.server.rpc.SubmissionListServiceImpl;
import uk.ac.ebi.fg.annotare2.web.server.rpc.SubmissionServiceImpl;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountManager;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;

import javax.servlet.http.HttpServlet;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Olga Melnichuk
 */
public class AppServletModule extends ServletModule {

    private final AllRpcServicePathsImpl allRpc = new AllRpcServicePathsImpl();

    private static final String JSESSIONID = "(?:;jsessionid=[A-Za-z0-9]+)?";

    @Override
    protected void configureServlets() {
        filter("/UserApp/*",
                "/",
                "/EditorApp/*",
                "/edit/*"
        ).through(SecurityFilter.class);

        serveRegex("(/login)" + JSESSIONID).with(LoginServlet.class);
        serveRegex("(/logout)" + JSESSIONID).with(LogoutServlet.class);
        serveRegex("(/)" + JSESSIONID).with(HomeServlet.class);
        serveRegex("(/edit/[0-9]+/)" + JSESSIONID).with(EditorServlet.class);
        serveRegex("(/index.*)").with(WelcomeServlet.class);
        serveRegex(".*\\.gupld").with(UploadServlet.class);

        bind(SecurityFilter.class).in(Scopes.SINGLETON);
        bind(LoginServlet.class).in(Scopes.SINGLETON);
        bind(LogoutServlet.class).in(Scopes.SINGLETON);
        bind(HomeServlet.class).in(Scopes.SINGLETON);
        bind(EditorServlet.class).in(Scopes.SINGLETON);
        bind(WelcomeServlet.class).in(Scopes.SINGLETON);
        bind(UploadServlet.class).in(Scopes.SINGLETON);

        serveAndBindRpcService("UserApp", CurrentUserAccountService.NAME, CurrentUserAccountServiceImpl.class);
        serveAndBindRpcService("UserApp", SubmissionListService.NAME, SubmissionListServiceImpl.class);
        serveAndBindRpcService("UserApp", SubmissionService.NAME, SubmissionServiceImpl.class);

        serveAndBindRpcService("EditorApp", IdfService.NAME, IdfServiceImpl.class);

        bind(SubmissionListServiceImpl.class).in(Scopes.SINGLETON);

        bind(UserDao.class).to(UserDaoDummy.class).in(Scopes.SINGLETON);
        bind(SubmissionDao.class).to(SubmissionDaoDummy.class).in(Scopes.SINGLETON);
        bind(AccountManager.class).in(Scopes.SINGLETON);
        bind(SubmissionManager.class).in(Scopes.SINGLETON);

        bind(AuthService.class).to(AuthServiceImpl.class).in(Scopes.SINGLETON);
        bind(AllRpcServicePaths.class).toInstance(allRpc);
    }

    private void serveAndBindRpcService(String moduleName, String serviceName, Class<? extends HttpServlet> implClass) {
        String servicePath = "/" + moduleName + "/" + serviceName;

        serve(servicePath).with(implClass);
        bind(implClass).in(Scopes.SINGLETON);

        allRpc.awareOf(servicePath);
    }

    static class AllRpcServicePathsImpl implements AllRpcServicePaths {

        private final Set<String> paths = new HashSet<String>();

        public boolean recognizeUri(String uri) {
            return paths.contains(uri);
        }

        void awareOf(String path) {
            paths.add(path);
        }
    }
}
