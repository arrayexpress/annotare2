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
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ContactDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.PublicationDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;

import java.util.*;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class ExperimentData {

    private final SubmissionServiceAsync submissionService;
    private final DataChangeManager changes;

    private Set<SampleRow> samples;

    private Map<Integer, ContactDto> contactsMap;
    private Set<ContactDto> updatedContacts = new HashSet<ContactDto>();

    private Map<Integer, PublicationDto> publicationsMap;
    private ExperimentDetails details;
    private ExperimentDetails updatedDetails;

    private ExperimentSettings settings;

    @Inject
    public ExperimentData(SubmissionServiceAsync submissionService,
                          DataChangeManager changes) {
        this.submissionService = submissionService;
        this.changes = changes;
    }

    public void getSettingsAsync(final AsyncCallback<ExperimentSettings> callback) {
        if (settings != null) {
            callback.onSuccess(settings);
            return;
        }
        submissionService.getExperimentSettings(getSubmissionId(), new AsyncCallbackWrapper<ExperimentSettings>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ExperimentSettings result) {
                settings = result;
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void getSamplesAsync(final AsyncCallback<List<SampleRow>> callback) {
        if (samples != null) {
            callback.onSuccess(new ArrayList<SampleRow>(samples));
            return;
        }
        submissionService.getSamples(getSubmissionId(), new AsyncCallbackWrapper<List<SampleRow>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(List<SampleRow> result) {
                samples = new LinkedHashSet<SampleRow>(result);
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void getContactsAsync(final AsyncCallback<List<ContactDto>> callback) {
        if (contactsMap != null) {
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
                contactsMap = new HashMap<Integer, ContactDto>();
                for (ContactDto dto : result) {
                    contactsMap.put(dto.getId(), dto);
                }
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void getPublicationsAsync(final AsyncCallback<List<PublicationDto>> callback) {
        if (publicationsMap != null) {
            callback.onSuccess(getPublications());
            return;
        }
        submissionService.getPublications(getSubmissionId(), new AsyncCallbackWrapper<List<PublicationDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(List<PublicationDto> result) {
                publicationsMap = new HashMap<Integer, PublicationDto>();
                for (PublicationDto dto : result) {
                    publicationsMap.put(dto.getId(), dto);
                }
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void getDetailsAsync(final AsyncCallback<ExperimentDetails> callback) {
        if (details != null) {
            callback.onSuccess(details);
            return;
        }
        submissionService.getExperimentDetails(getSubmissionId(), new AsyncCallbackWrapper<ExperimentDetails>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ExperimentDetails result) {
                details = result;
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void saveDetails(ExperimentDetails details) {
        this.updatedDetails = details;
        this.changes.add("submissionDetails", new DataChangeManager.SaveDataHandler() {
            @Override
            public void onSave(DataChangeManager.Callback callback) {
                saveExperimentDetails(callback);
            }
        });
    }

    public void saveContact(ContactDto contact) {
        List<ContactDto> list = new ArrayList<ContactDto>();
        list.add(contact);
        saveContacts(list);
    }

    public void saveContacts(List<ContactDto> contacts) {
        this.updatedContacts.addAll(contacts);
        this.changes.add("contact", new DataChangeManager.SaveDataHandler() {
            @Override
            public void onSave(DataChangeManager.Callback callback) {
                saveExperimentContacts(callback);
            }
        });
    }

    private List<ContactDto> getContacts() {
        List<ContactDto> list = new ArrayList<ContactDto>();
        list.addAll(contactsMap.values());
        return list;
    }

    private List<PublicationDto> getPublications() {
        List<PublicationDto> list = new ArrayList<PublicationDto>();
        list.addAll(publicationsMap.values());
        return list;
    }

    private void saveExperimentDetails(final DataChangeManager.Callback callback) {
        if (updatedDetails == null || details.isContentEqual(updatedDetails)) {
            return;
        }
        callback.onStart();
        submissionService.saveExperimentDetails(getSubmissionId(), updatedDetails, new AsyncCallbackWrapper<ExperimentDetails>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onStop(caught);
            }

            @Override
            public void onSuccess(ExperimentDetails result) {
                details = result;
                callback.onStop(null);
            }
        }.wrap());
    }

    private void saveExperimentContacts(final DataChangeManager.Callback callback) {
        List<ContactDto> changes = new ArrayList<ContactDto>();
        for (ContactDto dto : updatedContacts) {
            ContactDto contact = contactsMap.get(dto.getId());
            if (!contact.isContentEqual(dto)) {
                changes.add(dto);
            }
        }
        if (changes.isEmpty()) {
            return;
        }
        callback.onStart();
        submissionService.saveContacts(getSubmissionId(), changes, new AsyncCallbackWrapper<List<ContactDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onStop(caught);
            }

            @Override
            public void onSuccess(List<ContactDto> result) {
                for (ContactDto dto : result) {
                    contactsMap.put(dto.getId(), dto);
                }
                callback.onStop(null);
            }
        }.wrap());
    }

}
