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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ApplicationDataServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.CurrentUserAccountServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.CookiePopupDeatils;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.UserDto;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.HeaderView;

/**
 * @author Olga Melnichuk
 */
public class HeaderActivity extends AbstractActivity {

    private final HeaderView view;
    private final PlaceController placeController;
    private final CurrentUserAccountServiceAsync userService;

    private final ApplicationDataServiceAsync applicationDataService;

    @Inject
    public HeaderActivity(HeaderView view,
                          PlaceController placeController,
                          CurrentUserAccountServiceAsync userService,
                          ApplicationDataServiceAsync applicationDataService) {
        this.view = view;
        this.placeController = placeController;
        this.userService = userService;
        this.applicationDataService = applicationDataService;
    }

    public HeaderActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view.asWidget());
        initAsync();
    }

    public void goTo(Place place) {
        placeController.goTo(place);
    }

    private void initAsync() {

        applicationDataService.getCookiePopupDetails(AsyncCallbackWrapper.callbackWrap(
                new ReportingAsyncCallback<CookiePopupDeatils>(ReportingAsyncCallback.FailureMessage.GENERIC_FAILURE) {
                    @Override
                    public void onSuccess(CookiePopupDeatils result) {
                        view.setNoticeCookie(result);
                    }
                }
        ));
        userService.me(AsyncCallbackWrapper.callbackWrap(
                new ReportingAsyncCallback<UserDto>(FailureMessage.UNABLE_TO_LOAD_USER_INFORMATION) {
                    @Override
                    public void onSuccess(UserDto result) {
                        view.setUserName(result.getName());
                    }
                }
        ));
    }
}