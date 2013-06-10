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
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ExperimentUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ExperimentUpdateResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.UpdateExperimentDetailsCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataUpdateEventHandler;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class ExperimentDetails {

    private final SubmissionServiceAsync submissionService;
    private final UpdateQueue<ExperimentUpdateCommand, ExperimentUpdateResult> updateQueue;

    private ExperimentDetailsDto details;

    public ExperimentDetails(SubmissionServiceAsync submissionService,
                             UpdateQueue<ExperimentUpdateCommand, ExperimentUpdateResult> updateQueue) {
        this.submissionService = submissionService;
        this.updateQueue = updateQueue;
        this.updateQueue.addDataUpdateEventHandler(new DataUpdateEventHandler<ExperimentUpdateResult>() {
            @Override
            public void onDataUpdate(DataUpdateEvent<ExperimentUpdateResult> event) {
                applyUpdates(event.getUpdates());
            }
        });
    }

    public void getDetailsAsync(final AsyncCallback<ExperimentDetailsDto> callback) {
        if (details != null) {
            callback.onSuccess(details);
            return;
        }
        submissionService.getExperimentDetails(getSubmissionId(), new AsyncCallbackWrapper<ExperimentDetailsDto>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ExperimentDetailsDto result) {
                details = result;
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void updateDetails(ExperimentDetailsDto toBeUpdated) {
        if (toBeUpdated == null || details.isContentEqual(toBeUpdated)) {
            return;
        }
        updateQueue.add(new UpdateExperimentDetailsCommand(toBeUpdated));
    }

    private void applyUpdates(ExperimentUpdateResult updates) {
        ExperimentDetailsDto dto = updates.getUpdatedDetails();
        if (dto != null) {
            details = dto;
        }
    }
}
