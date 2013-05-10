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
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.PublicationDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataUpdateEventHandler;

import java.util.List;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class ExperimentPublications {

    private final SubmissionServiceAsync submissionService;
    private final UpdateQueue updateQueue;

    private IdentityMap<PublicationDto> map = new IdentityMap<PublicationDto>() {
        @Override
        protected PublicationDto create(int tmpId) {
            return new PublicationDto(tmpId);
        }
    };

    public ExperimentPublications(SubmissionServiceAsync submissionService, UpdateQueue updateQueue) {
        this.submissionService = submissionService;
        this.updateQueue = updateQueue;
        this.updateQueue.addDataUpdateEventHandler(new DataUpdateEventHandler() {
            @Override
            public void onDataUpdate(DataUpdateEvent event) {
                applyUpdates(event.getUpdates());
            }
        });
    }

    public void getPublicationsAsync(final AsyncCallback<List<PublicationDto>> callback) {
        if (map.isInitialized()) {
            callback.onSuccess(map.values());
            return;
        }
        submissionService.getPublications(getSubmissionId(), new AsyncCallbackWrapper<List<PublicationDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(List<PublicationDto> result) {
                map.init(result);
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public PublicationDto create() {
        PublicationDto publication = map.create();
        addUpdateCommand(new CreatePublicationCommand(publication));
        return publication;
    }

    public void update(List<PublicationDto> toBeUpdated) {
        for (PublicationDto publication : toBeUpdated) {
            update(publication);
        }
    }

    public void update(PublicationDto toBeUpdated) {
        PublicationDto publication = map.find(toBeUpdated);
        if (!publication.isTheSameAs(toBeUpdated)) {
            addUpdateCommand(new UpdatePublicationCommand(publication.updatedCopy(toBeUpdated)));
        }
    }

    public void remove(List<PublicationDto> publications) {
        for (PublicationDto publication : publications) {
            PublicationDto toBeRemoved = map.find(publication);
            addUpdateCommand(new RemovePublicationCommand(toBeRemoved));
        }
    }

    private void addUpdateCommand(UpdateCommand command) {
        updateQueue.add(command);
    }

    private void applyUpdates(UpdateResult result) {
        for (PublicationDto created : result.getCreatedPublications()) {
            map.update(created);
        }

        for (PublicationDto updated : result.getUpdatedPublications()) {
            map.update(updated);
        }

        for (PublicationDto removed : result.getRemovedPublications()) {
            map.remove(removed);
        }
    }
}
