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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.magetab.idf.Person;

/**
 * @author Olga Melnichuk
 */
public class ContactView extends DisclosurePanelContent {

    interface Binder extends UiBinder<Widget, ContactView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    TextBox firstName;

    @UiField
    TextBox midInitials;

    @UiField
    TextBox lastName;

    @UiField
    TextBox phone;

    @UiField
    TextBox fax;

    @UiField
    TextBox email;

    @UiField
    TextArea affiliation;

    @UiField
    TextArea address;

    @UiField
    TextArea roles;

    private Person person;

    public ContactView() {
        initWidget(Binder.BINDER.createAndBindUi(this));

        firstName.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                onRecordChange();
                if (person != null) {
                    person.getFirstName().setValue(firstName.getValue());
                }
            }
        });

        midInitials.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                onRecordChange();
                if (person != null) {
                    person.getMidInitials().setValue(midInitials.getValue());
                }
            }
        });

        lastName.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                onRecordChange();
                if (person != null) {
                    person.getLastName().setValue(lastName.getValue());
                }
            }
        });
    }

    public void update(Person p) {
        this.person = p;
        firstName.setValue(p.getFirstName().getValue());
        midInitials.setValue(p.getMidInitials().getValue());
        lastName.setValue(p.getLastName().getValue());
        //TODO
        onRecordChange();
    }

    private void onRecordChange() {
        String fn = firstName.getValue();
        String mi = midInitials.getValue();
        String ln = lastName.getValue();
        fireRecordChangeEvent(
                (fn.isEmpty() ? "" : fn + " ") +
                        (mi.isEmpty() ? "" : mi + " ") +
                        (ln.isEmpty() ? "" : ln));
    }

}
