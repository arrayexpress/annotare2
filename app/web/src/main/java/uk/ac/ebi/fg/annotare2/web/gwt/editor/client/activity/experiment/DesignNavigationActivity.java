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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ApplicationDataServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ExperimentUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ExperimentUpdateEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpDesignPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.proxy.ExperimentDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.LeftNavigationView;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.ExpDesignSection;

import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.ExpDesignSection.experimentDesignSectionsFor;

/**
 * @author Olga Melnichuk
 */
public class DesignNavigationActivity extends AbstractActivity implements LeftNavigationView.Presenter {

    private final LeftNavigationView view;
    private final PlaceController placeController;
    private final ExperimentDataProxy expData;
    private final ApplicationDataServiceAsync dataService;

    private ExpDesignSection section;
    private HandlerRegistration experimentUpdateHandler;

    @Inject
    public DesignNavigationActivity(LeftNavigationView view,
                                    PlaceController placeController,
                                    ExperimentDataProxy expData,
                                    ApplicationDataServiceAsync dataService) {
        this.view = view;
        this.placeController = placeController;
        this.expData = expData;
        this.dataService = dataService;
    }

    public DesignNavigationActivity withPlace(ExpDesignPlace place) {
        this.section = place.getExpDesignSection();
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());

        experimentUpdateHandler = eventBus.addHandler(ExperimentUpdateEvent.getType(), new ExperimentUpdateEventHandler() {
            @Override
            public void onExperimentUpdate() {
                loadExperimentSettings(false);
            }
        });
        loadExperimentSettings(true);
        getArrayDesigns(null, 999999,
                new ReportingAsyncCallback<ArrayList<ArrayDesignRef>>(FailureMessage.UNABLE_TO_LOAD_ARRAYS_LIST) {
                    @Override
                    public void onSuccess(ArrayList<ArrayDesignRef> result) {
                        view.setArrayDesignList(result);
                    }
                }
        );
    }

    @Override
    public void onStop() {
        experimentUpdateHandler.removeHandler();
        super.onStop();
    }

    @Override
    public void navigateTo(LeftNavigationView.Section section) {
        goTo(new ExpDesignPlace((ExpDesignSection) section));
    }

    @Override
    public void saveSettings(ExperimentSettings settings) {
        expData.updateExperimentSettings(settings);
    }

    @Override
    public void getArrayDesigns(String query, int limit, AsyncCallback<ArrayList<ArrayDesignRef>> callback) {
        dataService.getArrayDesignList(query, limit, AsyncCallbackWrapper.callbackWrap(callback));
    }

    private void goTo(ExpDesignPlace place) {
        placeController.goTo(place);
    }

    private void loadExperimentSettings(final boolean setSections) {
        expData.getSettingsAsync(
                new ReportingAsyncCallback<ExperimentSettings>(FailureMessage.UNABLE_TO_LOAD_SUBMISSION_SETTINGS) {
                    @Override
                    public void onSuccess(ExperimentSettings result) {
                        if (setSections) {
                            List<ExpDesignSection> sections = experimentDesignSectionsFor(result.getExperimentType());
                            view.setSections(sections);
                            if (sections.contains(section)) {
                                view.setSelected(section);
                            } else if (!sections.isEmpty()) {
                                view.setSelected(sections.get(0));
                            } else {
                                goTo(new ExpDesignPlace(ExpDesignSection.NONE));
                            }
                        }
                        view.setExperimentSettings(result);
                    }
                });
    }
}
