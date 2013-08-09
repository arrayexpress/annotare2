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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataFilesUpdateEvent;

import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class DataFiles {

    private final SubmissionServiceAsync submissionServiceAsync;
    private final EventBus eventBus;
    private List<DataFileRow> fileRows;

    @Inject
    public DataFiles(EventBus eventBus,
                     SubmissionServiceAsync submissionServiceAsync) {
        this.submissionServiceAsync = submissionServiceAsync;
        this.eventBus = eventBus;
    }

    public void getFilesAsync(final AsyncCallback<List<DataFileRow>> callback) {
        if (fileRows != null) {
            callback.onSuccess(new ArrayList<DataFileRow>(fileRows));
            return;
        }
        submissionServiceAsync.loadDataFiles(getSubmissionId(), new AsyncCallbackWrapper<List<DataFileRow>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(List<DataFileRow> result) {
                fileRows = new ArrayList<DataFileRow>(result);
                callback.onSuccess(result);
            }
        }.wrap());
    }

    private void update(List<DataFileRow> newFileRows) {
        if (!fileRows.equals(newFileRows)) {
            eventBus.fireEvent(new DataFilesUpdateEvent());
        }
    }
}
