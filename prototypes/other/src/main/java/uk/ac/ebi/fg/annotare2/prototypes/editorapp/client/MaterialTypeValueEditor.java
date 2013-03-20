package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.data.MaterialTypeValue;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.data.SdrfValue;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.data.ValueSource;

/**
 * @author Olga Melnichuk
 */
public class MaterialTypeValueEditor extends Composite implements SdrfCellValueEditor {

    interface Binder extends UiBinder<Widget, MaterialTypeValueEditor> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    ListBox sourceList;

    @UiField
    TextBox valueBox;

    @UiField
    TextBox nameBox;

    private final SdrfSection section;

    private final SdrfColumn column;

    public MaterialTypeValueEditor(String name, SdrfSection section, SdrfColumn column) {
        this.section = section;
        this.column = column;

        initWidget(Binder.BINDER.createAndBindUi(this));
        nameBox.setValue(name);
        valueBox.setValue(name);
        for (ValueSource vs : ValueSource.ALL) {
            sourceList.addItem(vs.getName());
        }
    }

    @Override
    public SdrfValue getValue() {
        return new MaterialTypeValue(
                nameBox.getValue(),
                column,
                section,
                valueBox.getValue(),
                ValueSource.get(sourceList.getValue(sourceList.getSelectedIndex())));
    }

}
