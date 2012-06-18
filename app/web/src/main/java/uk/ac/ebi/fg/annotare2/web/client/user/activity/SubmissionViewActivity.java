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

package uk.ac.ebi.fg.annotare2.web.client.user.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.client.user.place.SubmissionViewPlace;
import uk.ac.ebi.fg.annotare2.web.client.user.view.SubmissionView;

/**
 * @author Olga Melnichuk
 */
public class SubmissionViewActivity extends AbstractActivity implements SubmissionView.Presenter {

    private final SubmissionView view;
    private final PlaceController placeController;

    @Inject
    public SubmissionViewActivity(SubmissionView view, PlaceController placeController) {
        this.view = view;
        this.placeController = placeController;
    }

    public SubmissionViewActivity withPlace(SubmissionViewPlace place) {
        //this.token = place.getPlaceName();
        return this;
    }

    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        //view.setPlaceName(token);
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
    }

    @Override
    public String mayStop() {
        return "Please hold on. This activity is stopping.";
    }

    public void goTo(Place place) {
        placeController.goTo(place);
    }
}
