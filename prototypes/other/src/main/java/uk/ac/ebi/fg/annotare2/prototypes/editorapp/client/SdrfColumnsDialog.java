package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.DialogBox;

/**
 * @author Olga Melnichuk
 */
public class SdrfColumnsDialog extends DialogBox {

    public SdrfColumnsDialog() {
        setText("Add/Delete Columns");
        setGlassEnabled(true);

        SdrfColumnsDialogContent content = new SdrfColumnsDialogContent();
        content.addCloseHandler(new CloseHandler<SdrfColumnsDialogContent>() {
            @Override
            public void onClose(CloseEvent<SdrfColumnsDialogContent> event) {
                hide();
            }
        });
        setWidget(content);
        center();
    }
}
