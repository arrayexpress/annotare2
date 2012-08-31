package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Olga Melnichuk
 */
public class ContactDetails extends Composite implements IsWidget /*, HasTitle*/ {

    interface Binder extends UiBinder<Widget, ContactDetails> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    TextBoxWithPlaceHolder firstNameBox;

    @UiField
    TextBoxWithPlaceHolder initialsBox;

    @UiField
    TextBoxWithPlaceHolder lastNameBox;

    @UiConstructor
    public ContactDetails() {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    public void setFirstName(String name) {
        firstNameBox.setValue(name);
    }

    public void setLastName(String name) {
        lastNameBox.setValue(name);
    }

    public void setInitials(String initials){
        initialsBox.setValue(initials);
    }

    public String getTitle() {
        return isEmpty() ? "Empty" : firstNameBox.getValue() + " " + initialsBox.getValue() + " " + lastNameBox.getValue();
    }

    private boolean isEmpty() {
        return firstNameBox.getValue().isEmpty()
                && lastNameBox.getValue().isEmpty()
                && initialsBox.getValue().isEmpty();
    }

}
