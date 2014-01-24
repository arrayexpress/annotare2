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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Olga Melnichuk
 */
public class SuccessMessage extends Composite implements HasClickHandlers {

    @UiField
    Anchor hideLink;

    @UiField
    InlineLabel label;

    interface Binder extends UiBinder<Widget, SuccessMessage> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public SuccessMessage(String message, String hideText) {
        initWidget(Binder.BINDER.createAndBindUi(this));
        label.setText(message);
        hideLink.setText(hideText);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addHandler(handler, ClickEvent.getType());
    }

    @UiHandler("hideLink")
    void hideLinkClicked(ClickEvent event) {
        fireEvent(event);
    }
}
