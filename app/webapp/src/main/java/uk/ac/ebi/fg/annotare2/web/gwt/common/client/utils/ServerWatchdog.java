/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package uk.ac.ebi.fg.annotare2.web.gwt.common.client.utils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.ui.RootPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;

public class ServerWatchdog {

    private final static String SERVER_DOWN = "We are sorry that the Annotare server is temporarily unavailable. " +
            "Please wait until this message disappears and then resume editing your submission. " +
            "If the problem persists, please contact us at <a href=\"mailto:annotare@ebi.ac.uk\">annotare@ebi.ac.uk</a>. " +
            "Thank you for your patience.<br><br>";

    private final static String SERVER_UPGRADED = "To improve your submission experience, Annotare software has just been updated. " +
            "Please <a href=\"javascript:window.location.reload()\">click here</a> to reload this page to continue editing your submission.<br><br>" +
            "Should you experience any problems after this update, please contact us at <a href=\"mailto:annotare@ebi.ac.uk\">annotare@ebi.ac.uk</a>. " +
            "Thank you.";

    private static boolean isNotificaionVisible;
    private static boolean isPaused;

    public static void start() {
        isPaused = false;
        isNotificaionVisible = false;
        Scheduler.get().scheduleFixedPeriod(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                if (!isPaused) {
                    try {
                        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, Urls.getContextUrl() + "status");
                        builder.setTimeoutMillis(30000);
                        builder.sendRequest(null, new RequestCallback() {
                            @Override
                            public void onResponseReceived(Request request, Response response) {
                                if (200 == response.getStatusCode()) {
                                    String[] status = response.getText().split("\\n");
                                    if (null != status && 2 == status.length && status[0].equals("OK")) {
                                        String revision = RootPanel.getBodyElement().getAttribute("revision");
                                        if (null != revision && revision.equals(status[1])) {
                                            if (isNotificaionVisible) {
                                                    NotificationPopupPanel.cancel();
                                                    isNotificaionVisible = false;
                                            }
                                        } else {
                                            NotificationPopupPanel.warning(SERVER_UPGRADED, false, true);
                                            isNotificaionVisible = true;
                                        }
                                    } else {
                                        NotificationPopupPanel.error(SERVER_DOWN + "<i>Incorrect status response: " + response.getText() + "</i>", false, true);
                                        isNotificaionVisible = true;
                                    }
                                } else {
                                    NotificationPopupPanel.error(SERVER_DOWN + "<i>HTTP status code: " + response.getStatusCode() + "</i>", false, true);
                                    isNotificaionVisible = true;
                                }
                            }

                            @Override
                            public void onError(Request request, Throwable caught) {
                                NotificationPopupPanel.error(SERVER_DOWN + "<i>Exception caught: " + caught.getMessage() + "</i>", false, true);
                                isNotificaionVisible = true;
                            }
                        });

                    } catch (RequestException caught) {
                        NotificationPopupPanel.error(SERVER_DOWN + "<i>Exception caught: " + caught.getMessage() + "</i>", false, true);
                        isNotificaionVisible = true;
                    }
                }
                return true;
            }
        }, 60000);
    }

    public static void pause() {
        isPaused = true;
    }

    public static void resume() {
        isPaused = false;
    }
}
