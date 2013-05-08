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
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ContactDto;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ChangeableValues.hasChangeableValue;

/**
 * @author Olga Melnichuk
 */
public class ContactView extends ItemView<ContactDto.Editor> {

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

    public ContactView(ContactDto contact) {
        initWidget(Binder.BINDER.createAndBindUi(this));

        addHeaderField(hasChangeableValue(firstName));
        addHeaderField(hasChangeableValue(midInitials));
        addHeaderField(hasChangeableValue(lastName));

        addField(new EditableField<ContactDto.Editor, String>(hasChangeableValue(firstName)) {
            @Override
            protected String getValue(ContactDto.Editor p) {
                return p.getFirstName();
            }

            @Override
            protected void setValue(ContactDto.Editor p, String value) {
                p.setFirstName(value);
            }
        });

        addField(new EditableField<ContactDto.Editor, String>(hasChangeableValue(midInitials)) {
            @Override
            protected String getValue(ContactDto.Editor p) {
                return p.getMidInitials();
            }

            @Override
            protected void setValue(ContactDto.Editor p, String value) {
                p.setMidInitials(value);
            }
        });

        addField(new EditableField<ContactDto.Editor, String>(hasChangeableValue(lastName)) {
            @Override
            protected String getValue(ContactDto.Editor p) {
                return p.getLastName();
            }

            @Override
            protected void setValue(ContactDto.Editor p, String value) {
                p.setLastName(value);
            }
        });

        addField(new EditableField<ContactDto.Editor, String>(hasChangeableValue(phone)) {
            @Override
            protected String getValue(ContactDto.Editor p) {
                return p.getPhone();
            }

            @Override
            protected void setValue(ContactDto.Editor p, String value) {
                p.setPhone(value);
            }
        });

        addField(new EditableField<ContactDto.Editor, String>(hasChangeableValue(fax)) {
            @Override
            protected String getValue(ContactDto.Editor p) {
                return p.getFax();
            }

            @Override
            protected void setValue(ContactDto.Editor p, String value) {
                p.setFax(value);
            }
        });

        addField(new EditableField<ContactDto.Editor, String>(hasChangeableValue(email)) {
            @Override
            protected String getValue(ContactDto.Editor p) {
                return p.getEmail();
            }

            @Override
            protected void setValue(ContactDto.Editor p, String value) {
                p.setEmail(value);
            }
        });

        addField(new EditableField<ContactDto.Editor, String>(hasChangeableValue(affiliation)) {
            @Override
            protected String getValue(ContactDto.Editor p) {
                return p.getAffiliation();
            }

            @Override
            protected void setValue(ContactDto.Editor p, String value) {
                p.setAffiliation(value);
            }
        });

        addField(new EditableField<ContactDto.Editor, String>(hasChangeableValue(address)) {
            @Override
            protected String getValue(ContactDto.Editor p) {
                return p.getAddress();
            }

            @Override
            protected void setValue(ContactDto.Editor p, String value) {
                p.setAddress(value);
            }
        });

        setItem(contact.editor());
    }

    public ContactDto getContact() {
        return getItem().copy();
    }
}

