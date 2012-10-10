package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Olga Melnichuk
 */
public class SortableListHeader extends Composite {

    interface Binder extends UiBinder<Widget, SortableListHeader> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    Label label;


    public SortableListHeader() {
        initWidget(Binder.BINDER.createAndBindUi(this));


    }

    public void setOpened() {


    }

    public void setClosed() {


    }

    public void setText(String title) {
        label.setText(title);
    }

    @UiFactory
    public EditorClientBundle getResources() {
        EditorClientBundle.INSTANCE.moreStyles().ensureInjected();
        return EditorClientBundle.INSTANCE;
    }
}
