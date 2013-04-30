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
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.ExperimentData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpInfoPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info.ExpDetailsView;

/**
 * @author Olga Melnichuk
 */
public class ExpDetailsActivity extends AbstractActivity implements ExpDetailsView.Presenter {

    private final ExpDetailsView view;

    private final PlaceController placeController;

    private final ExperimentData experimentData;

    @Inject
    public ExpDetailsActivity(ExpDetailsView view,
                              PlaceController placeController,
                              ExperimentData experimentData) {
        this.view = view;
        this.placeController = placeController;
        this.experimentData = experimentData;
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
        experimentData.saveDetails(view.getDetails());
        super.onStop();
    }

    public void goTo(Place place) {
        placeController.goTo(place);
    }

    private void loadAsync() {
        experimentData.getDetailsAsync(new AsyncCallback<ExperimentDetails>() {
            @Override
            public void onFailure(Throwable caught) {
                //TODO
                Window.alert("Can't load experiment details.");
            }

            @Override
            public void onSuccess(ExperimentDetails details) {
                view.setDetails(details);
            }
        });
    }

    @Override
    public void saveDetails(ExperimentDetails details) {
        experimentData.saveDetails(details);
    }
}

