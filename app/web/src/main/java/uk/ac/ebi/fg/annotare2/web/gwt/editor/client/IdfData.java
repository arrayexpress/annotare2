/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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
import uk.ac.ebi.fg.annotare2.magetab.table.ChangeListener;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import uk.ac.ebi.fg.annotare2.magetab.table.operation.Operation;
import uk.ac.ebi.fg.annotare2.magetab.idf.Investigation;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.IdfServiceAsync;

import java.util.LinkedList;
import java.util.Queue;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class IdfData {

    private final IdfServiceAsync idfService;

    private InvestigationData data;

    private Queue<Operation> changes = new LinkedList<Operation>();

    private static final int MAX_SIZE = 100;

    @Inject
    public IdfData(IdfServiceAsync idfService) {
        this.idfService = idfService;
        new Timer() {
            @Override
            public void run() {
               sendChanges();
            }
        }.scheduleRepeating(2000);
    }

    public void getInvestigation(final AsyncCallback<Investigation> callback) {
        if (data != null) {
            callback.onSuccess(data.getInvestigation());
            return;
        }
        load(new AsyncCallback<InvestigationData>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(InvestigationData result) {
                callback.onSuccess(result.getInvestigation());
            }
        });
    }

    public void getTable(final AsyncCallback<Table> callback) {
        if (data != null) {
            callback.onSuccess(data.getTable());
            return;
        }
        load(new AsyncCallback<InvestigationData>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(InvestigationData result) {
                callback.onSuccess(result.getTable());
            }
        });
    }

    private void load(final AsyncCallback<InvestigationData> callback) {
        idfService.loadInvestigation(getSubmissionId(), new AsyncCallbackWrapper<Table>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Table result) {
                callback.onSuccess(setTable(result));
            }
        }.wrap());
    }

    private InvestigationData setTable(Table table) {
        this.data = new InvestigationData(table);
        this.data.addChangeListener(new ChangeListener() {
            @Override
            public void onChange(Operation operation) {
                if (changes.size() < MAX_SIZE) {
                    changes.add(operation);
                } else {
                    //TODO use local storage?
                    Window.alert("Cache of changes exceeded");
                }
            }
        });
        return this.data;
    }

    private void sendChanges() {
        if (changes.isEmpty()) {
            return;
        }

        Operation next = changes.peek();
        idfService.updateInvestigation(getSubmissionId(), next, new AsyncCallbackWrapper<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                //TODO
                Window.alert("Can't save changes on the server: " + caught.getMessage());
            }

            @Override
            public void onSuccess(Void result) {
                changes.poll();
            }
        }.wrap());
    }

    private static class InvestigationData {

        private final Table table;

        private final Investigation investigation;

        public InvestigationData(Table table) {
            this.table = table;
            this.investigation = new Investigation(table);
        }

        public Investigation getInvestigation() {
            return investigation;
        }

        public Table getTable() {
            return table;
        }

        public void addChangeListener(ChangeListener listener) {
            this.table.addChangeListener(listener);
        }
    }

}
