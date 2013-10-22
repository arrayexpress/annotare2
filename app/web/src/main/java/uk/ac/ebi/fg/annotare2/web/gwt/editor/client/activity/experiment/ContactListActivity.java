/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.configmodel.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ContactDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data.ExperimentData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data.OntologyData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CriticalUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CriticalUpdateEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpInfoPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info.ContactListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ContactListActivity extends AbstractActivity implements ContactListView.Presenter {

    private final ContactListView view;
    private final ExperimentData experimentData;
    private final OntologyData ontologyData;

    private HandlerRegistration criticalUpdateHandler;

    @Inject
    public ContactListActivity(ContactListView view,
                               ExperimentData experimentData,
                               OntologyData ontologyData) {
        this.view = view;
        this.experimentData = experimentData;
        this.ontologyData = ontologyData;
    }

    public ContactListActivity withPlace(ExpInfoPlace place) {
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
        experimentData.updateContacts(view.getContacts());
        criticalUpdateHandler.removeHandler();
        super.onStop();
    }

    @Override
    public void updateContact(ContactDto contact) {
        experimentData.updateContact(contact);
    }

    @Override
    public void createContact() {
        experimentData.createContact();
    }

    @Override
    public void removeContacts(List<ContactDto> contacts) {
        experimentData.removeContacts(contacts);
    }

    @Override
    public void getRoles(final AsyncCallback<List<String>> callback) {
        ontologyData.getContactRoles(new AsyncCallback<List<OntologyTerm>>(){
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
        experimentData.getContactsAsync(new AsyncCallback<List<ContactDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                //TODO
                Window.alert("Can't load contact list.");
            }

            @Override
            public void onSuccess(List<ContactDto> result) {
                view.setContacts(result);
            }
        });
    }
}
