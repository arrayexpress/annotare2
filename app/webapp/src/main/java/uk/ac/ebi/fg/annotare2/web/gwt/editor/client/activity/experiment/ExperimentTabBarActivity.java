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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.CurrentUserAccountServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.UserDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExperimentPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.EditorTab;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.EditorTabBarView;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.ExperimentTab;

/**
 * @author Olga Melnichuk
 */
public class ExperimentTabBarActivity extends AbstractActivity implements EditorTabBarView.Presenter {

    private final EditorTabBarView view;
    private final PlaceController placeController;
    private ExperimentTab selectedTab;
    private UserDto currentUser;
    private boolean tabLoadedFlag = false;

    private final CurrentUserAccountServiceAsync userService;

    @Inject
    public ExperimentTabBarActivity(EditorTabBarView view,
                                    PlaceController placeController, CurrentUserAccountServiceAsync userService) {
        this.view = view;
        this.placeController = placeController;
        this.userService = userService;
    }

    public ExperimentTabBarActivity withPlace(ExperimentPlace place) {
        selectedTab = place.getSelectedTab();
        if(tabLoadedFlag) {
            view.selectTab(selectedTab);
        }
        return this;
    }

    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        loadCurrentUserAsync();
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
    }

    private void loadCurrentUserAsync() {
        userService.me(AsyncCallbackWrapper.callbackWrap(
                new ReportingAsyncCallback<UserDto>(ReportingAsyncCallback.FailureMessage.UNABLE_TO_LOAD_USER_INFORMATION) {
                    @Override
                    public void onSuccess(UserDto result) {
                        view.setCurrentUser(result);
                        view.initWithTabs(ExperimentTab.values());
                        view.selectTab(selectedTab);
                        tabLoadedFlag = true;
                    }
                }));
    }
    public void goTo(Place place) {
        placeController.goTo(place);
    }

    public void onTabSelect(EditorTab tab) {
        goTo(ExperimentPlace.create((ExperimentTab)tab));
    }
}
