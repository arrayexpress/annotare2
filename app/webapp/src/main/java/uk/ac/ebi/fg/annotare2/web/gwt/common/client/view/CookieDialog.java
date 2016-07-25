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

package uk.ac.ebi.fg.annotare2.web.gwt.common.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;

import java.util.Date;

public class CookieDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, CookieDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    private String title;
    private String cookieName;
    private Date cookieExpiryDate;

    @UiField
    HTML messageHtml;

    @UiField
    CheckBox repeatCheckBox;

    @UiField
    Button okButton;

    public CookieDialog(String title, String html, String cookie, Date cookieExpiryDate) {
        this.title = title;
        this.cookieName = cookie;
        this.cookieExpiryDate = cookieExpiryDate;
        addStyleName("app-CookieDialog");
        setModal(true);
        setGlassEnabled(true);
        setText(title);
        setWidget(Binder.BINDER.createAndBindUi(this));
        this.messageHtml.setHTML(html);
        center();
    }


    @UiHandler("okButton")
    void okButtonClicked(ClickEvent event) {
        if (repeatCheckBox.getValue()) {
            Cookies.setCookie(cookieName, "YEZ", cookieExpiryDate);
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
