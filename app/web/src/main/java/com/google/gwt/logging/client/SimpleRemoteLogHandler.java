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

package com.google.gwt.logging.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.shared.RemoteLoggingService;
import com.google.gwt.logging.shared.RemoteLoggingServiceAsync;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class SimpleRemoteLogHandler extends RemoteLogHandlerBase {
    class DefaultCallback implements AsyncCallback<String> {
        public void onFailure(Throwable caught) {
            wireLogger.log(Level.SEVERE, "Remote logging failed: ", caught);
        }
        public void onSuccess(String result) {
            if (result != null) {
                wireLogger.severe("Remote logging failed: " + result);
            } else {
                wireLogger.finest("Remote logging message acknowledged");
            }
        }
    }

    private AsyncCallback<String> callback;
    private RemoteLoggingServiceAsync service;

    public SimpleRemoteLogHandler() {
        super(new ArrayList<String>(){{add("Gwt client Uploader");}});

        service = GWT.create(RemoteLoggingService.class);
        this.callback = new DefaultCallback();
    }

    @Override
    public void publish(LogRecord record) {
        if (isLoggable(record)) {
            service.logOnServer(record, callback);
        }
    }
}
