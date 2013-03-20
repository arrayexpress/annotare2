package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.user.client.ui.DialogBox;

/**
 * @author Olga Melnichuk
 */
public class SdrfCellNewValueDialog extends DialogBox {

    public SdrfCellNewValueDialog(SdrfColumn column, String name) {
        setText("New " + column.getTitle() + " value");
        setModal(true);

        SdrfCellValueEditor content = column.createEditor(name);
        setWidget(content);

        center();
    }

    protected void ok(String result) {
    }

    protected void cancel() {
    }
}
