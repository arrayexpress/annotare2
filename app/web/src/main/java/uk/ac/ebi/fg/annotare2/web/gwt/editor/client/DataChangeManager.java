/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.AutoSaveEvent;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class DataChangeManager {

    private static final int REPEAT_INTERVAL = 2000;

    private final EventBus eventBus;
    private final Map<String, SaveAction> actions = new HashMap<String, SaveAction>();
    private Queue<String> queue = new LinkedList<String>();
    private boolean isActive;

    @Inject
    public DataChangeManager(EventBus eventBus) {
        this.eventBus = eventBus;

        new Timer() {
            @Override
            public void run() {
                execute();
            }
        }.scheduleRepeating(REPEAT_INTERVAL);
    }

    public void add(String key, SaveAction action) {
        if (queue.contains(key)) {
            return;
        }
        actions.put(key, action);
        queue.add(key);
    }

    public void execute() {
        if (isActive || queue.isEmpty()) {
            return;
        }

        notifyStart();
        next();
    }

    private void next() {
        if (queue.isEmpty()) {
            notifyStop(null);
        }
        String key = queue.peek();
        SaveAction action = actions.get(key);
        action.onSave(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                notifyStop(caught);
            }

            @Override
            public void onSuccess(Void result) {
                queue.poll();
                next();
            }
        });
    }

    private void notifyStart() {
        eventBus.fireEvent(AutoSaveEvent.autoSaveStarted());
    }

    private void notifyStop(Throwable caught) {
        isActive = false;
        eventBus.fireEvent(AutoSaveEvent.autoSaveStopped(caught));
        Window.alert(caught.getMessage());
    }

    public interface SaveAction {
        void onSave(AsyncCallback<Void> callback);
    }
}


