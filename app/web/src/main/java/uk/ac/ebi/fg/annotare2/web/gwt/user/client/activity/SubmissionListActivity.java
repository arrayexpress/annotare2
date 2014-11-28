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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.CurrentUserAccountServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionListServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.UserDto;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.event.SubmissionListUpdatedEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.event.SubmissionListUpdatedEventHandler;
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
    private final SubmissionListServiceAsync rpcService;
    private final PlaceController placeController;
    private final CurrentUserAccountServiceAsync userService;
    private final AsyncCallback<ArrayList<SubmissionRow>> callback;

    private HandlerRegistration submissionListUpdatedHandler;
    private SubmissionListFilter filter;

    @Inject
    public SubmissionListActivity(final SubmissionListView view,
                                  PlaceController placeController,
                                  SubmissionListServiceAsync rpcService,
                                  CurrentUserAccountServiceAsync userService) {
        this.view = view;
        this.placeController = placeController;
        this.rpcService = rpcService;
        this.userService = userService;
        this.callback = AsyncCallbackWrapper.callbackWrap(
                new ReportingAsyncCallback<ArrayList<SubmissionRow>>(FailureMessage.UNABLE_TO_LOAD_SUBMISSIONS_LIST) {
                    @Override
                    public void onSuccess(ArrayList<SubmissionRow> result) {
                        view.setSubmissions(result);
                    }
                }
        );

    }

    public SubmissionListActivity withPlace(SubmissionListPlace place) {
        filter = place.getFilter();
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
        submissionListUpdatedHandler = eventBus.addHandler(SubmissionListUpdatedEvent.getType(), new SubmissionListUpdatedEventHandler() {
            @Override
            public void onSubmissionListUpdated() {
                loadSubmissionListAsync();
            }
        });
        loadCurrentUserAsync();
        loadSubmissionListAsync();
    }

    @Override
    public void onStop() {
        submissionListUpdatedHandler.removeHandler();
        super.onStop();
    }

    private void loadCurrentUserAsync() {
        userService.me(AsyncCallbackWrapper.callbackWrap(
                new ReportingAsyncCallback<UserDto>(FailureMessage.UNABLE_TO_LOAD_USER_INFORMATION) {
            @Override
            public void onSuccess(UserDto result) {
                view.setCurator(result.isCurator());
            }
        }));
    }

    private void loadSubmissionListAsync() {
        switch (filter) {
            case ALL_SUBMISSIONS:
                rpcService.getAllSubmissions(callback);
                break;

            case INCOMPLETE_SUBMISSIONS:
                rpcService.getIncompleteSubmissions(callback);
                break;

            case COMPLETED_SUBMISSIONS:
                rpcService.getCompletedSubmissions(callback);
        }
    }

    public void goTo(Place place) {
        placeController.goTo(place);
    }

    public void onSubmissionSelected(long id) {
        SubmissionViewPlace place = new SubmissionViewPlace();
        place.setSubmissionId(id);
        goTo(place);
    }
}
