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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorApp;
import java.util.Date;

public class SubmissionIsReadOnlyDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, SubmissionIsReadOnlyDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    CheckBox repeatCheckBox;

    @UiField
    Button okButton;

    public SubmissionIsReadOnlyDialog() {
        addStyleName("app-ReadOnlySubmissionDialogBox");
        setModal(true);
        setGlassEnabled(true);
        setText("Submissiron can not be modified");
        setWidget(Binder.BINDER.createAndBindUi(this));
        center();
    }


    @UiHandler("okButton")
    void okButtonClicked(ClickEvent event) {
        if (repeatCheckBox.getValue()) {
            Date cookieExpiryDate = new Date();
            CalendarUtil.addMonthsToDate(cookieExpiryDate,3);
            Cookies.setCookie(EditorApp.SUBMISSION_READONLY_COOKIE, "YEZ", cookieExpiryDate);
        }
        hide();
    }

    @Override
    protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
        super.onPreviewNativeEvent(event);
        if (Event.ONKEYDOWN == event.getTypeInt()) {
            if (KeyCodes.KEY_ESCAPE == event.getNativeEvent().getKeyCode()) {
                hide();
            }
        }
    }

}
