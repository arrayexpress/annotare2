/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ContactDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CriticalUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CriticalUpdateEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpDesignPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpInfoPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.proxy.ExperimentDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.proxy.OntologyDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info.ContactListView;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ContactView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ContactListActivity extends AbstractActivity implements ContactListView.Presenter {

    private final ContactListView view;
    private final ExperimentDataProxy experimentDataProxy;
    private final OntologyDataProxy ontologyDataProxy;

    private HandlerRegistration criticalUpdateHandler;

    @Inject
    public ContactListActivity(ContactListView view,
                               ExperimentDataProxy experimentDataProxy,
                               OntologyDataProxy ontologyDataProxy) {
        this.view = view;
        this.experimentDataProxy = experimentDataProxy;
        this.ontologyDataProxy = ontologyDataProxy;
    }

    public ContactListActivity withPlace(ExpInfoPlace place) {
        return this;
    }

    public ContactListActivity withPlace(ExpDesignPlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
        loadAsync();

        criticalUpdateHandler = eventBus.addHandler(CriticalUpdateEvent.getType(), new CriticalUpdateEventHandler() {
            @Override
            public void criticalUpdateStarted(CriticalUpdateEvent event) {
            }

            @Override
            public void criticalUpdateFinished(CriticalUpdateEvent event) {
                loadAsync();
            }
        });
    }

    @Override
    public void onStop() {
        experimentDataProxy.updateContacts(view.getContacts());
        criticalUpdateHandler.removeHandler();
        super.onStop();
    }

    @Override
    public void updateContact(ContactView contactView) {
        experimentDataProxy.getContactsAsync(
                new ReportingAsyncCallback<List<ContactDto>>(FailureMessage.UNABLE_TO_LOAD_CONTACT_LIST) {
                    @Override
                    public void onSuccess(List<ContactDto> result) {
                        if(hasDuplicateSubmitterRole(result)){
                            contactView.removeSelectedRole("submitter");
                            view.submitterRoleError();
                        }else{
                            experimentDataProxy.updateContact(contactView.getContact());
                        }
                    }

                    private boolean hasDuplicateSubmitterRole(List<ContactDto> contactDtos) {
                        return contactDtos.stream()
                                .anyMatch(this::isDuplicateSubmitter);
                    }

                    private boolean isDuplicateSubmitter(ContactDto contactDto) {
                        return isDifferentEmail(contactDto) && bothHaveSubmitterRole(contactDto);
                    }

                    private boolean bothHaveSubmitterRole(ContactDto contactDto) {
                        String submitterRole = "submitter";
                        return contactDto.getRoles().contains(submitterRole) &&
                                contactView.getContact().getRoles().contains(submitterRole);
                    }

                    private boolean isDifferentEmail(ContactDto contactDto) {
                        return (null != contactView.getContact() && null != contactDto.getEmail() && !contactDto.getEmail().equalsIgnoreCase(contactView.getContact().getEmail()));
                    }
                }
        );

    }

    @Override
    public void createContact() {
        experimentDataProxy.createContact();
    }

    @Override
    public void removeContacts(List<ContactDto> contacts) {
        experimentDataProxy.removeContacts(contacts);
    }

    @Override
    public void getRoles(final AsyncCallback<List<String>> callback) {
        ontologyDataProxy.getContactRoles(new AsyncCallback<List<OntologyTerm>>(){
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(List<OntologyTerm> result) {
                List<String> roles = new ArrayList<String>();
                for(OntologyTerm term : result) {
                    roles.add(term.getLabel());
                }
                callback.onSuccess(roles);
            }
        });
    }

    private void loadAsync() {
        experimentDataProxy.getContactsAsync(
                new ReportingAsyncCallback<List<ContactDto>>(FailureMessage.UNABLE_TO_LOAD_CONTACT_LIST) {
                    @Override
                    public void onSuccess(List<ContactDto> result) {
                        view.setContacts(result);
                    }
                }
        );
    }
}
