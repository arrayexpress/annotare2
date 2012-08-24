package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

/**
 * @author Olga Melnichuk
 */
public class ContactListView extends Composite implements IsWidget {

    @UiConstructor
    public ContactListView() {

        SortableList list = new SortableList();
        list.setWidth("100%");
        list.addWidget(newContact("Iva", "Woody"));
        list.addWidget(newContact("Kelly", "Hanton"));
        list.addWidget(newContact("Hugh", "Zanders"));
        list.addWidget(newContact("Neil", "Brandi"));
        list.addWidget(newContact("Lorrie", "Pascal"));

        initWidget(list);
    }

    private ContactView newContact(String name, String lastName) {
        ContactView v = new ContactView();
        v.setFirstName(name);
        v.setLastName(lastName);
        return v;
    }
}
