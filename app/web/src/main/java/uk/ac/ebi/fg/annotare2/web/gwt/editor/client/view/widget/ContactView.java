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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.magetab.idf.Person;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ChangeableValues.hasChangeableValue;

/**
 * @author Olga Melnichuk
 */
public class ContactView extends IdfItemView<Person> {

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

    public ContactView(Person person) {
        initWidget(Binder.BINDER.createAndBindUi(this));

        addHeaderField(hasChangeableValue(firstName));
        addHeaderField(hasChangeableValue(midInitials));
        addHeaderField(hasChangeableValue(lastName));

        addField(new EditableField<Person, String>(hasChangeableValue(firstName)) {
            @Override
            protected String getValue(Person p) {
                return p.getFirstName().getValue();
            }

            @Override
            protected void setValue(Person p, String value) {
                p.getFirstName().setValue(value);
            }
        });

        addField(new EditableField<Person, String>(hasChangeableValue(midInitials)) {
            @Override
            protected String getValue(Person p) {
                return p.getMidInitials().getValue();
            }

            @Override
            protected void setValue(Person p, String value) {
                p.getMidInitials().setValue(value);
            }
        });

        addField(new EditableField<Person, String>(hasChangeableValue(lastName)) {
            @Override
            protected String getValue(Person p) {
                return p.getLastName().getValue();
            }

            @Override
            protected void setValue(Person p, String value) {
                p.getLastName().setValue(value);
            }
        });

        addField(new EditableField<Person, String>(hasChangeableValue(phone)) {
            @Override
            protected String getValue(Person p) {
                return p.getPhone().getValue();
            }

            @Override
            protected void setValue(Person p, String value) {
                p.getPhone().setValue(value);
            }
        });

        addField(new EditableField<Person, String>(hasChangeableValue(fax)) {
            @Override
            protected String getValue(Person p) {
                return p.getFax().getValue();
            }

            @Override
            protected void setValue(Person p, String value) {
                p.getFax().setValue(value);
            }
        });

        addField(new EditableField<Person, String>(hasChangeableValue(email)) {
            @Override
            protected String getValue(Person p) {
                return p.getEmail().getValue();
            }

            @Override
            protected void setValue(Person p, String value) {
                p.getEmail().setValue(value);
            }
        });

        addField(new EditableField<Person, String>(hasChangeableValue(affiliation)) {
            @Override
            protected String getValue(Person p) {
                return p.getAffiliation().getValue();
            }

            @Override
            protected void setValue(Person p, String value) {
                p.getAffiliation().setValue(value);
            }
        });

        addField(new EditableField<Person, String>(hasChangeableValue(address)) {
            @Override
            protected String getValue(Person p) {
                return p.getAddress().getValue();
            }

            @Override
            protected void setValue(Person p, String value) {
                p.getAddress().setValue(value);
            }
        });

        setItem(person);
    }
}

