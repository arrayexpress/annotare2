package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author Olga Melnichuk
 */
public class ContactView extends Composite implements IsWidget {


   DisclosurePanel disclosurePanel = new DisclosurePanel();

   ContactDetails contactDetails = new ContactDetails();

   SortableListHeader header = new SortableListHeader();

    public ContactView() {

        disclosurePanel.setWidth("100%");
        disclosurePanel.addStyleName("my-ContactView");

        disclosurePanel.setHeader(header);
        disclosurePanel.setContent(contactDetails);
        /*disclosurePanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {
            public void onOpen(OpenEvent<DisclosurePanel> event) {
                header.setOpened();
            }
        });

        disclosurePanel.addCloseHandler(new CloseHandler<DisclosurePanel>() {
            public void onClose(CloseEvent<DisclosurePanel> event) {
                header.setClosed();
            }
        });*/

        initWidget(disclosurePanel);
        setFirstName("Test");
        setLastName("Test");
        setInitials("T");
    }

    private void updateHeader() {
       header.setText(contactDetails.getTitle());
    }

    public void setFirstName(String name) {
        contactDetails.setFirstName(name);
        updateHeader();
    }

    public void setLastName(String name) {
        contactDetails.setLastName(name);
        updateHeader();
    }

    public void setInitials(String initials){
        contactDetails.setInitials(initials);
        updateHeader();
    }
}
