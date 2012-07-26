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
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionListServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UISubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UISubmissionRow;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.SubmissionListPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.SubmissionViewPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.SubmissionListFilter;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.SubmissionListView;

import java.util.ArrayList;

/**
 * @author Olga Melnichuk
 */
public class SubmissionListActivity extends AbstractActivity implements SubmissionListView.Presenter {

    private final SubmissionListView view;
    private final PlaceController placeController;
    private final SubmissionListServiceAsync rpcService;
    private SubmissionListFilter filter;

    @Inject
    public SubmissionListActivity(SubmissionListView view, PlaceController placeController, SubmissionListServiceAsync rpcService) {
        this.view = view;
        this.placeController = placeController;
        this.rpcService = rpcService;
    }

    public SubmissionListActivity withPlace(SubmissionListPlace place) {
        this.filter = place.getFilter();
        return this;
    }

    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
        loadSubmissionListAsync();
    }

    private void loadSubmissionListAsync() {
        switch (filter) {
            case ALL_SUBMISSIONS:
                rpcService.getAllSubmissions(new AsyncCallbackWrapper<ArrayList<UISubmissionRow>>() {
                    public void onSuccess(ArrayList<UISubmissionRow> result) {
                        view.setSubmissions(result);
                    }

                    public void onFailure(Throwable caught) {
                        Window.alert("Can't load submission list");
                    }

                }.wrap());
                return;

            case INCOMPLETE_SUBMISSIONS:
                rpcService.getIncompleteSubmissions(new AsyncCallbackWrapper<ArrayList<UISubmissionRow>>() {
                    public void onSuccess(ArrayList<UISubmissionRow> result) {
                        view.setSubmissions(result);
                    }

                    public void onFailure(Throwable caught) {
                        Window.alert("Can't load submission list");
                    }

                }.wrap());
                return;

            case COMPLETED_SUBMISSIONS:
                rpcService.getCompletedSubmissions(new AsyncCallbackWrapper<ArrayList<UISubmissionRow>>() {
                    public void onSuccess(ArrayList<UISubmissionRow> result) {
                        view.setSubmissions(result);
                    }

                    public void onFailure(Throwable caught) {
                        Window.alert("Can't load submission list");
                    }

                }.wrap());
                return;

            default:
                Window.alert("Illegal application state..");
        }
        //TODO
    }

    public void goTo(Place place) {
        placeController.goTo(place);
    }

    public void onSubmissionSelected(int id) {
        SubmissionViewPlace place = new SubmissionViewPlace();
        place.setSubmissionId(id);
        goTo(place);
    }
}
