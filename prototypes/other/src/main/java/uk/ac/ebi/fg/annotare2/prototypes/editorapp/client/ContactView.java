package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Olga Melnichuk
 */
public class ContactView extends Composite implements IsWidget {

    interface Binder extends UiBinder<Widget, ContactView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    DisclosurePanel disclosurePanel;

    @UiField
    TextBoxWithPlaceHolder firstNameBox;

    @UiField
    TextBoxWithPlaceHolder initialsBox;

    @UiField
    TextBoxWithPlaceHolder lastNameBox;

    private Header header = new Header();

    @UiConstructor
    public ContactView() {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    private void updateHeader() {
        disclosurePanel.getHeaderTextAccessor().setText(header.toString());
    }

    public void setFirstName(String name) {
        firstNameBox.setValue(name);
        header.setFirstName(name);
        updateHeader();
    }

    public void setLastName(String name) {
        lastNameBox.setValue(name);
        header.setLastName(name);
        updateHeader();
    }

    public void setInitials(String initials){
        initialsBox.setValue(initials);
        header.setInitials(initials);
        updateHeader();
    }

    private static class Header {
        private String firstName = "";
        private String lastName = "";
        private String initials = "";

        @Override
        public String toString() {
            return isEmpty() ? "Empty" : firstName + " " + initials + " " + lastName;
        }

        private boolean isEmpty() {
            return firstName.isEmpty() && lastName.isEmpty() && initials.isEmpty();
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName == null ? "" : firstName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName == null ? "" : lastName;
        }

        public void setInitials(String initials) {
            this.initials = initials == null ? "" : initials;
        }
    }
}
