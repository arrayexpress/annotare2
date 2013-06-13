package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Olga Melnichuk
 */
public class EditorTopMenuBar extends Composite {

    interface Binder extends UiBinder<Widget, EditorTopMenuBar> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public EditorTopMenuBar() {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }
}
