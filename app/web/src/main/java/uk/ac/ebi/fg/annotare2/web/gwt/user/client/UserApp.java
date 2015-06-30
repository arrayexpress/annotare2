/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.user.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.*;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.utils.Urls;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.gin.UserAppGinjector;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.mvp.UserAppPlaceFactory;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.mvp.UserAppPlaceHistoryMapper;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.SubmissionListPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.widget.AppLayout;

import java.util.logging.Level;
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

        //startWatchdog();
        //showNotice();
    }

    private final static String NOTICE_COOKIE = "HolidayNoticeShown";

    private void showNotice() {
        if (!"YEZ".equalsIgnoreCase(Cookies.getCookie(NOTICE_COOKIE))) {
            NotificationPopupPanel.message(
                    "<strong>On 21 May 2015 (Thursday this week), Annotare will be unavailable due to essential maintenance.</strong><br><br>"
                        + "Processing of submitted experiments will also be halted. Please allow extra 2-3 days when preparing your submission.",
                    false);
            Cookies.setCookie(NOTICE_COOKIE, "YEZ");
        }
    }

    private void startWatchdog() {
        Scheduler.get().scheduleFixedPeriod(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                try {
                    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, Urls.getContextUrl() + "status");
                    builder.setTimeoutMillis(2000);
                    builder.sendRequest(null, new RequestCallback() {
                        @Override
                        public void onResponseReceived(Request request, Response response) {
                            if (200 == response.getStatusCode()) {
                                String[] status = response.getText().split("\\n");
                                if (null != status && 2 == status.length && status[0].equals("OK")) {
                                    String revision = RootPanel.getBodyElement().getAttribute("revision");
                                    if (null != revision && revision.equals(status[1])) {
                                        logger.log(Level.INFO, "OK, ping received & recognised");
                                    } else {
                                        logger.log(Level.SEVERE, "Error, revision mismatch, expected " + revision + ", received " + status[1]);
                                    }
                                } else {
                                    logger.log(Level.SEVERE, "Error, unexpected response: " + response.getText());
                                }
                            } else {
                                logger.log(Level.SEVERE, "Error, HTTP code [" + response.getStatusCode() + "]");
                            }
                        }

                        @Override
                        public void onError(Request request, Throwable caught) {
                            logger.log(Level.SEVERE, "Exception caught: ", caught);
                        }
                    });
                } catch (RequestException caught) {
                    logger.log(Level.SEVERE, "Exception caught: ", caught);
                }
            return true;
            }
        }, 5000);
    }
}
