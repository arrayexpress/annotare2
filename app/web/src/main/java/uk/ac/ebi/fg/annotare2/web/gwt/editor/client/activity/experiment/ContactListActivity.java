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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ContactDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.ExperimentData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpInfoPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info.ContactListView;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ContactListActivity extends AbstractActivity implements ContactListView.Presenter {

    private final ContactListView view;

    private final ExperimentData experimentData;

    @Inject
    public ContactListActivity(ContactListView view,
                               ExperimentData experimentData) {
        this.view = view;
        this.experimentData = experimentData;
    }

    public ContactListActivity withPlace(ExpInfoPlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
        loadAsync();
    }

    @Override
    public void onStop() {
        //TODO save changes here
        super.onStop();
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

    @Override
    public void saveContact(ContactDto contact) {
        experimentData.saveContact(contact);
    }

    @Override
    public void createContact(AsyncCallback<ContactDto> callback) {
        //experimentData.createContact();
    }

    @Override
    public void removeContacts(List<Integer> contactIds) {
        //experimentData.removeContacts(contactIds);
    }
}
