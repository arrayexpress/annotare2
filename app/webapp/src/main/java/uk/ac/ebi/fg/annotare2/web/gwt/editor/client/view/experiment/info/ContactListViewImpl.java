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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ContactDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ContentChangeEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ContentChangeEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ContactView;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DisclosureListItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ContactListViewImpl extends ListView<ContactDto.Editor> implements ContactListView, ContactView.Presenter {

    private Presenter presenter;

    public ContactListViewImpl() {
        removeIcon.setTitle("Remove selected contacts");
        addIcon.setTitle("Add a new contact");
        addIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addNewContact();
            }
        });
        removeIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                removeSelectedContacts();
            }
        });
    }

    @Override
    public List<ContactDto> getContacts() {
        List<ContactDto> contacts = new ArrayList<ContactDto>();
        for (DisclosureListItem item : getItems()) {
            ContactView view = (ContactView) item.getContent();
            contacts.add(view.getContact());
        }
        return contacts;
    }

    @Override
    public void setContacts(List<ContactDto> contacts) {
        clear();
        for (ContactDto p : contacts) {
            addContactView(p);
        }
    }

    @Override
    public void getRoles(AsyncCallback<List<String>> callback) {
        if (presenter == null) {
            callback.onSuccess(Collections.<String>emptyList());
        } else {
            presenter.getRoles(callback);
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private void addNewContact() {
        presenter.createContact();
    }

    private DisclosureListItem addContactView(ContactDto p) {
        final ContactView view = new ContactView(p);
        view.addContentChangeEventHandler(new ContentChangeEventHandler() {
            @Override
            public void onContentChange(ContentChangeEvent event) {
                if (presenter != null) {
                    presenter.updateContact(view.getContact());
                }
            }
        });
        view.setPresenter(this);
        return addListItem(view);
    }

    private void removeSelectedContacts() {
        List<Integer> selected = getSelected();
        if (selected.isEmpty()) {
            Window.alert("Please select the contact(s) to delete by clicking on the checkbox beside their name.");
            return;
        }
        if (selected.size()==listPanel.getWidgetCount()) {
            Window.alert("You must have at least one contact for this submission so you can't delete all contacts. " +
                    "Please edit the details instead.");
            return;
        }

        List<ContactDto> contacts = new ArrayList<ContactDto>();
        StringBuilder confirmationMessage = new StringBuilder();
        confirmationMessage.append("Are you sure you want to delete the following contact"+(selected.size()==1 ? "" : "s")+"?\n");
        int unnamedCount = 0;
        for (Integer index : selected) {
            DisclosureListItem item = getItem(index);
            ContactView view = (ContactView) item.getContent();
            ContactDto contactDto = view.getContact();
            contacts.add(contactDto);
            confirmationMessage.append("\n");
            String name = contactDto.getFirstName() + " " + contactDto.getLastName();
            if (name.equalsIgnoreCase("null null")) {
                name = "Unnamed "+ (++unnamedCount);
            }
            confirmationMessage.append(name);
        }
        if (Window.confirm(confirmationMessage.toString())) {
            presenter.removeContacts(contacts);
            removeItems(selected);
        }
    }
}
