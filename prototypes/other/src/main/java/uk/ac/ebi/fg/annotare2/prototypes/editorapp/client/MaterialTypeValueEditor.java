package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Olga Melnichuk
 */
public class MaterialTypeValueEditor extends Composite implements SdrfCellValueEditor {

    interface Binder extends UiBinder<Widget, MaterialTypeValueEditor> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public MaterialTypeValueEditor(String name) {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }
}
