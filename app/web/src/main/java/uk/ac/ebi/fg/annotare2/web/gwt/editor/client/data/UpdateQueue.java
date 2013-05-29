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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.UpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.UpdateResult;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataUpdateEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.HasDataUpdateEventHandlers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.AutoSaveEvent.autoSaveStarted;
import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.AutoSaveEvent.autoSaveStopped;

/**
 * @author Olga Melnichuk
 */
public class UpdateQueue implements HasDataUpdateEventHandlers {

    private static final int REPEAT_INTERVAL = 2000;

    private final EventBus eventBus;
    private final Transport transport;
    private final HandlerManager handlerManager;

    private Queue<UpdateCommand> queue = new LinkedList<UpdateCommand>();
    private boolean isActive;

    public UpdateQueue(EventBus eventBus, Transport transport) {
        this.eventBus = eventBus;
        this.transport = transport;

        handlerManager = new HandlerManager(this);

        new Timer() {
            @Override
            public void run() {
                execute();
            }
        }.scheduleRepeating(REPEAT_INTERVAL);
    }

    @Override
    public HandlerRegistration addDataUpdateEventHandler(DataUpdateEventHandler handler) {
         return handlerManager.addHandler(DataUpdateEvent.getType(), handler);
    }

    public void add(UpdateCommand command) {
        queue.offer(command);
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
            return;
        }
        List<UpdateCommand> commands = new ArrayList<UpdateCommand>(queue);
        final int queueSize = commands.size();
        transport.sendUpdates(commands, new AsyncCallback<UpdateResult>() {
            @Override
            public void onFailure(Throwable caught) {
                notifyStop(caught);
            }

            @Override
            public void onSuccess(UpdateResult result) {
                for (int i = 0; i < queueSize; i++) {
                    queue.poll();
                }
                fireDataUpdateEvent(result);
                next();
            }
        });
    }

    private void fireDataUpdateEvent(UpdateResult result) {
        DataUpdateEvent.fire(handlerManager, result);
    }

    private void notifyStart() {
        isActive = true;
        eventBus.fireEvent(autoSaveStarted());
    }

    private void notifyStop(Throwable caught) {
        isActive = false;
        eventBus.fireEvent(autoSaveStopped(caught));
        if (caught != null) {
            Window.alert(caught.getMessage());
        }
    }

    public interface Transport {
        void sendUpdates(List<UpdateCommand> commands, AsyncCallback<UpdateResult> callback);
    }
}


