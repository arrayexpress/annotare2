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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * @author Olga Melnichuk
 */
public class DataChangeManager {

    private static final int REPEAT_INTERVAL = 2000;
    private static final int MAX_QUEUE_SIZE = 100;

    private final EventBus eventBus;
    private final Map<String, SaveDataHandler> handlers = new HashMap<String, SaveDataHandler>();
    private final Queue<String> queue = new LinkedList<String>();

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


    public void add(String key, SaveDataHandler handler) {
        if (queue.contains(key)) {
            return;
        }
        if (queue.size() > MAX_QUEUE_SIZE) {
            Window.alert("can't save changes; the update queue is full");
            return;
        }
        handlers.put(key, handler);
        queue.add(key);
    }

    public void execute() {
        if (queue.isEmpty()) {
            return;
        }

        String key = queue.peek();
        SaveDataHandler handler = handlers.get(key);
        startToSave();
        handler.onSave(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                stopToSave(caught);
            }

            @Override
            public void onSuccess(Void result) {
                stopToSave(null);
            }
        });
    }

    private void startToSave() {
        eventBus.fireEvent(AutoSaveEvent.autoSaveStarted());
    }

    private void stopToSave(Throwable caught) {
        queue.poll();
        eventBus.fireEvent(AutoSaveEvent.autoSaveStopped(caught));
    }

    public interface SaveDataHandler {
        void onSave(AsyncCallback<Void> callback);
    }
}


