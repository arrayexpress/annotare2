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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.integration.RtFieldNames;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;

import static com.google.common.base.Strings.isNullOrEmpty;

public class ContactUsDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, ContactUsDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    TextBox subject;

    @UiField
    TextArea message;

    @UiField
    Button cancelButton;

    @UiField
    Button okButton;

    private Presenter presenter;

    public ContactUsDialog() {
        setModal(true);
        setGlassEnabled(true);
        setText("Contact us");
        setWidget(Binder.BINDER.createAndBindUi(this));
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show() {
        subject.setValue(RtFieldNames.CONTACT_US_SUBJECT);
        subject.setVisible(false);
        message.setValue("");
        okButton.setEnabled(true);
        super.show();
        Scheduler.get().scheduleDeferred(new Command() {
            public void execute() {
                message.setFocus(true);
            }
        });
    }

    @UiHandler("okButton")
    void okButtonClicked(ClickEvent event) {
        if(!isNullOrEmpty(message.getValue())) {
            presenter.sendMessage(subject.getValue().trim(), message.getValue().trim());
            okButton.setEnabled(false);
            showNotificationMole();
        }
    }

    @UiHandler("cancelButton")
    void cancelButtonClicked(ClickEvent event) {
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

    public interface Presenter {
        void sendMessage(String subject, String message);
    }

    private void showNotificationMole() {
        NotificationPopupPanel.message ("Thank you! Your message has been sent!", true);
        hide();
    }
}
