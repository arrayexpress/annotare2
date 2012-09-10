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
import uk.ac.ebi.fg.annotare2.magetab.base.ChangeListener;
import uk.ac.ebi.fg.annotare2.magetab.base.operation.Operation;
import uk.ac.ebi.fg.annotare2.magetab.base.Table;
import uk.ac.ebi.fg.annotare2.magetab.idf.Investigation;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.IdfServiceAsync;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class InvestigationData {

    private final IdfServiceAsync idfService;

    private Table table;

    private Investigation investigation;

    private Queue<Operation> changes = new LinkedList<Operation>();

    private static final int MAX_SIZE = 100;

    @Inject
    public InvestigationData(IdfServiceAsync idfService) {
        this.idfService = idfService;
        new Timer() {
            @Override
            public void run() {
               sendChanges();
            }
        }.scheduleRepeating(2000);
    }

    public void getInvestigation(AsyncCallback<Investigation> callback) {
        if (investigation != null) {
            callback.onSuccess(investigation);
            return;
        }
        load(callback);
    }

    private void load(final AsyncCallback<Investigation> callback) {
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

    private Investigation setTable(Table table) {
        this.table = table;
        this.investigation = new Investigation(table);
        this.table.addChangeListener(new ChangeListener() {
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
        return this.investigation;
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
                Window.alert("Can't save changes on the server");
            }

            @Override
            public void onSuccess(Void result) {
                changes.poll();
            }
        }.wrap());
    }

}
