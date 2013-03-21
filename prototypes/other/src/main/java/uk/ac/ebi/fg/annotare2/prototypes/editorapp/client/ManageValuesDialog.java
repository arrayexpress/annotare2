package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.data.SdrfData;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.data.SdrfValue;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ManageValuesDialog extends DialogBox {

    public static final String NEW_VALUE = "NEW VALUE";

    @UiField
    ListBox columnList;

    @UiField
    ListBox valueList;

    @UiField
    SimplePanel panel;

    @UiField
    Button cancelButton;

    @UiField
    Button okButton;

    @UiField
    Button addButton;

    @UiField
    Button removeButton;

    @UiField
    Button saveButton;

    interface Binder extends UiBinder<Widget, ManageValuesDialog> {
    }

    private final SdrfSection section;
    private SdrfCellValueEditor editor;
    private boolean hasNew;

    public ManageValuesDialog(final SdrfSection section) {
        setText("Manage Values");
        setModal(true);
        setGlassEnabled(true);

        Binder uiBinder = GWT.create(Binder.class);
        setWidget(uiBinder.createAndBindUi(this));
        center();

        this.section = section;
        columnList.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                showAllValues();
            }
        });

        valueList.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                showValue(getValues().get(valueList.getSelectedIndex()));
            }
        });

        for (SdrfColumn column : section.getColumns()) {
            columnList.addItem(column.getTitle());
        }

        showAllValues();
    }

    private void showAllValues() {
        List<SdrfValue> values = getValues();
        valueList.clear();
        for (SdrfValue v : values) {
            valueList.addItem(v.getName());
        }
        if (values.isEmpty()) {
            showNewValue();
        } else {
            valueList.setItemSelected(0, true);
            DomEvent.fireNativeEvent(Document.get().createChangeEvent(), valueList);
        }
    }

    private List<SdrfValue> getValues() {
        return SdrfData.get().getValuesFor(section, getColumn());
    }

    private void showNewValue() {
        if (!hasNew) {
            hasNew = true;
            valueList.addItem(NEW_VALUE);
            valueList.setItemSelected(valueList.getItemCount() - 1, true);
            showValue(null);
        }
    }

    private void showValue(SdrfValue value) {
        SdrfColumn column = getColumn();
        editor =
                column.createEditor(value, NEW_VALUE, section);
        panel.setWidget(editor);
    }

    private SdrfColumn getColumn() {
        return section.getColumns().get(columnList.getSelectedIndex());
    }

    @UiHandler("saveButton")
    public void saveClick(ClickEvent event) {
        SdrfValue value = editor.getValue();
        int index = valueList.getSelectedIndex();
        SdrfData.get().addOrReplace(valueList.getValue(index), value);
        valueList.setItemText(index, value.getName());
        hasNew = false;
    }

    @UiHandler("addButton")
    public void addClick(ClickEvent event) {
        showNewValue();
    }

    @UiHandler("okButton")
    public void okClick(ClickEvent event) {
        hide();
    }

    @UiHandler("cancelButton")
    public void cancelClick(ClickEvent event) {
        hide();
    }
}
