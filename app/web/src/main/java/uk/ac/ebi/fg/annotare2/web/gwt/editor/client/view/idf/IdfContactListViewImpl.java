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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.idf;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.magetab.idf.Person;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ItemSelectionEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.resources.EditorResources;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ContactListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class IdfContactListViewImpl extends Composite implements IdfContactListView {

    interface Binder extends UiBinder<Widget, IdfContactListViewImpl> {
    }

    @UiField
    VerticalPanel listPanel;

    @UiField
    Image addIcon;

    @UiField
    Image removeIcon;

    private Presenter presenter;

    private int selection = 0;

    public IdfContactListViewImpl() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));

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

    @UiFactory
    public EditorResources getResources() {
        EditorResources.INSTANCE.editorStyles().ensureInjected();
        return EditorResources.INSTANCE;
    }

    public void setContacts(List<Person> contacts) {
        for (Person p : contacts) {
            addListItem(p);
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private void addNewContact() {
        ContactListItem item = addListItem(presenter.addContact());
        //todo scroll + item.open();
    }

    private ContactListItem addListItem(Person p) {
        ContactListItem item = new ContactListItem();
        item.update(p);
        listPanel.add(item);

        item.addItemSelectionHandler(new ItemSelectionEventHandler() {
            @Override
            public void onSelect(boolean selected) {
                if (selected) {
                    selection++;
                } else if (selection > 0) {
                    selection--;
                }
            }
        });
        return item;
    }

    private void removeSelectedContacts() {
        if (selection == 0) {
            return;
        }

        ArrayList<Integer> selected = new ArrayList<Integer>();
        int size = listPanel.getWidgetCount();
        for (int i = size - 1; i >=0; i--) {
            ContactListItem item = (ContactListItem) listPanel.getWidget(i);
            if (item.isSelected()) {
                selected.add(i);
            }
        }
        presenter.removeContacts(selected);
        for (Integer i : selected) {
            listPanel.remove(i);
        }
    }
}
