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
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ContactDto;
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
public class ExperimentContacts {

    private final SubmissionServiceAsync submissionService;
    private final UpdateQueue updateQueue;

    private int nextId = 0;

    // real_or_temporary_id -> contact
    private Map<Integer, ContactDto> map;

    // real_id -> temporary_id
    private Map<Integer, Integer> idMap = new HashMap<Integer, Integer>();

    private List<Integer> order = new ArrayList<Integer>();

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
        if (map != null) {
            callback.onSuccess(getContacts());
            return;
        }
        submissionService.getContacts(getSubmissionId(), new AsyncCallbackWrapper<List<ContactDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(List<ContactDto> result) {
                map = new HashMap<Integer, ContactDto>();
                for (ContactDto dto : result) {
                    map.put(dto.getId(), dto);
                    order.add(dto.getId());
                }
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
        ContactDto contact = find(toBeUpdated);
        if (!contact.isTheSameAs(toBeUpdated)) {
            addUpdateCommand(new UpdateContactCommand(contact.updatedCopy(toBeUpdated)));
        }
    }

    public ContactDto create() {
        ContactDto toBeCreated = new ContactDto(nextId());
        map.put(toBeCreated.getId(), toBeCreated);
        order.add(toBeCreated.getId());
        addUpdateCommand(new CreateContactCommand(toBeCreated));
        return toBeCreated;
    }

    public void remove(List<ContactDto> contacts) {
        for (ContactDto contact : contacts) {
            ContactDto toBeRemoved = find(contact);
            addUpdateCommand(new RemoveContactCommand(toBeRemoved));
        }
    }

    private void addUpdateCommand(UpdateCommand command) {
        updateQueue.add(command);
    }

    private void applyUpdates(UpdateResult result) {
        for (ContactDto created : result.getCreatedContacts()) {
            idMap.put(created.getId(), created.getTmpId());
            map.put(created.getTmpId(), created);
        }

        for (ContactDto updated : result.getUpdatedContacts()) {
            map.put(getId(updated), updated);
        }

        for (ContactDto removed : result.getRemovedContacts()) {
            int id = getId(removed);
            map.remove(id);
            order.remove(Integer.valueOf(id));
        }
    }

    private int getId(ContactDto contact) {
        Integer id = idMap.get(contact.getId());
        return id == null ? contact.getId() : id;
    }

    private ContactDto find(ContactDto contact) {
        return map.get(getId(contact));
    }

    private List<ContactDto> getContacts() {
        List<ContactDto> contacts = new ArrayList<ContactDto>();
        for (Integer id : order) {
            contacts.add(map.get(id));
        }
        return contacts;
    }

    private int nextId() {
        return --nextId;
    }
}
