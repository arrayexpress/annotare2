package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.resources.EditorResources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

/**
 * @author Olga Melnichuk
 */
public class EditableList extends Composite implements HasValue<List<String>>, HasValueChangeHandlers<List<String>> {

    interface Binder extends UiBinder<Widget, EditableList> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    Button addButton;

    @UiField
    ScrollPanel scrollPanel;

    private CellTable<TextRow> table;
    private ListDataProvider<TextRow> dataProvider;

    public EditableList() {
        initWidget(Binder.BINDER.createAndBindUi(this));
        table = createCellTable();
        scrollPanel.add(table);

        dataProvider = new ListDataProvider<TextRow>();
        dataProvider.addDataDisplay(table);
    }

    private CellTable<TextRow> createCellTable() {
        CellTable<TextRow> table = new CellTable<TextRow>();
        Column<TextRow, String> column = new Column<TextRow, String>(new TextInputCell()) {
            @Override
            public String getValue(TextRow row) {
                return row.getValue();
            }
        };
        column.setFieldUpdater(new FieldUpdater<TextRow, String>() {
            @Override
            public void update(int index, TextRow object, String value) {
                dataProvider.getList().get(index).setValue(value);
                fireValueChangedEvent();
            }
        });
        table.addColumn(column);

        Column<TextRow, ImageResource> removeColumn = new Column<TextRow, ImageResource>(new ClickableImageResourceCell()) {
            @Override
            public ImageResource getValue(TextRow object) {
                return  EditorResources.EDITOR_RESOURCES.cancelIcon();
            }
        };
        removeColumn.setFieldUpdater(new FieldUpdater<TextRow, ImageResource>() {
            @Override
            public void update(int index, TextRow object, ImageResource value) {
                 dataProvider.getList().remove(index);
                fireValueChangedEvent();
            }
        });
        table.addColumn(removeColumn);
        table.setColumnWidth(removeColumn, "25px");

        table.setWidth("100%");
        table.setEmptyTableWidget(new Label("Empty"));
        return table;
    }

    @UiHandler("addButton")
    void addButtonClicked(ClickEvent event) {
        dataProvider.getList().add(new TextRow(""));
    }

    @Override
    public List<String> getValue() {
        List<String> value = new ArrayList<String>();
        for (TextRow row : dataProvider.getList()) {
            if (!row.isEmpty()) {
                value.add(row.getValue());
            }
        }
        return value;
    }

    @Override
    public void setValue(List<String> values) {
        setValue(values, true);
    }

    @Override
    public void setValue(List<String> values, boolean fireEvents) {
        dataProvider.setList(asRows(values));
        if (fireEvents) {
            fireValueChangedEvent();
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<String>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    private void fireValueChangedEvent() {
        ValueChangeEvent.fire(this, getValue());
    }

    private List<TextRow> asRows(List<String> values) {
        List<TextRow> rows = new ArrayList<TextRow>();
        for (String v : values) {
            rows.add(new TextRow(v));
        }
        return rows;
    }

    private static class TextRow {
        private String value;

        private TextRow(String value) {
            this.value = value;
        }

        public String getValue() {
            return value == null ? null : value.trim();
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isEmpty() {
            return value == null || value.trim().isEmpty();
        }
    }

    private class ClickableImageResourceCell extends ImageResourceCell {

        @Override
        public Set<String> getConsumedEvents() {
            Set<String> consumedEvents = new HashSet<String>();
            consumedEvents.add(BrowserEvents.CLICK);
            return consumedEvents;
        }

        @Override
        public void onBrowserEvent(Context context, Element parent, ImageResource value, NativeEvent event,
                                   ValueUpdater<ImageResource> valueUpdater) {

            if (CLICK.equals(event.getType())) {
                EventTarget eventTarget = event.getEventTarget();
                if (!Element.is(eventTarget)) {
                    return;
                }
                if (parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget))) {
                    // Ignore clicks that occur outside of the main element.
                    onEnterKeyDown(context, parent, value, event, valueUpdater);
                }
            }
        }

        @Override
        protected void onEnterKeyDown(Context context, Element parent, ImageResource value,
                                      NativeEvent event, ValueUpdater<ImageResource> valueUpdater) {
            if (valueUpdater != null) {
                valueUpdater.update(value);
            }
        }
    }

}
