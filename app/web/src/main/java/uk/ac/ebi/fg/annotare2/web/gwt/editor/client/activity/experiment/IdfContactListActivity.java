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
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.magetab.idf.Investigation;
import uk.ac.ebi.fg.annotare2.magetab.idf.Person;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.IdfData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.idf.IdfContactListView;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class IdfContactListActivity extends AbstractActivity implements IdfContactListView.Presenter {

    private final IdfContactListView view;

    private final PlaceController placeController;

    private final IdfData idfData;

    private Investigation investigation;

    @Inject
    public IdfContactListActivity(IdfContactListView view,
                                  PlaceController placeController,
                                  IdfData idfData) {
        this.view = view;
        this.placeController = placeController;
        this.idfData = idfData;
    }

    public IdfContactListActivity withPlace(Place place) {
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

    public void goTo(Place place) {
        placeController.goTo(place);
    }

    private void loadAsync() {
        idfData.getInvestigation(new AsyncCallbackWrapper<Investigation>() {
            @Override
            public void onFailure(Throwable caught) {
                //TODO
                Window.alert("Can't load Investigation Data.");
            }

            @Override
            public void onSuccess(Investigation inv) {
                if (inv != null) {
                    investigation = inv;
                    view.setContacts(inv.getContacts());
                }
            }
        }.wrap());
    }

    @Override
    public Person createContact() {
        return investigation.createContact();
    }

    @Override
    public void removeContacts(List<Integer> indices) {
        investigation.removeContact(indices);
    }
}
