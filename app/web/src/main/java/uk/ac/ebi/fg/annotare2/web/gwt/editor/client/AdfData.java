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
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AdfServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * TODO: merge it with ArrayDesignDataProxy
 *
 * @author Olga Melnichuk
 */
public class AdfData {

    private final AdfServiceAsync adfService;

    private Table table;

    @Inject
    public AdfData(AdfServiceAsync adfServiceAsync) {
        adfService = adfServiceAsync;
    }

    public void getTable(AsyncCallback<Table> callback) {
        if (table != null) {
            callback.onSuccess(table);
            return;
        }
        loadBody(callback);
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
}
