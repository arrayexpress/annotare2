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

import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import gwtupload.server.UploadServlet;
import uk.ac.ebi.fg.annotare2.dao.SubmissionDao;
import uk.ac.ebi.fg.annotare2.dao.UserDao;
import uk.ac.ebi.fg.annotare2.dao.dummy.SubmissionDaoDummy;
import uk.ac.ebi.fg.annotare2.dao.dummy.UserDaoDummy;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.AnnotareCheckListProvider;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckDefinition;
import uk.ac.ebi.fg.annotare2.services.efo.EfoService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.*;
import uk.ac.ebi.fg.annotare2.web.server.login.*;
import uk.ac.ebi.fg.annotare2.web.server.rpc.*;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountManager;
import uk.ac.ebi.fg.annotare2.web.server.services.AnnotareEfoService;
import uk.ac.ebi.fg.annotare2.web.server.services.ae.ArrayExpressArrayDesignList;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;

import javax.servlet.http.HttpServlet;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.google.inject.Scopes.SINGLETON;

/**
 * @author Olga Melnichuk
 */
public class AppServletModule extends ServletModule {

    private final AllRpcServicePathsImpl allRpc = new AllRpcServicePathsImpl();

    private static final String JSESSIONID = "(?:;jsessionid=[A-Za-z0-9]+)?";

    private final Set<URL> libPaths = newHashSet();

    public AppServletModule(Set<URL> libPaths) {
        this.libPaths.addAll(libPaths);
    }

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

        bind(SecurityFilter.class).in(SINGLETON);
        bind(LoginServlet.class).in(SINGLETON);
        bind(LogoutServlet.class).in(SINGLETON);
        bind(HomeServlet.class).in(SINGLETON);
        bind(EditorServlet.class).in(SINGLETON);
        bind(WelcomeServlet.class).in(SINGLETON);
        bind(UploadServlet.class).in(SINGLETON);

        serveAndBindRpcService(CurrentUserAccountService.NAME, CurrentUserAccountServiceImpl.class, "UserApp");
        serveAndBindRpcService(SubmissionListService.NAME, SubmissionListServiceImpl.class, "UserApp");

        serveAndBindRpcService(SubmissionService.NAME, SubmissionServiceImpl.class, "UserApp", "EditorApp");

        serveAndBindRpcService(AdfService.NAME, AdfServiceImpl.class, "EditorApp");
        serveAndBindRpcService(SubmissionValidationService.NAME, SubmissionValidationServiceImpl.class, "EditorApp");
        serveAndBindRpcService(VocabularyService.NAME, VocabularyServiceImpl.class, "EditorApp");
        serveAndBindRpcService(DataService.NAME, DataServiceImpl.class, "EditorApp");

        bind(SubmissionListServiceImpl.class).in(SINGLETON);

        bind(UserDao.class).to(UserDaoDummy.class).in(SINGLETON);
        bind(SubmissionDao.class).to(SubmissionDaoDummy.class).in(SINGLETON);
        bind(AccountManager.class).in(SINGLETON);
        bind(SubmissionManager.class).in(SINGLETON);

        bind(AuthService.class).to(AuthServiceImpl.class).in(SINGLETON);
        bind(AllRpcServicePaths.class).toInstance(allRpc);

        bind(AnnotareProperties.class).asEagerSingleton();
        bind(AnnotareEfoService.class).asEagerSingleton();

        overrideMageTabCheck();
    }

    @Singleton
    @Provides
    public ArrayExpressArrayDesignList getArrayExpressArrayDesignList() {
        return ArrayExpressArrayDesignList.create();
    }

    private void overrideMageTabCheck() {
        bind(new TypeLiteral<Set<URL>>() {
        }).annotatedWith(Names.named("libPaths")).toInstance(libPaths);

        bind(new TypeLiteral<List<CheckDefinition>>() {
        }).toProvider(AnnotareCheckListProvider.class).in(SINGLETON);

        bind(EfoService.class).to(AnnotareEfoService.class);
    }

    private void serveAndBindRpcService(String serviceName, Class<? extends HttpServlet> implClass, String... moduleNames) {
        for (String moduleName : moduleNames) {
            String servicePath = "/" + moduleName + "/" + serviceName;
            serve(servicePath).with(implClass);
            allRpc.awareOf(servicePath);
        }

        bind(implClass).in(SINGLETON);
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
