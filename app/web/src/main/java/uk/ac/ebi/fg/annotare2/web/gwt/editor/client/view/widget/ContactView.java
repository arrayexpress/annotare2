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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ContactDto;

import java.util.Collections;
import java.util.List;

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

    @UiField(provided = true)
    MultiSelectList roles;

    private Presenter presenter;

    public ContactView(ContactDto contact) {
        roles = new MultiSelectList("Roles", new MultiSelectList.AsyncListItemProvider() {
            @Override
            public void getListItems(AsyncCallback<List<String>> callback) {
                if (presenter == null) {
                    callback.onSuccess(Collections.<String>emptyList());
                } else {
                    presenter.getRoles(callback);
                }
            }
        });

        initWidget(Binder.BINDER.createAndBindUi(this));

        addHeaderField(firstName);
        addHeaderField(midInitials);
        addHeaderField(lastName);

        addField(new EditableField<ContactDto.Editor, String>(firstName) {
            @Override
            protected String getValue(ContactDto.Editor p) {
                return p.getFirstName();
            }

            @Override
            protected void setValue(ContactDto.Editor p, String value) {
                p.setFirstName(value);
            }
        });

        addField(new EditableField<ContactDto.Editor, String>(midInitials) {
            @Override
            protected String getValue(ContactDto.Editor p) {
                return p.getMidInitials();
            }

            @Override
            protected void setValue(ContactDto.Editor p, String value) {
                p.setMidInitials(value);
            }
        });

        addField(new EditableField<ContactDto.Editor, String>(lastName) {
            @Override
            protected String getValue(ContactDto.Editor p) {
                return p.getLastName();
            }

            @Override
            protected void setValue(ContactDto.Editor p, String value) {
                p.setLastName(value);
            }
        });

        addField(new EditableField<ContactDto.Editor, String>(phone) {
            @Override
            protected String getValue(ContactDto.Editor p) {
                return p.getPhone();
            }

            @Override
            protected void setValue(ContactDto.Editor p, String value) {
                p.setPhone(value);
            }
        });

        addField(new EditableField<ContactDto.Editor, String>(fax) {
            @Override
            protected String getValue(ContactDto.Editor p) {
                return p.getFax();
            }

            @Override
            protected void setValue(ContactDto.Editor p, String value) {
                p.setFax(value);
            }
        });

        addField(new EditableField<ContactDto.Editor, String>(email) {
            @Override
            protected String getValue(ContactDto.Editor p) {
                return p.getEmail();
            }

            @Override
            protected void setValue(ContactDto.Editor p, String value) {
                p.setEmail(value);
            }
        });

        addField(new EditableField<ContactDto.Editor, String>(affiliation) {
            @Override
            protected String getValue(ContactDto.Editor p) {
                return p.getAffiliation();
            }

            @Override
            protected void setValue(ContactDto.Editor p, String value) {
                p.setAffiliation(value);
            }
        });

        addField(new EditableField<ContactDto.Editor, String>(address) {
            @Override
            protected String getValue(ContactDto.Editor p) {
                return p.getAddress();
            }

            @Override
            protected void setValue(ContactDto.Editor p, String value) {
                p.setAddress(value);
            }
        });

        addField(new EditableField<ContactDto.Editor, List<String>>(roles) {
            @Override
            protected List<String> getValue(ContactDto.Editor p) {
                return p.getRoles();
            }

            @Override
            protected void setValue(ContactDto.Editor p, List<String> value) {
                p.setRoles(value);
            }
        });

        setItem(contact.editor());
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public ContactDto getContact() {
        return getItem().copy();
    }

    public interface Presenter {

        void getRoles(AsyncCallback<List<String>> callback);
    }
}

