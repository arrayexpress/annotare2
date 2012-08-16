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
import uk.ac.ebi.fg.annotare2.magetab.base.Table;
import uk.ac.ebi.fg.annotare2.magetab.idf.Investigation;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.IdfServiceAsync;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class InvestigationData {

    private final IdfServiceAsync idfService;

    private Table table;

    private Investigation investigation;

    @Inject
    public InvestigationData(IdfServiceAsync idfService) {
        this.idfService = idfService;
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
                table = result;
                investigation = new Investigation(table);
                callback.onSuccess(investigation);
            }
        }.wrap());
    }
}
