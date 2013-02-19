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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.idf;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import uk.ac.ebi.fg.annotare2.magetab.rowbased.Person;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ContactView;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DisclosureListItem;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class IdfContactListViewImpl extends IdfListView<Person> implements IdfContactListView {

    private Presenter presenter;

    public IdfContactListViewImpl() {

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
    public void setContacts(List<Person> contacts) {
        for (Person p : contacts) {
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

    private DisclosureListItem addContactView(Person p) {
        return addListItem(new ContactView(p));
    }

    private void removeSelectedContacts() {
        List<Integer> selected = getSelected();
        if (selected.isEmpty()) {
            return;
        }
        presenter.removeContacts(selected);
        removeItems(selected);
    }
}
