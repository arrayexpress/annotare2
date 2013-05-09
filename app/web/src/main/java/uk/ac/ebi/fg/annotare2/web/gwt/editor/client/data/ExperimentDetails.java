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
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.UpdateDetailsCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.UpdateResult;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataUpdateEventHandler;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class ExperimentDetails {

    private final SubmissionServiceAsync submissionService;
    private final UpdateQueue updateQueue;

    private DetailsDto details;

    public ExperimentDetails(SubmissionServiceAsync submissionService, UpdateQueue updateQueue) {
        this.submissionService = submissionService;
        this.updateQueue = updateQueue;
        this.updateQueue.addDataUpdateEventHandler(new DataUpdateEventHandler() {
            @Override
            public void onDataUpdate(DataUpdateEvent event) {
                applyUpdates(event.getUpdates());
            }
        });
    }

    public void getDetailsAsync(final AsyncCallback<DetailsDto> callback) {
        if (details != null) {
            callback.onSuccess(details);
            return;
        }
        submissionService.getExperimentDetails(getSubmissionId(), new AsyncCallbackWrapper<DetailsDto>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DetailsDto result) {
                details = result;
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void updateDetails(DetailsDto toBeUpdated) {
        if (toBeUpdated == null || details.isContentEqual(toBeUpdated)) {
            return;
        }
        updateQueue.add(new UpdateDetailsCommand(toBeUpdated));
    }

    private void applyUpdates(UpdateResult updates) {
        DetailsDto dto = updates.getUpdatedDetails();
        if (dto != null) {
            details = dto;
        }
    }
}
