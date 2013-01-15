package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.user.client.ui.DialogBox;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITermSource;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CloseEventHandler;

import java.util.ArrayList;

/**
 * @author Olga Melnichuk
 */
public class ExpDesignTemplatesDialog extends DialogBox {

    public ExpDesignTemplatesDialog(ArrayList<UITerm> templates) {
        setText("Add Experimental Design(s)");
        setGlassEnabled(true);
        setModal(true);

        ExpDesignTemplatesDialogContent content = new ExpDesignTemplatesDialogContent(templates);
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
        return false; //TODO
    }

    public ArrayList<UITerm> getSelection() {
        return new ArrayList<UITerm>(); //TODO
    }
}
