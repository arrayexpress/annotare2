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
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.CurrentUserAccountServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UIUser;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.HeaderView;

/**
 * @author Olga Melnichuk
 */
public class HeaderActivity extends AbstractActivity implements HeaderView.Presenter {

    private final HeaderView view;
    private final PlaceController placeController;
    private final CurrentUserAccountServiceAsync rpcService;

    @Inject
    public HeaderActivity(HeaderView view,
                          PlaceController placeController,
                          CurrentUserAccountServiceAsync rpcService) {
        this.view = view;
        this.placeController = placeController;
        this.rpcService = rpcService;
    }

    public HeaderActivity withPlace(Place place) {
        //this.token = place.getPlaceName();
        return this;
    }

    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        //view.setPlaceName(token);
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
        asyncInit(view);
    }

    public void goTo(Place place) {
        placeController.goTo(place);
    }

    public void logout() {
        rpcService.logout(new AsyncCallbackWrapper<Void>() {
            public void onSuccess(Void result) {
                 Window.Location.reload();
            }

            public void onFailure(Throwable caught) {
                Window.alert("Error during logout");
            }
        }.wrap());
    }

    private void asyncInit(final HeaderView view) {
        rpcService.me(new AsyncCallbackWrapper<UIUser>() {
            public void onSuccess(UIUser result) {
                view.setUserName(result.getEmail());
            }

            public void onFailure(Throwable caught) {
                Window.alert("Error retrieving user");
            }
        }.wrap());
    }
}