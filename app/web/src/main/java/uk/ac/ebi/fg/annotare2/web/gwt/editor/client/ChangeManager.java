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
import uk.ac.ebi.fg.annotare2.magetab.table.ChangeListener;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import uk.ac.ebi.fg.annotare2.magetab.table.operation.Operation;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Olga Melnichuk
 */
public class ChangeManager {

    private static final int MAX_QUEUE_SIZE = 100;

    private static final int REPEAT_INTERVAL = 2000;

    private final Queue<Operation> queue = new LinkedList<Operation>();

    private ChangeListener changeListener = new ChangeListener() {
        @Override
        public void onChange(Operation operation) {
            if (queue.size() < MAX_QUEUE_SIZE) {
                queue.add(operation);
            } else {
                //TODO use local storage?
                Window.alert("Cache of changes exceeded");
            }
        }
    };

    private final OperationTransport transport;

    public ChangeManager(OperationTransport transport) {
        this.transport = transport;

        new Timer() {
            @Override
            public void run() {
                sendChanges();
            }
        }.scheduleRepeating(REPEAT_INTERVAL);
    }

    public void registry(Table table) {
        table.addChangeListener(changeListener);
    }

    private void sendChanges() {
        if (queue.isEmpty()) {
            return;
        }

        Operation next = queue.peek();
        transport.send(next, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                //TODO
                Window.alert("Can't save changes on the server: " + caught.getMessage());
            }
            @Override
            public void onSuccess(Void result) {
                queue.poll();
            }
        });
    }

    public static interface OperationTransport {
        void send(Operation op, AsyncCallback<Void> callback);
    }
}
