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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.CurrentUserAccountServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.UserDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.EditorTopBarView;

/**
 * @author Olga Melnichuk
 */
public class EditorTopBarActivity extends AbstractActivity {

    private final EditorTopBarView view;
    private final CurrentUserAccountServiceAsync userService;

    @Inject
    public EditorTopBarActivity(EditorTopBarView view,
                                CurrentUserAccountServiceAsync userService) {
        this.view = view;
        this.userService = userService;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view.asWidget());
        initAsync();
    }

    public Activity withPlace(Place place) {
        return this;
    }

    private void initAsync() {
        userService.me(AsyncCallbackWrapper.callbackWrap(
                new ReportingAsyncCallback<UserDto>(FailureMessage.UNABLE_TO_LOAD_SUBMISSION) {
                    @Override
                    public void onSuccess(UserDto result) {
                        view.setUserName(result.getName());
                    }
                }
        ));
    }
}
