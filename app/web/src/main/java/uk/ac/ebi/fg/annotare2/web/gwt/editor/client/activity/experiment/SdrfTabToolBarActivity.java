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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SdrfServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.SdrfPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.sdrf.SdrfTabToolBarView;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class SdrfTabToolBarActivity extends AbstractActivity implements SdrfTabToolBarView.Presenter {

    private final SdrfTabToolBarView view;

    private final PlaceController placeController;

    private final SdrfServiceAsync sdrfService;

    private SdrfPlace place;

    @Inject
    public SdrfTabToolBarActivity(SdrfTabToolBarView view,
                                  PlaceController placeController,
                                  SdrfServiceAsync sdrfService) {
        this.view = view;
        this.placeController = placeController;
        this.sdrfService = sdrfService;
    }

    public SdrfTabToolBarActivity withPlace(Place place) {
        this.place = (SdrfPlace) place;
        return this;
    }

    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        view.setSheetModeOn(place.isSheetModeOn());
        containerWidget.setWidget(view.asWidget());
    }

    public void goTo(Place place) {
        placeController.goTo(place);
    }

    @Override
    public void importFile(final AsyncCallback<Void> callback) {
        sdrfService.importData(getSubmissionId(), new AsyncCallbackWrapper<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                // TODO
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Void result) {
                callback.onSuccess(result);
            }
        }.wrap());
    }

    @Override
    public void switchToSheetMode(boolean yesNo) {
        SdrfPlace newPlace = new SdrfPlace(place);
        newPlace.setSheetModeOn(yesNo);
        goTo(newPlace);
    }
}
