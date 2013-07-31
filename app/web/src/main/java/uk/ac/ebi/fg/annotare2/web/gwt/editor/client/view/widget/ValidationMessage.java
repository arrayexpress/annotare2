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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

/**
 * @author Olga Melnichuk
 */
public class ValidationMessage extends Composite {

    interface Binder extends UiBinder<Widget, ValidationMessage> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    Label messageLabel;

    @UiField
    Anchor closeLink;

    @UiField
    HTMLPanel panel;

    public ValidationMessage() {
        initWidget(Binder.BINDER.createAndBindUi(this));
        panel.setVisible(false);
    }

    public void setMessage(String message) {
        panel.setVisible(!isEmpty(message));
        messageLabel.setText(message == null ? "" : message);
    }

    private boolean isEmpty(String v) {
        return v == null || v.isEmpty();
    }

    @UiHandler("closeLink")
    void closeLinkClicked(ClickEvent event) {
        setMessage(null);
    }
}
