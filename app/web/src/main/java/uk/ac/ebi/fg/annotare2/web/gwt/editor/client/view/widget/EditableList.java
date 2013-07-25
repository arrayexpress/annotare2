package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class EditableList extends Composite implements HasValue<List<String>>, HasValueChangeHandlers<List<String>> {

    interface Binder extends UiBinder<Widget, EditableList> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    Button removeButton;

    @UiField
    Button addButton;

    @UiField
    ScrollPanel scrollPanel;

    private CellTable<String> table;
    private ListDataProvider<String> dataProvider;

    public EditableList() {
        initWidget(Binder.BINDER.createAndBindUi(this));
        table = createCellTable();
        scrollPanel.add(table);

        dataProvider = new ListDataProvider<String>();
        dataProvider.addDataDisplay(table);
    }

    private CellTable<String> createCellTable() {
        CellTable<String> table = new CellTable<String>();
        TextColumn<String> column = new TextColumn<String>() {
            @Override
            public String getValue(String value) {
                return value;
            }
        };
        column.setFieldUpdater(new FieldUpdater<String, String>() {
            @Override
            public void update(int index, String object, String value) {
                dataProvider.getList().set(index, value);
            }
        });
        table.addColumn(column);
        table.setWidth("100%");
        return table;
    }

    @UiHandler("addButton")
    void addButtonClicked(ClickEvent event) {
        dataProvider.getList().add("");
        //dataProvider.refresh();
    }

    @UiHandler("removeButton")
    void setRemoveButtonClicked(ClickEvent event) {
        //TODO selection model needed
        //dataProvider.getList().remove()
    }

    @Override
    public List<String> getValue() {
        return new ArrayList<String>(dataProvider.getList());
    }

    @Override
    public void setValue(List<String> values) {
        setValue(values, true);
    }

    @Override
    public void setValue(List<String> values, boolean fireEvents) {
        dataProvider.setList(values);
        if (fireEvents) {
            ValueChangeEvent.fire(this, values);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<String>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
