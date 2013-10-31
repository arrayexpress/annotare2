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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.OntologyTermGroup;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data.ExperimentData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data.OntologyData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpInfoPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info.ExperimentDetailsView;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ExpDetailsActivity extends AbstractActivity implements ExperimentDetailsView.Presenter {

    private final ExperimentDetailsView view;

    private final PlaceController placeController;

    private final ExperimentData experimentData;

    private final OntologyData ontologyData;

    @Inject
    public ExpDetailsActivity(ExperimentDetailsView view,
                              PlaceController placeController,
                              ExperimentData experimentData,
                              OntologyData ontologyData) {
        this.view = view;
        this.placeController = placeController;
        this.experimentData = experimentData;
        this.ontologyData = ontologyData;
    }

    public ExpDetailsActivity withPlace(ExpInfoPlace place) {
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
        experimentData.updateDetails(view.getDetails());
        super.onStop();
    }

    public void goTo(Place place) {
        placeController.goTo(place);
    }

    private void loadAsync() {
        experimentData.getDetailsAsync(new AsyncCallback<ExperimentDetailsDto>() {
            @Override
            public void onFailure(Throwable caught) {
                //TODO
                Window.alert("Can't load experiment details.");
            }

            @Override
            public void onSuccess(ExperimentDetailsDto details) {
                view.setDetails(details);
            }
        });
    }

    @Override
    public void saveDetails(ExperimentDetailsDto details) {
        experimentData.updateDetails(details);
    }

    @Override
    public void getExperimentalDesigns(AsyncCallback<List<OntologyTermGroup>> callback) {
        ontologyData.getExperimentalDesigns(callback);
    }
}

