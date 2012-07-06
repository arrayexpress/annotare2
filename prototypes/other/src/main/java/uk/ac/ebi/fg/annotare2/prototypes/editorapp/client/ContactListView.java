package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Olga Melnichuk
 */
public class ContactListView extends Composite implements IsWidget {

    interface Binder extends UiBinder<Widget, ContactListView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiConstructor
    public ContactListView() {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }
}
