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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.db.model.enums.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.CurrentUserAccountServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.utils.ServerWatchdog;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.CookieDialog;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.Accession;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.UserDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.gin.EditorGinjector;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.mvp.EditorPlaceFactory;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.mvp.EditorPlaceHistoryMapper;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.*;

import java.util.Date;

import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionType.EXPERIMENT;
import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class EditorApp implements EntryPoint {

    private final EditorGinjector injector = GWT.create(EditorGinjector.class);
    private final static String SUBMISSION_READONLY_COOKIE = "Submission_ReadOnly_Shown";

    public void onModuleLoad() {
        loadModule(RootLayoutPanel.get());
    }

    private void loadModule(final HasWidgets root) {
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(Throwable e) {
                NotificationPopupPanel.failure("Uncaught exception", e);
            }
        });

        SubmissionServiceAsync submissionService = injector.getSubmissionService();
        final int subId = getSubmissionId();
        submissionService.getSubmissionDetails(subId,
                AsyncCallbackWrapper.callbackWrap(
                        new ReportingAsyncCallback<SubmissionDetails>(FailureMessage.UNABLE_TO_LOAD_SUBMISSION) {
                            @Override
                            public void onSuccess(SubmissionDetails details) {
                                renameBrowserTab(details);
                                init(root, details);
                            }
                        }
                )
        );
    }

    private void renameBrowserTab(SubmissionDetails details) {
        Accession accession = details.getAccession();
        String tabName = accession.getText();
        if (!accession.hasValue()) {
            String title = details.getTitle();
            if (title != null && title.trim().length() > 0) {
                tabName = title;
            }
        }
        Window.setTitle(tabName);
    }

    private void init(HasWidgets root, SubmissionDetails details) {
        EventBus eventBus = injector.getEventBus();

        SubmissionType type = details.getType();
        Widget layout = (type == EXPERIMENT && details.isEmpty()) ?
                initStartLayout(eventBus) :
                initMainLayout(type, eventBus);

        EditorPlaceFactory factory = injector.getPlaceFactory();
        Place defaultPlace =
                (type == EXPERIMENT) ?
                        factory.getExpInfoPlace() : factory.getAdHeaderPlace();

        EditorPlaceHistoryMapper historyMapper = GWT.create(EditorPlaceHistoryMapper.class);
        historyMapper.setFactory(factory);

        PlaceController placeController = injector.getPlaceController();
        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, defaultPlace);

        root.add(layout);

        historyHandler.handleCurrentHistory();

        showSubmissionReadOnlyMessage(details);

        ServerWatchdog.start();
    }

    private void showSubmissionReadOnlyMessage(SubmissionDetails details) {
        if (!"YEZ".equalsIgnoreCase(Cookies.getCookie(SUBMISSION_READONLY_COOKIE))) {
            final SubmissionStatus submissionStatus = details.getStatus();
            CurrentUserAccountServiceAsync userService = injector.getCurrentUserAccountService();
            userService.me(AsyncCallbackWrapper.callbackWrap(
                    new ReportingAsyncCallback<UserDto>(FailureMessage.UNABLE_TO_LOAD_USER_INFORMATION) {
                        @Override
                        public void onSuccess(UserDto result) {
                            if (submissionStatus != SubmissionStatus.IN_PROGRESS && !result.isCurator()) {
                                Date cookieExpiryDate = new Date();
                                CalendarUtil.addMonthsToDate(cookieExpiryDate,3);
                                final DialogBox dialogBox = new CookieDialog(
                                        "Submission cannot be modified",
                                        "<p>This submission can no longer be modified. Please do not make any changes on these forms, as they will be lost. </p><p>To change release date and/or publication details, please use <a target=\"_blank\" href=\"https://www.ebi.ac.uk/fg/acext/\">https://www.ebi.ac.uk/fg/acext/</a>. Login details were emailed to you when your experiment was loaded into ArrayExpress.</p><p>If you'd like to modify other aspects of your submission, e.g. add/remove samples, please click the \"Contact Us\" button above and tell us what modifications are required. A curator will respond as soon as possible.</p>",
                                        SUBMISSION_READONLY_COOKIE, cookieExpiryDate
                                );
                                dialogBox.show();
                            }
                        }
                    }
            ));
        }
    }

    private Widget initStartLayout(EventBus eventBus) {
        EditorStartLayout layout = new EditorStartLayout();

        ActivityMapper topBarActivityMapper = injector.getTopBarActivityMapper();
        ActivityManager topBarActivityManager = new ActivityManager(topBarActivityMapper, eventBus);
        topBarActivityManager.setDisplay(layout.getTopBarDisplay());

        ActivityMapper startActivityMapper = injector.getStartActivityMapper();
        ActivityManager startActivityManager = new ActivityManager(startActivityMapper, eventBus);
        startActivityManager.setDisplay(layout.getDisplay());

        return layout;
    }

    private Widget initMainLayout(SubmissionType type, EventBus eventBus) {
        EditorLayout layout = (type == EXPERIMENT) ? new ExperimentLayout(eventBus) : new ArrayDesignLayout();

        ActivityMapper topBarActivityMapper = injector.getTopBarActivityMapper();
        ActivityManager topBarActivityManager = new ActivityManager(topBarActivityMapper, eventBus);
        topBarActivityManager.setDisplay(layout.getTopBarDisplay());

        ActivityMapper titleBarActivityMapper = injector.getTitleBarActivityMapper();
        ActivityManager titleBarActivityManager = new ActivityManager(titleBarActivityMapper, eventBus);
        titleBarActivityManager.setDisplay(layout.getTitleBarDisplay());

        ActivityMapper tabBarActivityMapper = injector.getTabBarActivityMapper();
        ActivityManager tabBarActivityManager = new ActivityManager(tabBarActivityMapper, eventBus);
        tabBarActivityManager.setDisplay(layout.getTabBarDisplay());

        ActivityMapper leftMenuActivityMapper = injector.getLeftMenuActivityMapper();
        ActivityManager leftMenuActivityManager = new ActivityManager(leftMenuActivityMapper, eventBus);
        leftMenuActivityManager.setDisplay(layout.getLeftMenuDisplay());

        ActivityMapper contentActivityMapper = injector.getContentActivityMapper();
        ActivityManager contentActivityManager = new ActivityManager(contentActivityMapper, eventBus);
        contentActivityManager.setDisplay(layout.getContentDisplay());

        if (layout.getLogBarDisplay() != null) {
            ActivityMapper logBarActivityMapper = injector.getLogBarActivityMapper();
            ActivityManager logBarActivityManager = new ActivityManager(logBarActivityMapper, eventBus);
            logBarActivityManager.setDisplay(layout.getLogBarDisplay());
        }

        return layout.asWidget();
    }
}
