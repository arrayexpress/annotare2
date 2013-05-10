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
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DataUpdateEventHandler;

import java.util.List;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class ExperimentContacts {

    private final SubmissionServiceAsync submissionService;
    private final UpdateQueue updateQueue;

    private IdentityMap<ContactDto> map = new IdentityMap<ContactDto>() {
        @Override
        protected ContactDto create(int tmpId) {
            return new ContactDto(tmpId);
        }
    };

    public ExperimentContacts(SubmissionServiceAsync submissionService,
                              UpdateQueue updateQueue) {
        this.submissionService = submissionService;
        this.updateQueue = updateQueue;
        this.updateQueue.addDataUpdateEventHandler(new DataUpdateEventHandler() {
            @Override
            public void onDataUpdate(DataUpdateEvent event) {
                applyUpdates(event.getUpdates());
            }
        });
    }

    public void getContactsAsync(final AsyncCallback<List<ContactDto>> callback) {
        if (map.isInitialized()) {
            callback.onSuccess(map.values());
            return;
        }
        submissionService.getContacts(getSubmissionId(), new AsyncCallbackWrapper<List<ContactDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(List<ContactDto> result) {
                map.init(result);
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void update(List<ContactDto> toBeUpdated) {
        for (ContactDto contact : toBeUpdated) {
            update(contact);
        }
    }

    private void update(ContactDto toBeUpdated) {
        ContactDto contact = map.find(toBeUpdated);
        if (!contact.isTheSameAs(toBeUpdated)) {
            addUpdateCommand(new UpdateContactCommand(contact.updatedCopy(toBeUpdated)));
        }
    }

    public ContactDto create() {
        ContactDto created = map.create();
        addUpdateCommand(new CreateContactCommand(created));
        return created;
    }

    public void remove(List<ContactDto> contacts) {
        for (ContactDto contact : contacts) {
            ContactDto toBeRemoved = map.find(contact);
            addUpdateCommand(new RemoveContactCommand(toBeRemoved));
        }
    }

    private void addUpdateCommand(UpdateCommand command) {
        updateQueue.add(command);
    }

    private void applyUpdates(UpdateResult result) {
        for (ContactDto created : result.getCreatedContacts()) {
            map.update(created);
        }

        for (ContactDto updated : result.getUpdatedContacts()) {
            map.update(updated);
        }

        for (ContactDto removed : result.getRemovedContacts()) {
            map.remove(removed);
        }
    }
}
