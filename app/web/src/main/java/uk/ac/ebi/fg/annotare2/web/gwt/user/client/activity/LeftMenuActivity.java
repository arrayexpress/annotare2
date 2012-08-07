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

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UISubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.SubmissionListPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.SubmissionViewPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.LeftMenuView;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.SubmissionListFilter;

/**
 * @author Olga Melnichuk
 */
public class LeftMenuActivity extends AbstractActivity implements LeftMenuView.Presenter {

    private final LeftMenuView view;
    private final PlaceController placeController;
    private final SubmissionServiceAsync asyncService;

    @Inject
    public LeftMenuActivity(LeftMenuView view, PlaceController placeController, SubmissionServiceAsync asyncService) {
        this.view = view;
        this.placeController = placeController;
        this.asyncService = asyncService;
    }

    public LeftMenuActivity withPlace(Place place) {
        if (place instanceof SubmissionListPlace) {
            this.view.setFilter(((SubmissionListPlace) place).getFilter());
        }
        return this;
    }

    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
    }

    public void onSubmissionFilterClick(SubmissionListFilter filter) {
        gotoSubmissionListViewPlace(filter);
    }

    public void onSubmissionCreateButtonClick() {
        asyncService.createSubmission(new AsyncCallbackWrapper<Integer>() {
            public void onFailure(Throwable caught) {
                //TODO
                Window.alert("Can't create new submission");
            }

            public void onSuccess(Integer submissionId) {
                gotoSubmissionViewPlace(submissionId);
            }
        }.wrap());
    }

    public void goTo(Place place) {
        placeController.goTo(place);
    }

    private void gotoSubmissionListViewPlace(SubmissionListFilter filter) {
        SubmissionListPlace place = new SubmissionListPlace();
        place.setFilter(filter);
        goTo(place);
    }

    private void gotoSubmissionViewPlace(int id) {
        SubmissionViewPlace place = new SubmissionViewPlace();
        place.setSubmissionId(id);
        goTo(place);
    }
}
