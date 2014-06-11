/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.dataproxy;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Olga Melnichuk
 */
public abstract class Updater {

    private final int millis;
    private int updateRequests;
    private boolean isActive;


    public Updater(int millis) {
        this.millis = millis;
    }

    public abstract void onAsyncUpdate(AsyncCallback<Boolean> callback);

    public void update() {
        updateRequests++;
        start();
    }

    private void start() {
        if (!isActive) {
            isActive = true;
            getUpdates(10);
        }
    }

    private void stop() {
        isActive = false;
    }

    private void getUpdates(int ms) {
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                updateRequests = 0;
                onAsyncUpdate(new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Unable to load updates");
                        stop();
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if (result || updateRequests > 0) {
                            getUpdates(millis);
                        } else {
                            stop();
                        }
                    }
                });
                return false;
            }
        }, ms);
    }
}
