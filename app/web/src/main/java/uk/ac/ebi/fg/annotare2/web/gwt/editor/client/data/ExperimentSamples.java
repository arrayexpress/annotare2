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
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRowsAndColumns;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataUpdateEventHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class ExperimentSamples {

    private final SubmissionServiceAsync submissionService;
    private final UpdateQueue updateQueue;

    private List<Integer> columnOrder = new ArrayList<Integer>();

    private IdentityMap<SampleColumn> sampleColumns = new IdentityMap<SampleColumn>() {
        @Override
        protected SampleColumn create(int tmpId) {
            return new SampleColumn(tmpId, "New Attribute");
        }
    };

    private IdentityMap<SampleRow> sampleRows = new IdentityMap<SampleRow>() {
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
        if (sampleRows.isInitialized() && sampleColumns.isInitialized()) {
            callback.onSuccess(
                    new SampleRowsAndColumns(
                            new ArrayList<SampleRow>(sampleRows.values()),
                            getColumns()));
            return;
        }
        submissionService.getSamples(getSubmissionId(), new AsyncCallbackWrapper<SampleRowsAndColumns>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(SampleRowsAndColumns result) {
                sampleRows.init(result.getSampleRows());
                sampleColumns.init(result.getSampleColumns());
                initColumnOrder();
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public List<SampleColumn> updateSampleColumns(List<SampleColumn> columns) {
        List<SampleColumn> columnsToUpdate = new ArrayList<SampleColumn>();
        for (SampleColumn column : columns) {
            if (column.getId() == 0) {
                int tmpId = sampleColumns.create().getId();
                columnsToUpdate.add(new SampleColumn(tmpId, tmpId, column));
            } else {
                columnsToUpdate.add(column);
            }
        }
        updateQueue.add(new UpdateSampleColumnsCommand(columnsToUpdate));
        return columnsToUpdate;
    }

    public void updateSampleRow(SampleRow row) {
        SampleRow existedRow = sampleRows.find(row);
        updateQueue.add(new UpdateSampleRowCommand(existedRow.updatedCopy(fixRowValues(row))));
    }

    public SampleRow createSampleRow() {
        SampleRow row = sampleRows.create();
        updateQueue.add(new CreateSampleCommand(row));
        return row;
    }

    public void removeSampleRows(List<SampleRow> rows) {
        for (SampleRow row : rows) {
            SampleRow toBeRemoved = sampleRows.find(row);
            updateQueue.add(new RemoveSampleCommand(toBeRemoved));
        }
    }

    private SampleRow fixRowValues(SampleRow row) {
        Map<Integer, String> values = row.getValues();
        Map<Integer, String> newValues = new HashMap<Integer, String>();
        for (int key : values.keySet()) {
            SampleColumn column = sampleColumns.find(key);
            newValues.put(column.getId(), values.get(key));
        }
        return new SampleRow(row.getId(), row.getName(), newValues);
    }

    private void setColumnOrder(List<Integer> order) {
        columnOrder = new ArrayList<Integer>(order);
    }

    private void initColumnOrder() {
        columnOrder = new ArrayList<Integer>();
        for(SampleColumn column : sampleColumns.values()) {
            columnOrder.add(column.getId());
        }
    }

    private List<SampleColumn> getColumns() {
        List<SampleColumn> columns = new ArrayList<SampleColumn>();
        for (Integer id : columnOrder) {
            columns.add(sampleColumns.find(id));
        }
        return columns;
    }

    private void applyUpdates(UpdateResult updates) {
        for (SampleColumn column : updates.getCreatedSampleColumns()) {
            sampleColumns.update(column);
        }
        for (SampleColumn column : updates.getUpdatedSampleColumns()) {
            sampleColumns.update(column);
        }
        for (Integer id : updates.getRemovedSampleColumnIds()) {
            sampleColumns.remove(id);
        }
        setColumnOrder(updates.getSampleColumnOrder());

        for (SampleRow row : updates.getCreatedSampleRows()) {
            sampleRows.update(row);
        }
        for (SampleRow row : updates.getUpdatedSampleRows()) {
            sampleRows.update(row);
        }
        for (SampleRow row : updates.getRemovedSampleRows()) {
            sampleRows.remove(row);
        }
    }
}
