package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.user.client.ui.DialogBox;

/**
 * @author Olga Melnichuk
 */
public class AddMultipleAssociationsDialog extends DialogBox {

    public AddMultipleAssociationsDialog() {
        setTitle("Add Multiple Associations");
        setGlassEnabled(true);
        setWidget(new AddMultipleAssociationsDialogContent());
        center();
    }
}
