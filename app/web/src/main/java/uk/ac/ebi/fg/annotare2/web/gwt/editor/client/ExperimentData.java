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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ContactDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.PublicationDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.UpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.UpdateResult;

import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class ExperimentData {

    private final SubmissionServiceAsync submissionService;

    private ExperimentSamples samples;

    private ExperimentContacts contacts;

    private ExperimentPublications publications;

    private ExperimentDetails details;

    private ExperimentSettings settings;

    @Inject
    public ExperimentData(EventBus eventBus,
                          SubmissionServiceAsync submissionServiceAsync) {
        submissionService = submissionServiceAsync;

        UpdateQueue updateQueue = new UpdateQueue(eventBus,
                new UpdateQueue.Transport() {
                    @Override
                    public void sendUpdates(List<UpdateCommand> commands, final AsyncCallback<UpdateResult> callback) {
                        submissionService.updateExperiment(getSubmissionId(), commands, new AsyncCallbackWrapper<UpdateResult>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                callback.onFailure(caught);
                            }

                            @Override
                            public void onSuccess(UpdateResult result) {
                                callback.onSuccess(result);
                            }
                        }.wrap());
                    }
                });

        details = new ExperimentDetails(submissionService, updateQueue);
        contacts = new ExperimentContacts(submissionService, updateQueue);
        publications = new ExperimentPublications(submissionService, updateQueue);
        samples = new ExperimentSamples(submissionService, updateQueue);
        GWT.log(getClass().getName() + ": initialized");
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

    public void getDetailsAsync(AsyncCallback<DetailsDto> callback) {
        details.getDetailsAsync(callback);
    }

    public void getContactsAsync(AsyncCallback<List<ContactDto>> callback) {
        contacts.getContactsAsync(callback);
    }

    public void getPublicationsAsync(AsyncCallback<List<PublicationDto>> callback) {
        publications.getPublicationsAsync(callback);
    }

    public void getSamplesAsync(AsyncCallback<List<SampleRow>> callback) {
        samples.getSamplesAsync(callback);
    }

    public void updateDetails(DetailsDto toBeUpdated) {
        details.updateDetails(toBeUpdated);
    }

    public ContactDto createContact() {
        return contacts.create();
    }

    public void updateContact(ContactDto toBeUpdated) {
        List<ContactDto> list = new ArrayList<ContactDto>();
        list.add(toBeUpdated);
        updateContacts(list);
    }

    public void updateContacts(List<ContactDto> toBeUpdated) {
        contacts.update(toBeUpdated);
    }

    public void removeContacts(List<ContactDto> toBeRemoved) {
        contacts.remove(toBeRemoved);
    }
}
