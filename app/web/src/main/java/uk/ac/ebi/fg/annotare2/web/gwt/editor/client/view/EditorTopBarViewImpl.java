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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

import static uk.ac.ebi.fg.annotare2.web.gwt.common.client.util.Urls.logoutUrl;

/**
 * @author Olga Melnichuk
 */
public class EditorTopBarViewImpl extends Composite implements EditorTopBarView {

    @UiField
    InlineLabel userNameLabel;

    @UiField
    Anchor signOutLink;

    interface Binder extends UiBinder<Widget, EditorTopBarViewImpl> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public EditorTopBarViewImpl() {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    @Override
    public void setUserName(String userName) {
        userNameLabel.setText(userName);
    }

    @UiHandler("signOutLink")
    void signOutClicked(ClickEvent event) {
        Window.Location.assign(logoutUrl());
    }
}
