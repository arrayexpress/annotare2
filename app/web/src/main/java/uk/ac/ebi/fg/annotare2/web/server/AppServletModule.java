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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import uk.ac.ebi.fg.annotare2.ae.AEConnection;
import uk.ac.ebi.fg.annotare2.ae.AEConnectionProperties;
import uk.ac.ebi.fg.annotare2.autosubs.SubsTracking;
import uk.ac.ebi.fg.annotare2.autosubs.SubsTrackingProperties;
import uk.ac.ebi.fg.annotare2.db.dao.*;
import uk.ac.ebi.fg.annotare2.db.dao.impl.*;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.AnnotareCheckListProvider;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckDefinition;
import uk.ac.ebi.fg.annotare2.magetabcheck.efo.EfoService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.*;
import uk.ac.ebi.fg.annotare2.web.server.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.web.server.properties.DataFileStoreProperties;
import uk.ac.ebi.fg.annotare2.web.server.rpc.*;
import uk.ac.ebi.fg.annotare2.web.server.services.*;
import uk.ac.ebi.fg.annotare2.web.server.services.ae.ArrayExpressArrayDesignList;
import uk.ac.ebi.fg.annotare2.web.server.services.ae.ArrayExpressExperimentTypeList;
import uk.ac.ebi.fg.annotare2.web.server.services.ae.ArrayExpressProperties;
import uk.ac.ebi.fg.annotare2.web.server.services.files.DataFileConnector;
import uk.ac.ebi.fg.annotare2.web.server.services.files.DataFilesPeriodicProcess;
import uk.ac.ebi.fg.annotare2.web.server.services.files.FtpManager;
import uk.ac.ebi.fg.annotare2.web.server.services.migration.SubmissionMigrator;
import uk.ac.ebi.fg.annotare2.web.server.servlets.*;
import uk.ac.ebi.fg.annotare2.web.server.transaction.Transactional;
import uk.ac.ebi.fg.annotare2.web.server.transaction.TransactionalMethodInterceptor;
import uk.ac.ebi.fg.gwt.resumable.server.ResumableUploadServlet;

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
        filterRegex("^/.+[.]nocache[.]js",
                "^/status/?").through(ExpiresNowFilter.class);

        filterRegex("^/status/?").through(AccessLoggingSuppressFilter.class);

        filter("/login",
                "/logout",
                "/export",
                "/download",
                "/sign-up",
                "/verify-email",
                "/change-password",
                "/UserApp/*",
                "/EditorApp/*").through(HibernateSessionFilter.class);

        filter("/UserApp/*",
                "/",
                "/EditorApp/*",
                "/edit/*",
                "/upload/",
                "/export",
                "/download").through(SecurityFilter.class);

        serveRegex("^/status/?").with(StatusServlet.class);
        serveRegex("^/upload/").with(ResumableUploadServlet.class);

        serveRegex("(/login)" + JSESSIONID).with(LoginServlet.class);
        serveRegex("(/logout)" + JSESSIONID).with(LogoutServlet.class);
        serveRegex("(/)" + JSESSIONID).with(HomeServlet.class);
        serveRegex("(/edit/[0-9]+/)" + JSESSIONID).with(EditorServlet.class);
        serveRegex("(/index.*)").with(WelcomeServlet.class);
        serveRegex("/sign-up" + JSESSIONID).with(SignUpServlet.class);
        serveRegex("/verify-email" + JSESSIONID).with(VerifyEmailServlet.class);
        serveRegex("/change-password" + JSESSIONID).with(ChangePasswordServlet.class);
        serve("/export").with(ExportServlet.class);
        serve("/download").with(DownloadServlet.class);
        serve("/error").with(UncaughtExceptionServlet.class);

        bind(ExpiresNowFilter.class).in(SINGLETON);
        bind(AccessLoggingSuppressFilter.class).in(SINGLETON);
        bind(HibernateSessionFilter.class).in(SINGLETON);
        bind(SecurityFilter.class).in(SINGLETON);
        bind(StatusServlet.class).in(SINGLETON);
        bind(LoginServlet.class).in(SINGLETON);
        bind(LogoutServlet.class).in(SINGLETON);
        bind(HomeServlet.class).in(SINGLETON);
        bind(EditorServlet.class).in(SINGLETON);
        bind(WelcomeServlet.class).in(SINGLETON);
        bind(ResumableUploadServlet.class).in(SINGLETON);
        bind(ExportServlet.class).in(SINGLETON);
        bind(DownloadServlet.class).in(SINGLETON);
        bind(SignUpServlet.class).in(SINGLETON);
        bind(VerifyEmailServlet.class).in(SINGLETON);
        bind(ChangePasswordServlet.class).in(SINGLETON);
        bind(UncaughtExceptionServlet.class).in(SINGLETON);

        // shared services
        serveAndBindRpcService(ApplicationDataService.NAME, ApplicationDataServiceImpl.class, "UserApp", "EditorApp");
        serveAndBindRpcService(CurrentUserAccountService.NAME, CurrentUserAccountServiceImpl.class, "UserApp", "EditorApp");
        serveAndBindRpcService(DataFilesService.NAME, DataFilesServiceImpl.class, "UserApp", "EditorApp");
        serveAndBindRpcService(SubmissionService.NAME, SubmissionServiceImpl.class, "UserApp", "EditorApp");
        // remote logging
        serveAndBindRpcService("remote_logging", RemoteLoggingServiceImpl.class, "UserApp", "EditorApp");
        // user app services
        serveAndBindRpcService(SubmissionListService.NAME, SubmissionListServiceImpl.class, "UserApp");
        serveAndBindRpcService(SubmissionCreateService.NAME, SubmissionCreateServiceImpl.class, "UserApp");
        serveAndBindRpcService(ImportSubmissionService.NAME, ImportSubmissionServiceImpl.class, "UserApp");
        // editor app services
        serveAndBindRpcService(AdfService.NAME, AdfServiceImpl.class, "EditorApp");

        bind(HibernateSessionFactoryProvider.class).asEagerSingleton();
        bind(HibernateSessionFactory.class).toProvider(HibernateSessionFactoryProvider.class);

        bind(DataFilesPeriodicProcess.class).asEagerSingleton();

        bind(SubmissionListServiceImpl.class).in(SINGLETON);

        bind(UserDao.class).to(UserDaoImpl.class).in(SINGLETON);
        bind(UserRoleDao.class).to(UserRoleDaoImpl.class).in(SINGLETON);
        bind(SubmissionDao.class).to(SubmissionDaoImpl.class).in(SINGLETON);
        bind(DataFileDao.class).to(DataFileDaoImpl.class).in(SINGLETON);
        bind(SubmissionFeedbackDao.class).to(SubmissionFeedbackDaoImpl.class).in(SINGLETON);

        bind(AccountManager.class).in(SINGLETON);
        bind(SubmissionManager.class).in(SINGLETON);
        bind(DataFileManager.class).in(SINGLETON);
        bind(FtpManager.class).in(SINGLETON);
        bind(DataFileConnector.class).in(SINGLETON);
        bind(EmailSender.class).in(SINGLETON);

        bind(SubsTracking.class).in(SINGLETON);
        bind(AEConnection.class).in(SINGLETON);
        bind(SubsTrackingWatchdog.class).asEagerSingleton();

        bind(AccountService.class).to(AccountServiceImpl.class).in(SINGLETON);
        bind(AllRpcServicePaths.class).toInstance(allRpc);

        bind(AnnotareProperties.class).asEagerSingleton();
        bind(DataFileStoreProperties.class).to(AnnotareProperties.class);
        bind(ArrayExpressProperties.class).to(AnnotareProperties.class);
        bind(SubsTrackingProperties.class).to(AnnotareProperties.class);
        bind(AEConnectionProperties.class).to(AnnotareProperties.class);

        bind(DatabaseDataSource.class).asEagerSingleton();

        bind(EfoSearch.class).to(EfoSearchImpl.class).asEagerSingleton();
        bind(AnnotareEfoService.class).in(SINGLETON);

        bind(SubmissionMigrator.class).asEagerSingleton();

        bind(ArrayExpressArrayDesignList.class).asEagerSingleton();

        TransactionalMethodInterceptor txMethodInterceptor = new TransactionalMethodInterceptor();
        requestInjection(txMethodInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), txMethodInterceptor);

        overrideMageTabCheck();
    }

    @Singleton
    @Provides
    public ArrayExpressExperimentTypeList getArrayExpressExperimentTypeList() {
        return ArrayExpressExperimentTypeList.create();
    }

    @Singleton
    @Provides
    public ProtocolTypes getProtocolTypes() {
        return ProtocolTypes.create();
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
            for (String path : paths) {
                if (uri.endsWith(path)) {
                    return true;
                }
            }
            return false;
        }

        void awareOf(String path) {
            paths.add(path);
        }
    }
}
