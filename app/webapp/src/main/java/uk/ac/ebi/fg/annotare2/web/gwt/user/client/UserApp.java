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

package uk.ac.ebi.fg.annotare2.web.gwt.user.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.utils.ServerWatchdog;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.CookieDialog;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.gin.UserAppGinjector;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.mvp.UserAppPlaceFactory;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.mvp.UserAppPlaceHistoryMapper;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.SubmissionListPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.widget.AppLayout;

import java.util.Date;
import java.util.logging.Logger;

/**
 * @author Olga Melnichuk
 */
public class UserApp implements EntryPoint {

    private final UserAppGinjector injector = GWT.create(UserAppGinjector.class);

    private final static Logger logger = Logger.getLogger("gwt.client.annotare.UserApp");

    private AppLayout appWidget = new AppLayout();

    public void onModuleLoad() {
        loadModule(RootLayoutPanel.get());
    }

    private void loadModule(HasWidgets root) {
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(Throwable e) {
                NotificationPopupPanel.failure("Uncaught exception", e);
            }
        });

        EventBus eventBus = injector.getEventBus();
        PlaceController placeController = injector.getPlaceController();

        ActivityMapper headerActivityMapper = injector.getHeaderActivityMapper();
        ActivityManager headerActivityManager = new ActivityManager(headerActivityMapper, eventBus);
        headerActivityManager.setDisplay(appWidget.getTopPanel());

        ActivityMapper leftMenuActivityMapper = injector.getLeftMenuActivityMapper();
        ActivityManager leftMenuActivityManager = new ActivityManager(leftMenuActivityMapper, eventBus);
        leftMenuActivityManager.setDisplay(appWidget.getWestPanel());

        ActivityMapper contentActivityMapper = injector.getContentActivityMapper();
        ActivityManager contentActivityManager = new ActivityManager(contentActivityMapper, eventBus);
        contentActivityManager.setDisplay(appWidget.getCenterPanel());

        UserAppPlaceFactory factory = injector.getPlaceFactory();
        SubmissionListPlace defaultPlace = factory.getSubmissionListPlace();

        UserAppPlaceHistoryMapper historyMapper = GWT.create(UserAppPlaceHistoryMapper.class);
        historyMapper.setFactory(factory);

        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, defaultPlace);

        root.add(appWidget);

        historyHandler.handleCurrentHistory();

        ServerWatchdog.start();
        showNotice();
    }

    private final static String NOTICE_COOKIE = "Notice_100817_Shown"; // change name according to the cookie display date ddmmyy
    private void showNotice() {
        Date stopNoticeDate = new Date();
        stopNoticeDate.setTime(1503356400000L); // stop showing on 22 Aug (http://www.epochconverter.com/)
        if (!"YEZ".equalsIgnoreCase(Cookies.getCookie(NOTICE_COOKIE)) && (new Date().before(stopNoticeDate))) {
            Date expiryDate = new Date();
            expiryDate.setTime(8000000000000L);
            CookieDialog dialogBox = new CookieDialog(
                    "Annotare Downtime!",
                    "<p>Dear Submitter,</p>" +
                            "<p>Due to essential systems maintenance, Annotare will not be available from 18th to 21st August. We apologise for any inconvenience.</p>" +
                            "<p>Regards,<br/>Annotare Team</p>",
                    NOTICE_COOKIE, expiryDate
            );
            dialogBox.show();
        }
    }
}
