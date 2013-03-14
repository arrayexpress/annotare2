package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SdrfColumnsDialogContent extends Composite implements IsWidget, HasCloseHandlers<SdrfColumnsDialogContent> {

    public static final String NO_NAME = "NO NAME";

    @UiField
    Button okButton;

    @UiField
    Button cancelButton;

    @UiField(provided = true)
    ValueListBox<SdrfColumn.Type> columnTypeList;

    @UiField
    ListBox columnList;

    @UiField
    Button moveDownButton;

    @UiField
    Button moveUpButton;

    @UiField
    Button removeButton;

    @UiField
    Button addButton;

    @UiField
    TextBox columnKey;

    private final List<SdrfColumn> columns = new ArrayList<SdrfColumn>();

    interface Binder extends UiBinder<Widget, SdrfColumnsDialogContent> {
    }

    public SdrfColumnsDialogContent(List<SdrfColumn.Type> columnTypes) {
        columnTypeList = new ValueListBox<SdrfColumn.Type>(new Renderer<SdrfColumn.Type>() {
            @Override
            public String render(SdrfColumn.Type object) {
                return object == null ? "null" : object.getTitle();
            }

            @Override
            public void render(SdrfColumn.Type object, Appendable appendable) throws IOException {
                appendable.append(render(object));
            }
        });

        columnTypeList.setValue(columnTypes.isEmpty() ? null : columnTypes.get(0));
        columnTypeList.setAcceptableValues(columnTypes);

        Binder uiBinder = GWT.create(Binder.class);
        Widget widget = uiBinder.createAndBindUi(this);
        initWidget(widget);
    }

    @UiHandler("columnKey")
    public void columnKeyChange(KeyUpEvent event) {
        int idx = columnList.getSelectedIndex();
        if (idx < 0) {
            return;
        }
        String value = (columnKey.getValue()).trim();
        if (value.isEmpty()) {
            value = NO_NAME;
        }
        SdrfColumn col = columns.get(idx);
        col.setKey(value);
        columnList.setItemText(idx, col.getTitle());
    }

    @UiHandler("columnList")
    public void columnListChange(ChangeEvent event) {
        int idx = columnList.getSelectedIndex();
        if (idx < 0) {
            return;
        }
        SdrfColumn col = columns.get(idx);
        if (col.getType().requiresKey()) {
            columnKey.setValue(NO_NAME.equals(col.getKey()) ? "" : col.getKey());
            columnKey.setEnabled(true);
        } else {
            columnKey.setText("");
            columnKey.setEnabled(false);
        }
    }

    @UiHandler("addButton")
    public void addButtonClick(ClickEvent event) {
        SdrfColumn.Type type = columnTypeList.getValue();
        SdrfColumn column = new SdrfColumn(type, NO_NAME);
        for (SdrfColumn c : columns) {
            if (c.equals(column)) {
                return;
            }
        }
        columns.add(column);
        showColumns(columns);

        // select new column
        columnList.setItemSelected(columnList.getItemCount() - 1, true);
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), columnList);
    }

    @UiHandler("removeButton")
    public void removeButtonClick(ClickEvent event) {
        int idx = columnList.getSelectedIndex();
        if (idx >= 0) {
            columns.remove(idx);
            columnList.removeItem(idx);
        }
    }

    @UiHandler("moveDownButton")
    public void moveUpButtonClick(ClickEvent event) {
        //TODO
    }

    @UiHandler("moveDownButton")
    public void moveDownButtonClicked(ClickEvent event) {
        //TODO
    }

    @UiHandler("okButton")
    public void okButtonClick(ClickEvent event) {
        close();
    }

    @UiHandler("cancelButton")
    public void cancelButtonClick(ClickEvent event) {
        close();
    }

    private void showColumns(List<SdrfColumn> columns) {
        columnList.clear();
        for (SdrfColumn column : columns) {
            columnList.addItem(column.getTitle());
        }
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<SdrfColumnsDialogContent> handler) {
        return addHandler(handler, CloseEvent.getType());
    }

    public void close() {
        CloseEvent.fire(this, this);
    }
}
