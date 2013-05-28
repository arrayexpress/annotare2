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
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ContactDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRowsAndColumns;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataUpdateEventHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class ExperimentSamples {

    private final SubmissionServiceAsync submissionService;
    private final UpdateQueue updateQueue;

    private List<SampleColumn> columns;

    private IdentityMap<SampleRow> map = new IdentityMap<SampleRow>() {
        @Override
        protected SampleRow create(int tmpId) {
            return new SampleRow(tmpId, "New Sample");
        }
    };

    public ExperimentSamples(SubmissionServiceAsync submissionService, UpdateQueue updateQueue) {
        this.submissionService = submissionService;
        this.updateQueue = updateQueue;
        this.updateQueue.addDataUpdateEventHandler(new DataUpdateEventHandler() {
            @Override
            public void onDataUpdate(DataUpdateEvent event) {
                applyUpdates(event.getUpdates());
            }
        });
    }

    public void getSamplesAsync(final AsyncCallback<SampleRowsAndColumns> callback) {
        if (map.isInitialized() && columns != null) {
            callback.onSuccess(
                    new SampleRowsAndColumns(
                            new ArrayList<SampleRow>(map.values()),
                            new ArrayList<SampleColumn>(columns)));
            return;
        }
        submissionService.getSamples(getSubmissionId(), new AsyncCallbackWrapper<SampleRowsAndColumns>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(SampleRowsAndColumns result) {
                map.init(result.getSampleRows());
                columns = new ArrayList<SampleColumn>(result.getSampleColumns());
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void updateSampleColumns(List<SampleColumn> columns) {
        updateQueue.add(new UpdateSampleColumnsCommand(columns));
    }

    public void updateSampleRow(SampleRow row) {
        SampleRow existedRow = map.find(row);
        updateQueue.add(new UpdateSampleRowCommand(existedRow.updatedCopy(row)));
    }

    public SampleRow createSampleRow() {
        SampleRow row = map.create();
        updateQueue.add(new CreateSampleCommand(row));
        return row;
    }

    public void removeSampleRows(List<SampleRow> rows) {
        for (SampleRow row : rows) {
            SampleRow toBeRemoved = map.find(row);
            updateQueue.add(new RemoveSampleCommand(toBeRemoved));
        }
    }

    private void applyUpdates(UpdateResult updates) {
        columns = new ArrayList<SampleColumn>(updates.getUpdatedSampleColumns());
        for (SampleRow row : updates.getUpdatedSampleRows()) {
            map.update(row);
        }
        for (SampleRow row : updates.getCreatedSampleRows()) {
            map.update(row);
        }
    }
}
