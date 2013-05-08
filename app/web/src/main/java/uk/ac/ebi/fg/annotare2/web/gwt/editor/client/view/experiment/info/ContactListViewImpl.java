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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ContactDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ItemChangeEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ItemChangeEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ContactView;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DisclosureListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ContactListViewImpl extends ListView<ContactDto.Editor> implements ContactListView {

    private Presenter presenter;

    public ContactListViewImpl() {
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
        for (ContactDto p : contacts) {
            addContactView(p);
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private void addNewContact() {
        DisclosureListItem item = addContactView(presenter.createContact());
        //todo scroll + item.open();
    }

    private DisclosureListItem addContactView(ContactDto p) {
        final ContactView view = new ContactView(p);
        view.addItemChangeEventHandler(new ItemChangeEventHandler() {
            @Override
            public void onItemChange(ItemChangeEvent event) {
                presenter.updateContact(view.getContact());
            }
        });
        return addListItem(view);
    }

    private void removeSelectedContacts() {
        List<Integer> selected = getSelected();
        if (selected.isEmpty()) {
            return;
        }

        List<ContactDto> contacts = new ArrayList<ContactDto>();
        for (Integer index : selected) {
            DisclosureListItem item = getItem(index);
            ContactView view = (ContactView) item.getContent();
            contacts.add(view.getContact());
        }
        presenter.removeContacts(contacts);
        removeItems(selected);
    }
}
