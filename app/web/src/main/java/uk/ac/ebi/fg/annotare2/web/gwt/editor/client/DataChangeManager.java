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
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.AutoSaveEvent;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Olga Melnichuk
 */
public class DataChangeManager {

    private static final int REPEAT_INTERVAL = 2000;

    private final EventBus eventBus;
    private final Map<String, SaveDataHandler> handlers = new HashMap<String, SaveDataHandler>();
    private Set<String> queue = new LinkedHashSet<String>();

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
        handlers.put(key, handler);
        queue.add(key);
    }

    public void execute() {
        Set<String> queueCopy = new LinkedHashSet<String>(queue);
        queue = new LinkedHashSet<String>();

        for (String key : queueCopy) {
            SaveDataHandler handler = handlers.get(key);
            handler.onSave(new Callback() {
                @Override
                public void onStop(Throwable caught) {
                    eventBus.fireEvent(AutoSaveEvent.autoSaveStopped(caught));
                }

                @Override
                public void onStart() {
                    eventBus.fireEvent(AutoSaveEvent.autoSaveStarted());
                }
            });
        }
    }

    public interface SaveDataHandler {
        void onSave(Callback callback);
    }

    public interface Callback {
        void onStart();

        void onStop(Throwable caught);
    }
}


