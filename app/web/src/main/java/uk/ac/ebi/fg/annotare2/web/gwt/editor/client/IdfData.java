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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.magetab.rowbased.Investigation;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import uk.ac.ebi.fg.annotare2.magetab.table.operation.Operation;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.IdfServiceAsync;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
@Deprecated
public class IdfData {

    private final IdfServiceAsync idfService;

    private InvestigationData data;

    private final ChangeManager changeManager;

    @Inject
    public IdfData(IdfServiceAsync idfServiceAsync) {
        idfService = idfServiceAsync;
        changeManager = new ChangeManager(new ChangeManager.OperationTransport() {
            @Override
            public void send(Operation op, final AsyncCallback<Void> callback) {
                idfService.updateInvestigation(getSubmissionId(), op, new AsyncCallbackWrapper<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Void result) {
                        callback.onSuccess(result);
                    }
                }.wrap());
            }
        });
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
        data = new InvestigationData(table);
        changeManager.registry(table);
        return data;
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
    }
}
