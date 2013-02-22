package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DialogBox;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITerm;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DialogCloseEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DialogCloseHandler;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ExpDesignTemplatesDialog extends DialogBox implements HasSelectionHandlers<List<UITerm>> {

    public ExpDesignTemplatesDialog(List<UITerm> templates) {
        setText("Add Experimental Design(s)");
        setGlassEnabled(true);
        setModal(true);

        ExpDesignTemplatesDialogContent content = new ExpDesignTemplatesDialogContent(templates);
        content.addDialogCloseHandler(new DialogCloseHandler<List<UITerm>>() {
            @Override
            public void onDialogClose(DialogCloseEvent<List<UITerm>> event) {
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
    public HandlerRegistration addSelectionHandler(SelectionHandler<List<UITerm>> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    private void fireSelectionEvent(List<UITerm> selection) {
        SelectionEvent.fire(this, selection);
    }
}
