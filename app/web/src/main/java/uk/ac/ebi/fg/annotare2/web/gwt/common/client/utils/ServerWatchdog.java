/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

    private final static String SERVER_UPGRADED = "To improve your submission experience, the Annotare software has just been updated. " +
            "Please refresh this page in your browser to continue editing your the submission.<br>" +
            "Should you experience any problems after this update, please contact us at <a href=\"mailto:annotare@ebi.ac.uk\">annotare@ebi.ac.uk</a>. " +
            "Thank you.<br><br>";

    private static boolean isOnline;

    public static void start() {
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
                                        isOnline = true;
                                        NotificationPopupPanel.cancel();
                                    } else {
                                        isOnline = false;
                                        NotificationPopupPanel.warning(SERVER_UPGRADED, false, true);
                                    }
                                } else {
                                    isOnline = false;
                                    NotificationPopupPanel.error(SERVER_DOWN + "<i>Incorrect status response: " + response.getText() + "</i>", false, true);

                                }
                            } else {
                                isOnline = false;
                                NotificationPopupPanel.error(SERVER_DOWN + "<i>HTTP status code: " + response.getStatusCode() + "</i>", false, true);
                            }
                        }

                        @Override
                        public void onError(Request request, Throwable caught) {
                            isOnline = false;
                            NotificationPopupPanel.error(SERVER_DOWN + "<i>Exception caught: " + caught.getMessage() + "</i>", false, true);
                        }
                    });
                } catch (RequestException caught) {
                    isOnline = false;
                    NotificationPopupPanel.error(SERVER_DOWN + "<i>Exception caught: " + caught.getMessage() + "</i>", false, true);
                }
                return true;
            }
        }, 5000);
    }
}
