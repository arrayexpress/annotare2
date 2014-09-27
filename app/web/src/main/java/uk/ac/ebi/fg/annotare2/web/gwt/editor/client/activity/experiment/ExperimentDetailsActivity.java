/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.OntologyTermGroup;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.dataproxy.ApplicationDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.dataproxy.ExperimentDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.dataproxy.OntologyDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpInfoPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info.ExperimentDetailsView;

import java.util.Collections;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ExperimentDetailsActivity extends AbstractActivity implements ExperimentDetailsView.Presenter {

    private final ExperimentDetailsView view;

    private final PlaceController placeController;

    private final ExperimentDataProxy experimentDataProxy;

    private final OntologyDataProxy ontologyDataProxy;

    private final ApplicationDataProxy applicationDataProxy;

    @Inject
    public ExperimentDetailsActivity(ExperimentDetailsView view,
                                     PlaceController placeController,
                                     ExperimentDataProxy experimentDataProxy,
                                     OntologyDataProxy ontologyDataProxy,
                                     ApplicationDataProxy applicationDataProxy) {
        this.view = view;
        this.placeController = placeController;
        this.experimentDataProxy = experimentDataProxy;
        this.ontologyDataProxy = ontologyDataProxy;
        this.applicationDataProxy = applicationDataProxy;
    }

    public ExperimentDetailsActivity withPlace(ExpInfoPlace place) {
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
        experimentDataProxy.updateDetails(view.getDetails());
        super.onStop();
    }

    public void goTo(Place place) {
        placeController.goTo(place);
    }

    private void loadAsync() {
        experimentDataProxy.getExperimentProfileTypeAsync(new AsyncCallback<ExperimentProfileType>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Unable to load experiment type");
            }

            @Override
            public void onSuccess(ExperimentProfileType result) {
                loadDetailsAsync(result);
            }
        });

    }

    private void loadDetailsAsync(final ExperimentProfileType type) {
        experimentDataProxy.getDetailsAsync(new AsyncCallback<ExperimentDetailsDto>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Unable to load experiment details");
            }

            @Override
            public void onSuccess(ExperimentDetailsDto details) {
                setDetails(type, details);
            }
        });
    }

    private void setDetails(ExperimentProfileType type, final ExperimentDetailsDto details) {
        applicationDataProxy.getAeExperimentTypesAsync(type, new AsyncCallback<List<String>>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Unable to experiment type options");
                view.setDetails(details, Collections.<String>emptyList());
            }

            @Override
            public void onSuccess(List<String> result) {
                view.setDetails(details, result);
            }
        });
    }

    @Override
    public void saveDetails(ExperimentDetailsDto details) {
        experimentDataProxy.updateDetails(details);
    }

    @Override
    public void getExperimentalDesigns(AsyncCallback<List<OntologyTermGroup>> callback) {
        ontologyDataProxy.getExperimentalDesigns(callback);
    }
}

