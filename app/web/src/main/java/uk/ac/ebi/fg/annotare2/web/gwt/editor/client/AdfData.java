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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.magetab.rowbased.AdfHeader;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import uk.ac.ebi.fg.annotare2.magetab.table.operation.Operation;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AdfServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class AdfData {

    private final AdfServiceAsync adfService;

    private Table table;

    private AdfHeader header;

    private final ChangeManager changeManager;

    @Inject
    public AdfData(AdfServiceAsync adfServiceAsync) {
        adfService = adfServiceAsync;
        changeManager = new ChangeManager(new ChangeManager.OperationTransport() {
            @Override
            public void send(Operation op, final AsyncCallback<Void> callback) {
                adfService.updateHeaderData(getSubmissionId(), op, new AsyncCallbackWrapper<Void>() {
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

    public void getTable(AsyncCallback<Table> callback) {
        if (table != null) {
            callback.onSuccess(table);
            return;
        }
        loadBody(callback);
    }

    public void getHeader(AsyncCallback<AdfHeader> callback) {
        if (header != null) {
            callback.onSuccess(header);
            return;
        }
        loadHeader(callback);
    }

    private void loadHeader(final AsyncCallback<AdfHeader> callback) {
        adfService.loadHeaderData(getSubmissionId(), new AsyncCallbackWrapper<Table>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Table result) {
                callback.onSuccess(setHeaderTable(result));
            }
        }.wrap());
    }

    private void loadBody(final AsyncCallback<Table> callback) {
        adfService.loadBodyData(getSubmissionId(), new AsyncCallbackWrapper<Table>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Table result) {
                callback.onSuccess(setBodyTable(result));
            }
        }.wrap());
    }

    private Table setBodyTable(Table table) {
        this.table = table;
        return table;
    }

    private AdfHeader setHeaderTable(Table table) {
        this.header = new AdfHeader(table);
        changeManager.registry(table);
        return header;
    }

}
