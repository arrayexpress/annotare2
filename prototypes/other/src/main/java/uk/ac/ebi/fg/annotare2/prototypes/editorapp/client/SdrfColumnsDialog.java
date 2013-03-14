package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DialogBox;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.event.DialogCloseEvent;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.event.DialogCloseHandler;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SdrfColumnsDialog extends DialogBox implements HasSelectionHandlers<List<SdrfColumn>> {

    public SdrfColumnsDialog(List<SdrfColumn.Type> columnTypes, List<SdrfColumn> initColumns) {
        setText("Add/Delete Columns");
        setGlassEnabled(true);

        SdrfColumnsDialogContent content = new SdrfColumnsDialogContent(columnTypes, initColumns);
        content.addDialogCloseHandler(new DialogCloseHandler<List<SdrfColumn>>() {
            @Override
            public void onDialogClose(DialogCloseEvent<List<SdrfColumn>> event) {
                if (event.hasResult()) {
                    fireSelectionEvent(event.getTarget());
                }
                hide();
            }
        });
        setWidget(content);
        center();
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<List<SdrfColumn>> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    private void fireSelectionEvent(List<SdrfColumn> selection) {
        SelectionEvent.fire(this, selection);
    }
}
