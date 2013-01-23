package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.user.client.ui.DialogBox;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITerm;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CloseEventHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ExpDesignTemplatesDialog extends DialogBox {

    private final ExpDesignTemplatesDialogContent content;

    public ExpDesignTemplatesDialog(List<UITerm> templates) {
        setText("Add Experimental Design(s)");
        setGlassEnabled(true);
        setModal(true);

        content = new ExpDesignTemplatesDialogContent(templates);
        content.addCloseHandler(new CloseEventHandler() {
            @Override
            public void onClose() {
                hide();
            }
        });

        setWidget(content);

        center();
    }

    public boolean isCancelled() {
        return content.isCancelled();
    }

    public List<UITerm> getSelection() {
        return content.getSelection();
    }
}
