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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.ExperimentData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpDesignPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.LeftNavigationView;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.ExpDesignSection;

import java.util.List;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.ExpDesignSection.experimentDesignSectionsFor;

/**
 * @author Olga Melnichuk
 */
public class DesignNavigationActivity extends AbstractActivity implements LeftNavigationView.Presenter {

    private final LeftNavigationView view;
    private final PlaceController placeController;
    private final ExperimentData expData;

    private ExpDesignSection section;

    @Inject
    public DesignNavigationActivity(LeftNavigationView view,
                                    PlaceController placeController,
                                    ExperimentData expData) {
        this.view = view;
        this.placeController = placeController;
        this.expData = expData;
    }

    public DesignNavigationActivity withPlace(ExpDesignPlace place) {
        this.section = place.getExpDesignSection();
        return this;
    }

    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
        loadExperimentDetails();
    }

    @Override
    public void navigateTo(LeftNavigationView.Section section) {
        goTo(new ExpDesignPlace((ExpDesignSection) section));
    }

    private void goTo(ExpDesignPlace place) {
        placeController.goTo(place);
    }

    private void loadExperimentDetails() {
        expData.getSettings(
                new AsyncCallback<ExperimentSettings>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        //TODO
                        Window.alert(caught.getMessage());
                    }

                    @Override
                    public void onSuccess(ExperimentSettings result) {
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
                });
    }
}
