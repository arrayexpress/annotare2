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

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.NotificationPopupPanel;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionType;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.event.SubmissionListUpdatedEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.SubmissionListPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.LeftMenuView;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.SubmissionListFilter;

public class LeftMenuActivity extends AbstractActivity implements LeftMenuView.Presenter {

    private final LeftMenuView view;
    private final PlaceController placeController;
    private final SubmissionServiceAsync asyncService;
    private EventBus eventBus;

    @Inject
    public LeftMenuActivity(LeftMenuView view, PlaceController placeController, SubmissionServiceAsync asyncService) {
        this.view = view;
        this.placeController = placeController;
        this.asyncService = asyncService;
        this.eventBus = null;
    }

    public LeftMenuActivity withPlace(Place place) {
        if (place instanceof SubmissionListPlace) {
            this.view.setFilter(((SubmissionListPlace) place).getFilter());
        }
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
        this.eventBus = eventBus;
    }

    public void onSubmissionFilterClick(SubmissionListFilter filter) {
        gotoSubmissionListViewPlace(filter);
    }

    @Override
    public void onSubmissionCreateClick(SubmissionType type, final AsyncCallback<Long> callback) {
        switch (type) {
            case EXPERIMENT:
                asyncService.createExperiment(new AsyncCallbackWrapper<Long>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Long result) {
                        callback.onSuccess(result);
                        notifySubmissionListUpdated();
                    }
                }.wrap());
                return;
            case ARRAY_DESIGN:
                asyncService.createArrayDesign(new AsyncCallbackWrapper<Long>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Long result) {
                        callback.onSuccess(result);
                        notifySubmissionListUpdated();
                    }
                }.wrap());
                return;
            default:
                NotificationPopupPanel.failure("Unknown submission type + " + type.getTitle(), null);
        }
    }

    @Override
    public void onSubmissionImportClick(SubmissionType type, final AsyncCallback<Long> callback) {
        switch (type) {
            case IMPORTED_EXPERIMENT:
                asyncService.createImportedExperiment( new AsyncCallbackWrapper<Long>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Long result) {
                        callback.onSuccess(result);
                        notifySubmissionListUpdated();
                    }
                }.wrap());
                return;
            default:
                NotificationPopupPanel.failure("Unknown submission type + " + type.getTitle(), null);
        }
    }

    public void goTo(Place place) {
        placeController.goTo(place);
    }

    private void notifySubmissionListUpdated() {
        if (null != eventBus) {
            eventBus.fireEvent(new SubmissionListUpdatedEvent());
        }
    }

    private void gotoSubmissionListViewPlace(SubmissionListFilter filter) {
        SubmissionListPlace place = new SubmissionListPlace();
        place.setFilter(filter);
        goTo(place);
    }
}
