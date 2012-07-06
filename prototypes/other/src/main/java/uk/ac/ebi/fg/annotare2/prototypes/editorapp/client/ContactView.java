package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.Date;

/**
 * @author Olga Melnichuk
 */
public class ContactView extends Composite implements IsWidget {

    interface Binder extends UiBinder<Widget, ContactView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    Label contactName;

    @UiConstructor
    public ContactView() {
        initWidget(Binder.BINDER.createAndBindUi(this));

        contactName.setText("Contact Name " + new Date().getTime());
    }
}
